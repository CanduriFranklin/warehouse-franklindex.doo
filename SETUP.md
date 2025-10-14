# 🚀 Quick Start Guide - Local Development

## Prerequisites

- Java 25 (IBM Semeru Runtime or equivalent)
- PostgreSQL 15+
- RabbitMQ 3.12+
- Docker (optional, for databases)

## 🔒 Step 1: Configure Environment Variables

### Copy Environment Template

```bash
cp .env.example .env
```

### Generate JWT Secret

Choose one method:

**OpenSSL (Recommended):**
```bash
openssl rand -base64 64
```

**Node.js:**
```bash
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

**Python:**
```bash
python3 -c "import secrets; print(secrets.token_urlsafe(64))"
```

### Edit `.env` File

Open `.env` and replace the values:

```bash
# JWT Secret (replace with generated value above)
JWT_SECRET=your-generated-secret-here

# Database credentials (replace with your PostgreSQL credentials)
DB_PASSWORD=your-secure-password

# Development user passwords (replace with strong passwords)
DEV_ADMIN_PASSWORD=YourStrongAdminPassword123!
DEV_MANAGER_PASSWORD=YourStrongManagerPassword123!
DEV_SALES_PASSWORD=YourStrongSalesPassword123!
```

## 📦 Step 2: Start Required Services

### Option A: Using Docker Compose (Recommended)

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: warehouse-postgres
    environment:
      POSTGRES_DB: warehouse_db_dev
      POSTGRES_USER: warehouse_user
      POSTGRES_PASSWORD: your-secure-password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    container_name: warehouse-rabbitmq
    ports:
      - "5672:5672"   # AMQP
      - "15672:15672" # Management UI
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest

volumes:
  postgres_data:
```

Start services:

```bash
docker-compose up -d
```

### Option B: Local Installation

**PostgreSQL:**
```bash
# Ubuntu/Debian
sudo apt install postgresql-15

# Create database
sudo -u postgres createdb warehouse_db_dev
sudo -u postgres psql -c "CREATE USER warehouse_user WITH PASSWORD 'your-password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE warehouse_db_dev TO warehouse_user;"
```

**RabbitMQ:**
```bash
# Ubuntu/Debian
sudo apt install rabbitmq-server
sudo systemctl enable rabbitmq-server
sudo systemctl start rabbitmq-server

# Enable management plugin
sudo rabbitmq-plugins enable rabbitmq_management
```

## ⚙️ Step 3: Load Environment Variables

### Linux/Mac

**Option A: Export variables:**
```bash
export $(cat .env | xargs)
```

**Option B: Use direnv (recommended):**
```bash
# Install direnv
sudo apt install direnv  # Ubuntu/Debian
brew install direnv      # macOS

# Add to ~/.bashrc or ~/.zshrc
eval "$(direnv hook bash)"  # or zsh

# Allow .envrc
echo "dotenv" > .envrc
direnv allow
```

### Windows (PowerShell)

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#][^=]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}
```

## 🏗️ Step 4: Build and Run

### Build

```bash
./gradlew clean build
```

### Run

```bash
./gradlew bootRun
```

Or with explicit environment file:

```bash
./gradlew bootRun --args='--spring.config.additional-location=file:.env'
```

## ✅ Step 5: Verify Installation

### Check Health Endpoint

```bash
curl http://localhost:8080/api/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

### Test Authentication

**Login as Admin:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "DevAdmin123!"
  }'
```

Expected response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "expiresAt": "2025-10-15T...",
  "username": "admin",
  "roles": ["ROLE_ADMIN", "ROLE_WAREHOUSE_MANAGER"]
}
```

### Access Protected Endpoint

```bash
# Replace <TOKEN> with the token received above
curl -X GET http://localhost:8080/api/v1/baskets \
  -H "Authorization: Bearer <TOKEN>"
```

## 🧪 Run Tests

```bash
# All tests
./gradlew test

# Security tests only
./gradlew test --tests "*security*"

# With coverage
./gradlew test jacocoTestReport
```

## 📊 Access Management UIs

- **API Documentation:** http://localhost:8080/api/swagger-ui.html
- **Actuator:** http://localhost:8080/api/actuator
- **RabbitMQ Management:** http://localhost:15672 (guest/guest)

## 🔍 Troubleshooting

### Error: "User passwords not configured"

**Cause:** Environment variables not loaded.

**Solution:**
1. Verify `.env` file exists and contains `DEV_*_PASSWORD` variables
2. Source the file: `export $(cat .env | xargs)`
3. Check with: `echo $DEV_ADMIN_PASSWORD`

### Error: "JWT_SECRET not set"

**Cause:** JWT_SECRET environment variable missing.

**Solution:**
1. Generate secret: `openssl rand -base64 64`
2. Add to `.env`: `JWT_SECRET=<generated-value>`
3. Reload environment

### Error: "Connection refused" (PostgreSQL)

**Cause:** PostgreSQL not running or wrong credentials.

**Solution:**
1. Check if running: `sudo systemctl status postgresql`
2. Verify credentials in `.env` match database
3. Test connection: `psql -h localhost -U warehouse_user -d warehouse_db_dev`

### Error: "Java class file major version 69 unsupported"

**Cause:** Spring Boot 3.4.0 has limited Java 25 support.

**Solution:**
- Integration tests are disabled for now
- Unit tests work normally
- Consider using Java 21 LTS for full compatibility

## 📚 Next Steps

1. Read [SECURITY.md](SECURITY.md) for security best practices
2. Check API documentation at `/api/swagger-ui.html`
3. Review Sprint documentation in `/docs` folder
4. Set up your IDE (IntelliJ IDEA or VS Code with Java extensions)

## 🆘 Need Help?

- Review logs: `tail -f logs/warehouse-service.log`
- Enable debug: Set `logging.level.br.com.dio=DEBUG` in `application-dev.yml`
- Check GitHub Issues or contact the development team

---

**Last Updated:** 2025-10-14  
**Author:** Franklin Canduri
