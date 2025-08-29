Upload de Imagens + IA (Cloudinary, RabbitMQ, Groq, Postgres)

Fluxo: upload → Cloudinary → fila RabbitMQ → Groq API (enriquecimento de texto) → PostgreSQL.

1) Produção (Heroku)

Swagger (PROD):
https://mmcafe-upload-ia-cd915bd02d9e.herokuapp.com/swagger-ui/index.html

2) Subir tudo em DEV (Docker)

Crie um arquivo .env.dev na raiz:

# porta interna da app
SERVER_PORT=8083

# Postgres (container db)
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/upload_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# RabbitMQ (container rabbitmq)
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# Cloudinary (formato único)
CLOUDINARY_URL=cloudinary://<API_KEY>:<API_SECRET>@<CLOUD_NAME>

# Groq
GROQ_API_KEY=xxxxxxxxxxxxxxxx


Suba tudo (app + db + rabbit), em segundo plano, lendo o .env.dev:

docker compose --env-file .env.dev up -d --build


Comandos úteis:

# logs da app
docker compose --env-file .env.dev logs -f app

# parar tudo e remover volumes do Postgres
docker compose --env-file .env.dev down -v


Acesso rápido (DEV):

App: http://localhost:8083

Swagger: http://localhost:8083/swagger-ui/index.html

RabbitMQ UI: http://localhost:15672
(guest/guest)

Postgres: localhost:5432 (db: upload_db, user: postgres, pass: postgres)