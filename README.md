# Task Manager API

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)
![Docker](https://img.shields.io/badge/Docker-ready-blue)
![CI/CD](https://img.shields.io/badge/CI/CD-GitHub%20Actions-blue)
![Quality](https://img.shields.io/badge/Quality-SonarQube-green)

A production-ready Spring Boot Task Manager REST API with JWT Authentication, comprehensive testing, and CI/CD pipeline.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Development](#local-development)
  - [Docker](#docker)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [CI/CD Pipeline](#cicd-pipeline)
- [Security](#security)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

- **User Authentication**: JWT-based authentication with role-based authorization
- **Task Management**: Full CRUD operations for tasks
- **Task Status Workflow**: PENDING → IN_PROGRESS → COMPLETED
- **User Assignment**: Assign tasks to users
- **RESTful API**: Well-structured REST endpoints
- **API Documentation**: OpenAPI 3.0 (Swagger UI)
- **Code Quality**: Checkstyle, PMD, SpotBugs integration
- **Test Coverage**: JaCoCo code coverage reporting
- **Security Scans**: OWASP Dependency Check, Trivy, GitLeaks
- **Docker Support**: Multi-stage Dockerfile with BuildKit
- **CI/CD**: GitHub Actions pipeline

## 🛠 Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.1 |
| **Database** | MySQL 8.0, H2 (tests) |
| **Security** | Spring Security, JWT (jjwt) |
| **API Docs** | SpringDoc OpenAPI |
| **Build Tool** | Maven |
| **Testing** | JUnit 5, TestContainers, MockMvc |
| **CI/CD** | GitHub Actions |
| **Code Quality** | Checkstyle, PMD, SpotBugs, JaCoCo |
| **Security** | OWASP Dependency Check, Trivy, GitLeaks |
| **Container** | Docker, Docker Compose |

## 📂 Project Structure

```
taskmanager/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/
│   │   │   ├── TaskManagerApplication.java
│   │   │   ├── application/
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── mapper/        # DTO mappers
│   │   │   │   └── service/       # Business logic
│   │   │   ├── domain/
│   │   │   │   ├── exception/     # Custom exceptions
│   │   │   │   ├── model/         # Entities
│   │   │   │   └── repository/   # Data access
│   │   │   ├── infrastructure/
│   │   │   │   ├── config/        # Configuration classes
│   │   │   │   └── security/      # Security components
│   │   │   └── presentation/
│   │   │       ├── controller/    # REST controllers
│   │   │       └── handler/       # Exception handlers
│   │   └── resources/
│   │       ├── application.yml    # Main config
│   │       └── application-test.yml
│   └── test/
│       └── java/com/taskmanager/  # Test classes
├── .github/
│   └── workflows/
│       └── ci-cd.yml             # CI/CD pipeline
├── docs/                          # Documentation
├── docker-compose.yml             # Docker Compose
├── Dockerfile                     # Docker image
├── pom.xml                        # Maven config
├── checkstyle.xml                 # Checkstyle rules
├── pmd-ruleset.xml                # PMD rules
└── sonar-project.properties       # SonarQube config
```

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- MySQL 8.0 (optional for local dev)

### Local Development

#### Option 1: Run with Maven

```bash
# Clone the repository
git clone https://github.com/kamgaramos/taskmanager.git
cd taskmanager

# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

#### Option 2: Run with MySQL

```bash
# Create MySQL database
mysql -u root -p -e "CREATE DATABASE taskmanager;"

# Update application.yml with your MySQL credentials
# Run the application
./mvnw spring-boot:run
```

### Docker

#### Build and Run with Docker Compose

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

#### Manual Docker Build

```bash
# Build the image
docker build -t taskmanager:latest .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/taskmanager \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  taskmanager:latest
```

## 📚 API Documentation

Once the application is running, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login and get JWT |

### Task Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/tasks` | Get all tasks | ✅ |
| GET | `/api/v1/tasks/{id}` | Get task by ID | ✅ |
| POST | `/api/v1/tasks` | Create task | ✅ |
| PUT | `/api/v1/tasks/{id}` | Update task | ✅ |
| DELETE | `/api/v1/tasks/{id}` | Delete task | ✅ |
| PATCH | `/api/v1/tasks/{id}/status` | Update status | ✅ |
| POST | `/api/v1/tasks/{id}/assign` | Assign user | ✅ |

### Example: Register User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

### Example: Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

### Example: Get Tasks (with JWT)

```bash
curl -X GET http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

## 🧪 Testing

### Run All Tests

```bash
./mvnw test
```

### Run Unit Tests Only

```bash
./mvnw test -DskipIntegrationTests
```

### Generate Coverage Report

```bash
./mvnw jacoco:report
```

View report at: `target/site/jacoco/index.html`

### Run with TestContainers

```bash
./mvnw verify
```

## 🔄 CI/CD Pipeline

The project uses GitHub Actions for continuous integration and deployment.

### Pipeline Stages

1. **Build** - Compile code, build JAR
2. **Code Quality** - Checkstyle, PMD, SpotBugs
3. **Tests** - Unit & integration tests with JaCoCo
4. **SonarQube** - Static analysis
5. **Security** - OWASP Dependency Check, Trivy, GitLeaks
6. **Docker** - Build and push image to GHCR
7. **Deploy** - Deploy to staging/production

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `SONAR_HOST_URL` | SonarCloud URL (https://sonarcloud.io) |
| `SONAR_TOKEN` | SonarCloud authentication token |
| `DISCORD_WEBHOOK` | Discord webhook for notifications (optional) |

### Trigger Pipeline

```bash
# Make a change and push
git add .
git commit -m "Your commit message"
git push origin main
```

### Manual Deployment

Go to GitHub Actions → CI/CD Pipeline → Run workflow

## 🔐 Security

### JWT Configuration

Default JWT settings (customize in `application.yml`):

- Token expiration: 86400000 ms (24 hours)
- Algorithm: HS256
- Secret key: Base64 encoded (change in production!)

### Password Requirements

- Minimum 8 characters
- At least one uppercase letter
- At least one digit
- At least one special character

### Role-Based Access

| Role | Permissions |
|------|-------------|
| ADMIN | Full access to all resources |
| USER | Manage own tasks |

## ⚙️ Configuration

### Application Properties

Key configurations in `application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskmanager
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

app:
  jwt:
    secret: your-secret-key-here
    expiration: 86400000

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | dev |
| `SPRING_DATASOURCE_URL` | Database URL | jdbc:mysql://localhost:3306/taskmanager |
| `SPRING_DATASOURCE_USERNAME` | DB username | root |
| `SPRING_DATASOURCE_PASSWORD` | DB password | password |
| `APP_JWT_SECRET` | JWT secret key | (generated) |
| `APP_JWT_EXPIRATION` | Token expiration (ms) | 86400000 |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📊 Quality Metrics

| Metric | Target |
|--------|--------|
| Code Coverage | > 70% |
| Build Success | 100% |
| Critical Vulnerabilities | 0 |
| Code Smells | < 100 |

---

<p align="center">
  Built with ❤️ by <a href="https://github.com/kamgaramos">kamgaramos</a>
</p>

