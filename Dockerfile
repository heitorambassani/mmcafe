# ===========================
# Etapa de build
# ===========================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copia o wrapper e arquivos de configuração antes do código fonte (para aproveitar cache)
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Permite execução do wrapper no Linux/Windows
RUN chmod +x mvnw || true

# Garante o cache de dependências usando o volume externo .m2 (ver docker-compose abaixo)
RUN ./mvnw -B dependency:resolve

# Copia o código-fonte após dependências
COPY src ./src

# Compila e empacota o projeto (executa testes por padrão — remova se quiser skip)
RUN ./mvnw -B clean package --no-transfer-progress

# Move o JAR final
RUN JAR_FILE=$(ls target/*.jar | grep -v '\.original$') && \
    mv "$JAR_FILE" /app/app.jar

# ===========================
# Etapa de runtime
# ===========================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/app.jar /app/app.jar

ENV SERVER_PORT=8082 \
    SPRING_PROFILES_ACTIVE=docker \
    JAVA_OPTS=""

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${SERVER_PORT} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar /app/app.jar"]
