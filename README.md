# AlfaInsta

Rede social desenvolvida com Spring Boot, inspirada no Instagram/Twitter, com funcionalidades de posts, chat em tempo real, sistema de follow, notificações e autenticação JWT + OAuth2 (Google).

## 🚀 Funcionalidades

- 🔐 **Autenticação JWT** — Registro, login e recuperação de senha por email
- 🌐 **Login social** — OAuth2 com Google
- 📝 **Posts** — Criar, curtir, comentar e repostar
- 👥 **Sistema de Follow** — Seguir/deixar de seguir usuários
- 💬 **Chat em tempo real** — Mensagens via WebSocket (STOMP + SockJS)
- 🔔 **Notificações** — Likes, follows, comentários e reposts
- 🖼️ **Upload de imagens** — Com redimensionamento e compressão automática
- 🔍 **Feed personalizado** — Feed de quem você segue + aba "Explore"
- ✉️ **Email** — Envio de email para reset de senha

## 🛠️ Tecnologias

- **Java 17**
- **Spring Boot 3.1.5**
  - Spring Web
  - Spring Security
  - Spring Data JPA
  - Spring WebSocket
  - Spring Mail
  - Spring Validation
- **PostgreSQL** — Banco de dados
- **JWT (jjwt 0.11.5)** — Autenticação stateless
- **OAuth2** — Login com Google
- **Lombok** — Redução de boilerplate
- **Maven** — Gerenciamento de dependências

## 📋 Pré-requisitos

- Java 17+
- Maven 3.9+
- PostgreSQL 14+
- Conta Google Cloud (para OAuth2)
- Conta SMTP (para envio de email)

## ⚙️ Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/alfainsta.git
cd alfainsta
```

### 2. Crie o banco no PostgreSQL

```sql
CREATE DATABASE alfaProject;
```

### 3. Configure o `application.properties`

Crie/edite o arquivo `src/main/resources/application.properties`:

```properties
# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/alfaProject
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# JWT
jwt.secret=SUA_CHAVE_SECRETA_COM_PELO_MENOS_32_CARACTERES
jwt.expiration=3600000

# Email (Gmail SMTP)
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-de-app

# Upload de arquivos
upload.dir=uploads
```

> ⚠️ **Importante:** Nunca commite credenciais reais. Use variáveis de ambiente em produção.

### 4. Rode o projeto

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

## 📡 Principais Endpoints

### Autenticação (`/api/auth`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/register` | Registrar novo usuário |
| POST | `/login` | Login com username/senha |
| POST | `/oauth/google` | Login com Google |
| POST | `/forgot-password` | Solicitar reset de senha |
| POST | `/reset-password` | Redefinir senha com token |

### Posts (`/api/post`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/` | Criar post |
| GET | `/feed` | Feed de quem você segue |
| GET | `/explore` | Posts de outros usuários |
| GET | `/my` | Meus posts |
| GET | `/{postId}` | Buscar post por ID |
| POST | `/{postId}/like` | Curtir/descurtir |
| POST | `/{postId}/comment` | Comentar |
| POST | `/{postId}/repost` | Repostar |

### Usuários (`/api/users`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/me` | Meu perfil |
| GET | `/username/{username}` | Buscar por username |
| PUT | `/me` | Atualizar perfil |
| POST | `/upload-profile-picture` | Upload foto de perfil |
| POST | `/upload-cover` | Upload foto de capa |

### Follow (`/api/follow`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/{userId}` | Seguir usuário |
| DELETE | `/{userId}` | Deixar de seguir |
| GET | `/{userId}/stats` | Contagem de seguidores/seguindo |
| GET | `/{userId}/followers` | Lista de seguidores |
| GET | `/{userId}/following` | Lista de seguindo |

### Chat (`/api/chat`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/send` | Enviar mensagem |
| GET | `/conversation/{otherId}` | Buscar conversa |
| GET | `/unread-count` | Mensagens não lidas |

### Notificações (`/api/notifications`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/` | Listar notificações |
| GET | `/unread-count` | Contagem não lidas |
| POST | `/mark-read` | Marcar todas como lidas |

### WebSocket

- **Endpoint:** `/ws` (com SockJS)
- **Tópicos:** `/topic`, `/queue`
- **Prefixo de aplicação:** `/app`
- **Autenticação:** Header `Authorization: Bearer <token>` no CONNECT

## 📁 Estrutura do Projeto

```
src/main/java/AlfaInsta/demo/
├── config/          # Configurações (Security, CORS, WebSocket)
├── controller/      # Endpoints REST
├── dto/             # Data Transfer Objects
├── exception/       # Tratamento global de exceções
├── model/           # Entidades JPA
├── repository/      # Repositórios Spring Data
├── security/        # JWT (filter e utils)
└── service/         # Lógica de negócio
```

## 🧪 Testes

```bash
./mvnw test
```

## 👥 Autores

Projeto desenvolvido por:

- **Vitor Gabriel**
- **João Pedro**
- **Pedro**

---

📌 Projeto desenvolvido para fins de aprendizado e portfólio.
