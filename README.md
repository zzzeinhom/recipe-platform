# 🍽️ Recipe Sharing Platform

A full-stack backend REST API for a recipe sharing platform built with **Spring Boot**, supporting user authentication, chef profiles, recipes, ratings, favorites, statistics, and JWT-based security.

This project is designed with **clean architecture**, **role-based authorization**, and **Docker support** for easy local and production deployment.

---

## 🚀 Features

### 👤 Authentication & Authorization
- JWT-based authentication
- Role-based access control (**USER / CHEF**)
- Secure signup & login
- Protected endpoints

### 👨‍🍳 User & Chef System
- Users can register as regular users or chefs
- Unified profile system
- Chef-owned recipes

### 🍲 Recipes
- Create, update, delete recipes (Chef only)
- Public recipe browsing
- Recipe details endpoint
- Pagination support

### ⭐ Ratings & Reviews
- Authenticated users can rate recipes
- One rating per user per recipe
- Update & delete own ratings only
- Average rating & count auto-calculated

### ❤️ Favorites
- Add / remove favorite recipes
- View personal favorites list
- Favorite count maintained automatically

### 📊 Recipe Statistics
- View counter (unique per user)
- Favorites count
- Ratings count
- Average rating

### 📘 API Documentation
- Swagger UI (OpenAPI)
- JWT authorization support
- Endpoint grouping
- Public vs secured endpoints clearly marked

---

## 🧱 Architecture Overview

```
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

**Key design principles:**
- DTO-based API
- No entity exposure
- Transactional services
- Centralized exception handling
- Clean separation of concerns

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security + JWT |
| Database | MySQL 8 |
| ORM | Spring Data JPA |
| Migration | Flyway |
| Documentation | Springdoc OpenAPI |
| Containerization | Docker & Docker Compose |

---

## 🔐 Security Model

### Roles
- `USER` - Can view, rate, and favorite recipes
- `CHEF` - All user permissions + create/manage recipes

### Access Rules

| Endpoint | Access |
|----------|--------|
| View recipes | Public |
| Rate recipe | Authenticated |
| Favorite recipe | Authenticated |
| Create recipe | CHEF only |
| Update/delete own resources | Owner only |

**JWT token structure:**
```json
{
  "sub": "username",
  "roles": ["ROLE_USER"]
}
```

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose (recommended)
- **OR** Java 21 + MySQL 8 (for local development)

### 🐳 Quick Start with Docker (Recommended)

**Step 1 — Clone repository**
```bash
git clone https://github.com/zzzeinhom/recipe-platform.git
cd recipe-platform
```

**Step 2 — Create `.env` file**
```env
# Database
DB_USERNAME=rp_user
DB_PASSWORD=rp_password
DB_NAME=recipe_platform

# JWT
JWT_SECRET=your-super-secret-key-change-this-in-production

# File storage
FILE_STORAGE_LOCATION=/uploads
FILE_STORAGE_BASE_URL=http://localhost:8080/files
```

**Step 3 — Start containers**
```bash
docker compose up --build
```

Docker will start:
- MySQL container
- Spring Boot API container
- Shared network
- Persistent database volume

**Step 4 — Access the application**
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html

---

## 📘 API Documentation

After running the app, access Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

### Using JWT Authorization in Swagger

1. **Register/Login** → Get JWT token from response
2. Click **Authorize** button (top right)
3. Enter: `Bearer YOUR_TOKEN`
4. All secured endpoints are now accessible

---

## ⚙️ Configuration

### Environment Variables

The application uses environment variables (configured via `.env` file):

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | `rp_user` |
| `DB_PASSWORD` | Database password | `rp_password` |
| `DB_NAME` | Database name | `recipe_platform` |
| `JWT_SECRET` | Secret key for JWT signing | `your-secret-key` |
| `FILE_STORAGE_LOCATION` | Path for uploaded files | `/uploads` |
| `FILE_STORAGE_BASE_URL` | Base URL for file access | `http://localhost:8080/files` |

### Docker Networking Notes

- MySQL hostname inside Docker: `mysql`
- Database URL (internal): `jdbc:mysql://mysql:3306/recipe_platform`
- **Important:** Do not use `localhost` for inter-container communication

---

## 📂 Project Structure

```
src/
├── config/              # Application configuration
├── security/            # JWT & security config
├── controller/          # REST endpoints
├── service/             # Business logic
├── repository/          # Data access layer
├── entity/              # JPA entities
├── dto/                 # Data transfer objects
├── mapper/              # Entity-DTO mappers
└── exception/           # Exception handling
```

---

## 🧪 Testing

Testing support includes:
- Unit tests (Service layer)
- Integration tests (Controller layer)
- Security testing with JWT
- Repository tests

**Run tests:**
```bash
./mvnw test
```

---

## 🔄 Local Development (Without Docker)

**Prerequisites:**
- Java 21
- MySQL 8 running on localhost:3306

**Steps:**

1. **Create database**
```sql
CREATE DATABASE recipe_platform;
CREATE USER 'rp_user'@'localhost' IDENTIFIED BY 'rp_password';
GRANT ALL PRIVILEGES ON recipe_platform.* TO 'rp_user'@'localhost';
```

2. **Configure `application.properties`** with local settings

3. **Run application**
```bash
./mvnw spring-boot:run
```

---

## ✅ Production-Ready Features

- ✅ Stateless JWT authentication
- ✅ Security filter chain
- ✅ Clean REST API design
- ✅ DTO mapping pattern
- ✅ Transaction management
- ✅ Pagination support
- ✅ Role-based authorization
- ✅ Dockerized environment
- ✅ Database migrations (Flyway)
- ✅ API documentation (Swagger)

---

## 📌 Future Enhancements

- [ ] Refresh token mechanism
- [ ] Admin role & dashboard
- [ ] Recipe comments system
- [ ] Advanced search & filtering
- [ ] Cloud file storage (AWS S3)
- [ ] Email notifications
- [ ] Rate limiting
- [ ] CI/CD pipeline
- [ ] Comprehensive test coverage
- [ ] Performance monitoring

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 👨‍💻 Author

**Mohammad Zeinhom**  
Backend Developer  
Java • Spring Boot • REST APIs • Docker

📧 [Email](zzzeinhom@gmail.com) | 💼 [LinkedIn](https://linkedin.com/in/zzzeinhom) | 🐙 [GitHub](https://github.com/zzzeinhom)

---

## ⭐ Support

If you find this project helpful, please consider:
- Giving it a ⭐ star on GitHub
- Sharing it with others
- Contributing to its development