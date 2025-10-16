# 🔒 Security Configuration Guide

## Overview

This document explains how to configure sensitive credentials and security settings for the Warehouse Microservice.

## ⚠️ Important Security Rules

1. **NEVER commit sensitive data to Git**
   - No passwords, secrets, or API keys in code
   - Use environment variables for all sensitive configuration
   - Keep `.env` file in `.gitignore`

2. **Generate strong secrets**
   - JWT secrets should be at least 256 bits
   - Use cryptographically secure random generators
   - Rotate secrets regularly in production

3. **Different credentials per environment**
   - Development, staging, and production must use different secrets
   - Never use development credentials in production

## 🚀 Quick Setup (Development)

### 1. Copy Environment Template

```bash
cp .env.example .env
```

### 2. Generate JWT Secret

**Using OpenSSL (recommended):**
```bash
openssl rand -base64 64
```

**Using Node.js:**
```bash
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

**Using Python:**
```bash
python3 -c "import secrets; print(secrets.token_urlsafe(64))"
```

### 3. Edit `.env` File

Replace all `change-me-*` values with your actual credentials:

```bash
# Example (use your own values!)
JWT_SECRET=Xk9vZ2R5Y2J5ZGNieWRjYnlkY2J5ZGNieWRjYnlkY2J5ZGNieWRjYnlkY2J5ZA==
DB_PASSWORD=my-secure-db-password
DEV_ADMIN_PASSWORD=StrongAdminPassword123!
```

### 4. Load Environment Variables

**Using `export` (Linux/Mac):**
```bash
export $(cat .env | xargs)
```

**Using `source` (Linux/Mac):**
```bash
source .env
```

**Using Docker Compose:**
```yaml
version: '3.8'
services:
  warehouse-service:
    env_file:
      - .env
```

## 🔧 Configuration Files

### application.yml (Committed)
Contains **only** environment variable references:
```yaml
warehouse:
  security:
    jwt:
      secret: ${JWT_SECRET}  # ✅ Safe - reads from environment
      expiration: ${JWT_EXPIRATION:86400000}
```

### .env (NOT Committed)
Contains **actual** sensitive values:
```bash
JWT_SECRET=your-actual-secret-here  # ❌ Never commit this file
```

### .env.example (Committed)
Contains **template** with placeholder values:
```bash
JWT_SECRET=change-me-to-a-strong-random-256-bit-key  # ✅ Safe - just a template
```

## 🏭 Production Deployment

### Option 1: Cloud Provider Secrets Manager

**AWS Secrets Manager:**
```bash
aws secretsmanager get-secret-value --secret-id warehouse/jwt-secret
```

**Azure Key Vault:**
```bash
az keyvault secret show --vault-name warehouse-vault --name jwt-secret
```

**Google Cloud Secret Manager:**
```bash
gcloud secrets versions access latest --secret="jwt-secret"
```

### Option 2: Kubernetes Secrets

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: warehouse-secrets
type: Opaque
data:
  jwt-secret: <base64-encoded-secret>
```

### Option 3: Docker Secrets

```yaml
version: '3.8'
services:
  warehouse-service:
    secrets:
      - jwt_secret
secrets:
  jwt_secret:
    external: true
```

## 🧪 Testing Users (Development Only)

The following users are configured via environment variables for **development and testing only**:

| Username | Password Env Var | Roles |
|----------|-----------------|-------|
| `${DEV_ADMIN_USERNAME}` | `${DEV_ADMIN_PASSWORD}` | ADMIN, WAREHOUSE_MANAGER |
| `${DEV_MANAGER_USERNAME}` | `${DEV_MANAGER_PASSWORD}` | WAREHOUSE_MANAGER, SALES |
| `${DEV_SALES_USERNAME}` | `${DEV_SALES_PASSWORD}` | SALES |

**⚠️ Important:** In production, replace `InMemoryUserDetailsService` with database-backed authentication.

## 📋 Security Checklist

Before deploying to production, verify:

- [ ] All sensitive values are in environment variables (not code)
- [ ] `.env` file is in `.gitignore`
- [ ] JWT secret is at least 256 bits and randomly generated
- [ ] Different credentials for each environment (dev/staging/prod)
- [ ] Database passwords are strong and unique
- [ ] CORS origins are restricted to your actual domains
- [ ] `InMemoryUserDetailsService` is replaced with database authentication
- [ ] Secrets are stored in a secrets manager (not plain environment variables)
- [ ] SSL/TLS is enabled for all external communication
- [ ] Regular security audits and dependency updates are scheduled

## 🔍 Auditing

### Check for Exposed Secrets in Git History

```bash
# Scan for potential secrets
git log -p | grep -i "secret\|password\|key"

# Use git-secrets tool
git secrets --scan-history
```

### If Secrets Were Committed

1. **Immediately rotate all exposed credentials**
2. **Remove from Git history:**
   ```bash
   git filter-branch --force --index-filter \
     "git rm --cached --ignore-unmatch src/main/resources/application-local.yml" \
     --prune-empty --tag-name-filter cat -- --all
   ```
3. **Force push (coordinate with team):**
   ```bash
   git push origin --force --all
   ```

## 📚 References

- [OWASP Secrets Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [12-Factor App Config](https://12factor.net/config)

---

**Last Updated:** 2025-10-14  
**Author:** Franklin Canduri
