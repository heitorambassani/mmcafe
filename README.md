# Upload de Imagens com Enriquecimento de Texto via IA

Este projeto implementa um fluxo completo de **upload de imagens**, armazenamento no **Cloudinary**,
envio de metadados para **RabbitMQ**, enriquecimento de texto via **Groq API** e
persistência em **PostgreSQL**. Abaixo estão **todas as informações de infraestrutura** –
incluindo **senha do banco**, como **subir o banco** sozinho e como rodar local/dev vs Docker.

---

## Credenciais do Banco de Dados (default)

> Em produção use variáveis de ambiente ou um cofre. Estes valores são **apenas para dev**.

| Item                  | Valor default  |
|-----------------------|----------------|
| **Host (Docker)**     | `db` (dentro do compose) |
| **Host (Local/dev)**  | `localhost` (via porta exposta) |
| **Porta**             | `5432` |
| **Database**          | `upload_db` |
| **Usuário**           | `postgres` |
| **Senha**             | `postgres` |

No Spring Boot, defina (conforme o profile):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/upload_db   # para app rodando LOCAL
    username: postgres
    password: postgres
```

Quando a aplicação roda **dentro do Docker Compose**, o host muda para o **nome do serviço**:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db:5432/upload_db          # para app rodando NO CONTAINER
    username: postgres
    password: postgres
```

---

## Como subir **APENAS o PostgreSQL** (para testar local)

Se você quer **debugar a aplicação local** (profile `dev`) mas usar o Postgres do Docker:

1) Suba apenas o serviço `db` do compose:
```bash
docker compose up -d db
```

2) Aguarde o healthcheck ficar **healthy**:
```bash
docker compose ps
```

3) Verifique se está ouvindo em `localhost:5432`:
```bash
docker logs -f postgres-db
```

4) Configure o **`application-dev.yml`** para apontar para **localhost:5432** (já exposto pelo compose):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/upload_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
```

> Dica: Se estiver no **WSL2/Linux** e rodando postgres em **Docker Desktop no Windows**, `localhost` também funciona porque a porta foi publicada (`- "5432:5432"`).

---

## Criando banco/usuário manualmente (opcional)

O compose já cria tudo via variáveis de ambiente, mas se quiser criar manualmente:

```bash
# Entrar no container do Postgres
docker exec -it postgres-db psql -U postgres

-- Dentro do psql:
CREATE DATABASE upload_db;
CREATE USER postgres WITH PASSWORD 'postgres'; -- (já existe no compose)
GRANT ALL PRIVILEGES ON DATABASE upload_db TO postgres;
\q
```

> Por padrão do compose, o DB `upload_db` e o usuário `postgres/postgres` já vêm prontos.

---

## Como subir **APENAS o RabbitMQ**

Para testar filas localmente com sua app rodando no IntelliJ (profile `dev`):

```bash
docker compose up -d rabbitmq
```

A UI do RabbitMQ ficará em: http://localhost:15672 (user: `guest`, pass: `guest`).  
A conexão AMQP é em `amqp://guest:guest@localhost:5672`.

No `application-dev.yml`:

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

---

## Executar **tudo** com Docker

```bash
docker compose up --build
```

Serviços disponíveis:
- **Aplicação** → http://localhost:8082
- **Swagger/OpenAPI** → http://localhost:8082/swagger-ui.html
- **RabbitMQ Management** → http://localhost:15672 (guest/guest)
- **PostgreSQL** → `localhost:5432` (postgres/postgres, db: upload_db)

---

## Rodar local (debug) usando Postgres & Rabbit do Docker

1. **Suba** `db` e `rabbitmq`:
   ```bash
   docker compose up -d db rabbitmq
   ```
2. **Selecione o profile `dev`** no IntelliJ/VSCode (ou `SPRING_PROFILES_ACTIVE=dev`).
3. Garanta que seu `application-dev.yml` **não usa H2** e está com `localhost:5432` e `localhost:5672`.
4. **Run/Debug** a classe `UploadImageIaApplication` localmente.

> Isso evita conflito de portas com a app no Docker (lá usamos `8082`; local use `8081` se preferir).

---

## Troubleshooting

- **`Failed to determine a suitable driver class`**  
  Falta o driver do PostgreSQL no `pom.xml` **ou** a URL/usuário/senha do datasource.  
  Confirme que o profile ativo tem `spring.datasource.url/username/password` e que o driver está declarado:
  ```xml
  <dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
  </dependency>
  ```

- **App local não conecta ao DB do Docker**  
  Verifique se a porta `5432` está publicada no compose e use `localhost` no `application-dev.yml`.

- **`Connection refused` no Rabbit**  
  Suba o serviço `rabbitmq` ou ajuste host/porta. Verifique `docker compose ps` e os logs.

---

## Endpoints principais

- **Swagger UI**: `http://localhost:8082/swagger-ui.html` (Docker) ou `http://localhost:8081/swagger-ui.html` (local/dev)
- **POST /upload**: envio multipart (imagem + metadados), publica mensagem no Rabbit.
- **GET /imagens**: lista registros persistidos (se implementado).

---

