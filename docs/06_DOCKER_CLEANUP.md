# 🧹 Guia de Limpeza - Docker e Kubernetes

**Data**: 16 de Outubro de 2025  
**Versão**: 1.1  
**Autor**: Franklin Canduri  
**LinkedIn**: [https://www.linkedin.com/in/franklin-david-canduri-presilla-b75956266/](https://www.linkedin.com/in/franklin-david-canduri-presilla-b75956266/)

---

## 📋 Objetivo

Este guia ajudará a limpar imagens Docker antigas de projetos anteriores (especialmente relacionadas ao Kubernetes) e manter apenas as imagens necessárias para o projeto **Warehouse & Storefront**.

---

## 🎯 Situação Atual do Sistema

### Imagens Detectadas (16/10/2025)

```
REPOSITORY                                TAG                                        SIZE      STATUS
postgres                                  16-alpine                                  394MB     ✅ MANTER (usado no projeto)
dpage/pgadmin4                            latest                                     783MB     ✅ MANTER (usado no projeto)
rabbitmq                                  3.12-management-alpine                     271MB     ✅ MANTER (usado no projeto)
docker/desktop-kubernetes                 kubernetes-v1.34.1-cni-v1.7.1-*           591MB     ❌ REMOVER (Kubernetes teste)
registry.k8s.io/kube-apiserver            v1.34.1                                    118MB     ❌ REMOVER (Kubernetes teste)
registry.k8s.io/kube-controller-manager   v1.34.1                                    101MB     ❌ REMOVER (Kubernetes teste)
registry.k8s.io/kube-scheduler            v1.34.1                                    73.5MB    ❌ REMOVER (Kubernetes teste)
registry.k8s.io/kube-proxy                v1.34.1                                    102MB     ❌ REMOVER (Kubernetes teste)
registry.k8s.io/etcd                      3.6.4-0                                    273MB     ❌ REMOVER (Kubernetes teste)
registry.k8s.io/coredns/coredns           v1.12.1                                    101MB     ❌ REMOVER (Kubernetes teste)
registry.k8s.io/pause                     3.10                                       1.06MB    ❌ REMOVER (Kubernetes teste)
docker/desktop-vpnkit-controller          dc331cb22850be0cdd97c84a9cfecaf44a1afb6e  47MB      ❌ REMOVER (Docker Desktop)
docker/desktop-storage-provisioner        v2.0                                       59.2MB    ❌ REMOVER (Docker Desktop)
```

**Análise**:
- **Total de imagens**: 13
- **Imagens a manter**: 3 (postgres, rabbitmq, pgadmin4) = **1.45 GB**
- **Imagens a remover**: 10 (Kubernetes + Docker Desktop) = **~1.47 GB**
- **Espaço a liberar**: **~1.47 GB (50% de redução)**

---

## � Limpeza Rápida (Comandos Específicos)

### Método 1: Remoção Individual (Mais Seguro)

```bash
# Remover imagens do Kubernetes (registry.k8s.io)
docker rmi registry.k8s.io/kube-apiserver:v1.34.1
docker rmi registry.k8s.io/kube-controller-manager:v1.34.1
docker rmi registry.k8s.io/kube-scheduler:v1.34.1
docker rmi registry.k8s.io/kube-proxy:v1.34.1
docker rmi registry.k8s.io/etcd:3.6.4-0
docker rmi registry.k8s.io/coredns/coredns:v1.12.1
docker rmi registry.k8s.io/pause:3.10

# Remover imagens do Docker Desktop
docker rmi docker/desktop-kubernetes:kubernetes-v1.34.1-cni-v1.7.1-critools-v1.33.0-cri-dockerd-v0.3.20-1-debian
docker rmi docker/desktop-vpnkit-controller:dc331cb22850be0cdd97c84a9cfecaf44a1afb6e
docker rmi docker/desktop-storage-provisioner:v2.0
```

### Método 2: Remoção por Padrão (Mais Rápido)

```bash
# Remover TODAS as imagens do Kubernetes
docker rmi $(docker images | grep 'registry.k8s.io' | awk '{print $3}')

# Remover TODAS as imagens do Docker Desktop
docker rmi $(docker images | grep 'docker/desktop-' | awk '{print $3}')

# Comando único para remover tudo
docker rmi $(docker images | grep -E 'registry.k8s.io|docker/desktop-' | awk '{print $3}')
```

### Método 3: Script Automatizado

```bash
#!/bin/bash
# Script: cleanup-k8s-images.sh

echo "🧹 Iniciando limpeza de imagens Docker..."
echo ""

# Contar imagens antes
BEFORE=$(docker images | wc -l)

# Remover imagens do Kubernetes
echo "📦 Removendo imagens do Kubernetes..."
docker images | grep 'registry.k8s.io' | awk '{print $1":"$2}' | xargs -r docker rmi 2>/dev/null

# Remover imagens do Docker Desktop
echo "🐳 Removendo imagens do Docker Desktop..."
docker images | grep 'docker/desktop-' | awk '{print $1":"$2}' | xargs -r docker rmi 2>/dev/null

# Contar imagens depois
AFTER=$(docker images | wc -l)
REMOVED=$((BEFORE - AFTER))

echo ""
echo "✅ Limpeza concluída!"
echo "   Imagens removidas: $REMOVED"
echo ""
echo "📊 Imagens restantes:"
docker images
echo ""
echo "💾 Espaço em disco:"
docker system df
```

**Como usar**:
```bash
# Salvar o script
nano cleanup-k8s-images.sh
# (colar o conteúdo acima)

# Dar permissão de execução
chmod +x cleanup-k8s-images.sh

# Executar
./cleanup-k8s-images.sh
```

---

## �🔍 Verificação Inicial

Antes de remover qualquer imagem, vamos listar o que existe:

### 1. Listar Todas as Imagens

```bash
# Ver todas as imagens
docker images

# Ver tamanho total ocupado
docker system df

# Listar imagens com formato customizado
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.ID}}"
```

### 2. Identificar Imagens do Kubernetes

Imagens comuns do Kubernetes que podem ser removidas:

```
- k8s.gcr.io/*
- registry.k8s.io/*
- kindest/node
- calico/*
- coredns/*
- pause:*
- etcd:*
- kube-apiserver:*
- kube-controller-manager:*
- kube-scheduler:*
- kube-proxy:*
- metallb/*
- nginx-ingress/*
```

---

## 🗑️ Limpeza Segura (Recomendado)

### Opção 1: Remover Imagens Específicas

```bash
# Remover imagens do Kubernetes (uma por vez, com confirmação)
docker rmi k8s.gcr.io/pause:3.9
docker rmi registry.k8s.io/kube-apiserver:v1.28.0
docker rmi registry.k8s.io/kube-controller-manager:v1.28.0
docker rmi registry.k8s.io/kube-scheduler:v1.28.0
docker rmi registry.k8s.io/kube-proxy:v1.28.0
docker rmi registry.k8s.io/coredns:v1.10.1
docker rmi registry.k8s.io/etcd:3.5.9-0

# Remover imagens do Kind (Kubernetes in Docker)
docker rmi kindest/node:v1.28.0

# Remover imagens de rede (Calico, Flannel, etc.)
docker images | grep -E 'calico|flannel|weave' | awk '{print $3}' | xargs docker rmi
```

### Opção 2: Remover por Padrão (Kubernetes)

```bash
# Remover todas as imagens que contém 'k8s' no nome
docker images | grep 'k8s' | awk '{print $3}' | xargs docker rmi -f

# Remover todas as imagens que contém 'kube' no nome
docker images | grep 'kube' | awk '{print $3}' | xargs docker rmi -f

# Remover imagens do registry.k8s.io
docker images | grep 'registry.k8s.io' | awk '{print $3}' | xargs docker rmi -f

# Remover imagens do gcr.io relacionadas ao Kubernetes
docker images | grep 'gcr.io' | awk '{print $3}' | xargs docker rmi -f
```

---

## 🧼 Limpeza Completa (Avançado)

### Remover Imagens Não Usadas

```bash
# Remover imagens sem tag (<none>)
docker image prune

# Remover imagens não utilizadas por nenhum container (cuidado!)
docker image prune -a

# Remover tudo que não está sendo usado (containers, volumes, imagens)
docker system prune -a --volumes
```

### Limpeza Seletiva com Confirmação

```bash
# Listar imagens candidatas à remoção (sem tag ou antigas)
docker images --filter "dangling=true"

# Remover apenas imagens "dangling" (sem tag)
docker images --filter "dangling=true" -q | xargs docker rmi

# Remover imagens criadas há mais de 30 dias
docker images --format '{{.ID}} {{.CreatedAt}}' | \
  awk '{if ($2 < "2024-09-16") print $1}' | \
  xargs docker rmi
```

---

## ✅ Manter Imagens do Projeto Warehouse

**Imagens que NÃO devem ser removidas:**

```
# Backend (quando criadas)
warehouse-app:latest
warehouse-backend:1.0.0

# Frontend
warehouse-frontend-prod:latest
warehouse-frontend:1.0.0

# Infraestrutura (usadas pelo docker-compose.yml)
postgres:16-alpine
rabbitmq:3.13-management-alpine
dpage/pgadmin4:8.12
node:20-alpine
nginx:1.27-alpine

# Base images (podem ser baixadas novamente se necessário)
eclipse-temurin:25-jdk-alpine
eclipse-temurin:25-jre-alpine
alpine:latest
```

### Proteger Imagens do Projeto

```bash
# Adicionar tag para proteger imagens importantes
docker tag postgres:16-alpine warehouse-postgres:keep
docker tag rabbitmq:3.13-management-alpine warehouse-rabbitmq:keep
docker tag nginx:1.27-alpine warehouse-nginx:keep
```

---

## 📊 Script de Limpeza Automatizado

Crie um script `cleanup-docker.sh`:

```bash
#!/bin/bash

echo "🧹 Limpeza de Imagens Docker - Warehouse Project"
echo "================================================"
echo ""

# Backup da lista de imagens antes da limpeza
echo "📋 Criando backup da lista de imagens..."
docker images > docker-images-backup-$(date +%Y%m%d-%H%M%S).txt

echo ""
echo "🔍 Imagens atuais:"
docker images

echo ""
echo "⚠️  Este script removerá:"
echo "   - Imagens relacionadas ao Kubernetes (k8s, kube, kind)"
echo "   - Imagens sem tag (<none>)"
echo "   - Imagens não utilizadas"
echo ""
read -p "Deseja continuar? (s/N): " confirm

if [[ $confirm != [sS] ]]; then
    echo "❌ Operação cancelada."
    exit 0
fi

echo ""
echo "🗑️  Removendo imagens do Kubernetes..."
docker images | grep -E 'k8s\.gcr\.io|registry\.k8s\.io|kindest|kube-|pause' | awk '{print $3}' | xargs -r docker rmi -f 2>/dev/null

echo ""
echo "🗑️  Removendo imagens de rede (Calico, Flannel, etc.)..."
docker images | grep -E 'calico|flannel|weave|metallb|coredns' | awk '{print $3}' | xargs -r docker rmi -f 2>/dev/null

echo ""
echo "🗑️  Removendo imagens sem tag (<none>)..."
docker image prune -f

echo ""
echo "📊 Estado final:"
docker system df

echo ""
echo "✅ Limpeza concluída!"
echo ""
echo "💡 Imagens mantidas para o projeto Warehouse:"
docker images | grep -E 'postgres|rabbitmq|pgadmin|node|nginx|warehouse|alpine'
```

### Executar o Script

```bash
# Dar permissão de execução
chmod +x cleanup-docker.sh

# Executar
./cleanup-docker.sh
```

---

## 🛡️ Prevenção de Limpeza Acidental

### Criar .dockerignore para Proteção

Adicione um arquivo `.docker-keep` na raiz do projeto:

```bash
# Criar arquivo de proteção
cat > .docker-keep << 'EOF'
# Imagens protegidas do projeto Warehouse
postgres:16-alpine
rabbitmq:3.13-management-alpine
dpage/pgadmin4:8.12
node:20-alpine
nginx:1.27-alpine
eclipse-temurin:25-jdk-alpine
eclipse-temurin:25-jre-alpine
EOF
```

### Script de Proteção

```bash
#!/bin/bash
# protect-images.sh

echo "🛡️  Protegendo imagens do projeto Warehouse..."

# Ler imagens do .docker-keep e adicionar tag de proteção
while read -r image; do
    if [[ ! $image =~ ^# ]]; then  # Ignorar comentários
        echo "Protegendo: $image"
        docker pull $image 2>/dev/null
        docker tag $image "warehouse-keep/$(echo $image | tr ':/' '--')" 2>/dev/null
    fi
done < .docker-keep

echo "✅ Imagens protegidas!"
```

---

## 📈 Monitoramento de Espaço

### Verificar Uso de Disco

```bash
# Espaço total usado pelo Docker
docker system df

# Detalhamento por tipo
docker system df -v

# Espaço usado por imagens
docker images --format "{{.Size}}" | \
  awk '{sum+=$1} END {print "Total:", sum, "MB"}'
```

### Criar Alias para Limpeza Rápida

Adicione ao `~/.bashrc` ou `~/.zshrc`:

```bash
# Aliases para limpeza Docker
alias docker-clean-images='docker image prune -a -f'
alias docker-clean-containers='docker container prune -f'
alias docker-clean-volumes='docker volume prune -f'
alias docker-clean-all='docker system prune -a -f --volumes'
alias docker-size='docker system df'

# Alias específico para K8s
alias docker-clean-k8s='docker images | grep -E "k8s|kube|kind" | awk "{print \$3}" | xargs -r docker rmi -f'
```

Recarregar configurações:
```bash
source ~/.bashrc
```

---

## 🚨 Recuperação em Caso de Erro

### Se Removeu uma Imagem Necessária

```bash
# Baixar novamente a imagem
docker pull postgres:16-alpine
docker pull rabbitmq:3.13-management-alpine
docker pull dpage/pgadmin4:8.12
docker pull node:20-alpine
docker pull nginx:1.27-alpine

# Ou usar docker-compose para baixar todas de uma vez
cd /home/franklindex/projects/warehouse-franklindex.doo
docker-compose pull
```

### Reconstruir Imagens do Projeto

```bash
# Backend
cd backend
docker build -t warehouse-backend:latest .

# Frontend Produção
cd ../frontend
docker build -f Dockerfile.prod -t warehouse-frontend:latest .

# Ou usar docker-compose
cd ..
docker-compose build
```

---

## 📚 Referências

- [Docker Image Documentation](https://docs.docker.com/engine/reference/commandline/image/)
- [Docker System Prune](https://docs.docker.com/engine/reference/commandline/system_prune/)
- [Docker Best Practices for Images](https://docs.docker.com/develop/dev-best-practices/)

---

## ⚠️ Avisos Importantes

1. **Sempre faça backup** da lista de imagens antes de remover:
   ```bash
   docker images > backup-images-$(date +%Y%m%d).txt
   ```

2. **Verifique containers em execução** antes de remover imagens:
   ```bash
   docker ps -a
   ```

3. **Imagens compartilhadas**: Algumas imagens podem ser usadas por múltiplos projetos.

4. **Tamanho do Docker Desktop**: No Windows, o arquivo de dados do Docker Desktop (ext4.vhdx) pode crescer. Para reduzi-lo:
   ```powershell
   # PowerShell como Administrador
   wsl --shutdown
   # Localizar: %LOCALAPPDATA%\Docker\wsl\data\ext4.vhdx
   # Usar Disk Cleanup ou otimizar manualmente
   ```

---

## ✅ Checklist de Limpeza

- [ ] Backup da lista de imagens atual
- [ ] Identificar imagens do Kubernetes
- [ ] Verificar containers em execução
- [ ] Remover imagens K8s específicas
- [ ] Limpar imagens sem tag
- [ ] Verificar espaço recuperado
- [ ] Testar docker-compose do projeto
- [ ] Documentar imagens removidas

---

**Última Atualização**: 16 de Outubro de 2025  
**Mantido por**: Franklin Canduri
