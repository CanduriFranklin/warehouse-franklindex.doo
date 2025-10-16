# 🤝 Guia de Contribuição

Obrigado por considerar contribuir com o **Warehouse Microservice**! 🎉

Este documento fornece diretrizes para contribuir com o projeto de forma eficiente e organizada.

---

## 📋 Índice

- [Código de Conduta](#código-de-conduta)
- [Como Posso Contribuir?](#como-posso-contribuir)
- [Processo de Desenvolvimento](#processo-de-desenvolvimento)
- [Padrões de Código](#padrões-de-código)
- [Commits e Pull Requests](#commits-e-pull-requests)
- [Reportando Bugs](#reportando-bugs)
- [Sugerindo Melhorias](#sugerindo-melhorias)

---

## 📜 Código de Conduta

Este projeto adota o [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/version/2/1/code_of_conduct/).

### Em Resumo:

- ✅ Seja respeitoso e inclusivo
- ✅ Aceite críticas construtivas
- ✅ Foque no que é melhor para a comunidade
- ❌ Não use linguagem ofensiva ou discriminatória
- ❌ Não faça ataques pessoais
- ❌ Não publique informações privadas de outros

---

## 🚀 Como Posso Contribuir?

Existem várias formas de contribuir:

### 1. 🐛 Reportando Bugs

Encontrou um bug? Ajude-nos a melhorar!

1. Verifique se o bug já não foi reportado em [Issues](https://github.com/CanduriFranklin/warehouse-franklindex.doo/issues)
2. Abra uma nova issue usando o template de bug report
3. Descreva claramente o problema, incluindo:
   - Passos para reproduzir
   - Comportamento esperado vs. atual
   - Screenshots (se aplicável)
   - Ambiente (Java version, OS, etc.)

### 2. ✨ Sugerindo Features

Tem uma ideia para melhorar o projeto?

1. Verifique se já não existe uma issue similar
2. Abra uma issue usando o template de feature request
3. Descreva a funcionalidade desejada e sua motivação
4. Se possível, sugira uma implementação

### 3. 📝 Melhorando Documentação

Documentação nunca é demais!

- Corrigir erros de português ou inglês
- Adicionar exemplos práticos
- Melhorar explicações técnicas
- Criar tutoriais ou guias

### 4. 💻 Contribuindo com Código

Quer implementar uma feature ou corrigir um bug?

Siga o [Processo de Desenvolvimento](#processo-de-desenvolvimento) abaixo.

---

## 🔧 Processo de Desenvolvimento

### 1️⃣ Fork e Clone

```bash
# Fork o repositório no GitHub (botão "Fork")

# Clone seu fork
git clone https://github.com/SEU_USUARIO/warehouse-franklindex.doo.git
cd warehouse-franklindex.doo

# Adicione o repositório original como upstream
git remote add upstream https://github.com/CanduriFranklin/warehouse-franklindex.doo.git
```

### 2️⃣ Configure o Ambiente

```bash
# Copie e configure o .env
cp .env.example .env

# Inicie containers Docker
docker-compose up -d

# Compile o projeto
./gradlew clean build
```

### 3️⃣ Crie uma Branch

```bash
# Atualize main com upstream
git checkout main
git pull upstream main

# Crie uma branch para sua feature/bugfix
git checkout -b feature/minha-feature
# ou
git checkout -b bugfix/corrige-bug-x
```

**Convenção de nomes de branches:**

- `feature/nome-da-feature` - Nova funcionalidade
- `bugfix/nome-do-bug` - Correção de bug
- `docs/descricao` - Mudanças em documentação
- `refactor/descricao` - Refatoração de código
- `test/descricao` - Adição ou correção de testes

### 4️⃣ Desenvolva sua Contribuição

```bash
# Faça suas mudanças seguindo os padrões de código

# Adicione testes para seu código
# Execute os testes
./gradlew test

# Verifique a cobertura
./gradlew test jacocoTestReport
```

### 5️⃣ Commit suas Mudanças

```bash
# Adicione os arquivos modificados
git add .

# Commit seguindo o padrão Conventional Commits
git commit -m "feat: adiciona endpoint de relatórios"
# ou
git commit -m "fix: corrige cálculo de margem de lucro"
```

### 6️⃣ Push e Pull Request

```bash
# Push para seu fork
git push origin feature/minha-feature

# Abra um Pull Request no GitHub
# 1. Vá para seu fork no GitHub
# 2. Clique em "Compare & pull request"
# 3. Preencha o template de PR
# 4. Aguarde review
```

---

## 📏 Padrões de Código

### Arquitetura

Este projeto segue **Arquitetura Hexagonal (Ports & Adapters)**:

```
src/main/java/br/com/dio/warehouse/
├── application/           # Casos de uso (Application Layer)
│   ├── port/
│   │   ├── in/           # Input Ports (interfaces)
│   │   └── out/          # Output Ports (interfaces)
│   └── service/          # Implementação dos casos de uso
├── domain/               # Domain Layer (núcleo do negócio)
│   ├── aggregate/        # Aggregates (DDD)
│   ├── entity/           # Entidades de domínio
│   ├── event/            # Domain Events
│   ├── exception/        # Exceções de domínio
│   └── valueobject/      # Value Objects
├── infrastructure/       # Infrastructure Layer
│   ├── adapter/
│   │   ├── in/          # Input Adapters (Controllers, Consumers)
│   │   └── out/         # Output Adapters (Repositories, Publishers)
│   ├── config/          # Configurações
│   └── security/        # Segurança (JWT, Auth)
└── WarehouseApplication.java
```

### Convenções Java

```java
// 1. Use Lombok para reduzir boilerplate
@Getter
@Builder
@AllArgsConstructor
public class Example {
    private final String name;
}

// 2. Value Objects devem ser imutáveis
@Value
@Builder
public class Money {
    BigDecimal amount;
    Currency currency;
}

// 3. Use Optional para valores que podem ser nulos
public Optional<Basket> findBasket(UUID id) {
    return repository.findById(id);
}

// 4. Prefira interfaces funcionais e streams
baskets.stream()
    .filter(Basket::isAvailable)
    .collect(Collectors.toList());

// 5. Documente APIs públicas com JavaDoc
/**
 * Recebe uma entrega de cestas básicas no armazém.
 *
 * @param command comando contendo dados da entrega
 * @return caixa de entrega criada
 * @throws InvalidDeliveryException se dados inválidos
 */
public DeliveryBox receiveDelivery(ReceiveDeliveryCommand command) {
    // implementation
}
```

### Testes

```java
// 1. Use JUnit 5 e AssertJ
@Test
@DisplayName("Deve calcular preço de venda com margem de 25%")
void shouldCalculateSellingPriceWithMargin() {
    // Given
    Money cost = Money.of(new BigDecimal("5.00"));
    double margin = 0.25;
    
    // When
    Money sellingPrice = deliveryBox.calculateSellingPrice(margin);
    
    // Then
    assertThat(sellingPrice.amount())
        .isEqualByComparingTo(new BigDecimal("6.25"));
}

// 2. Use @MockBean para testes de integração
@SpringBootTest
class DeliveryControllerIntegrationTest {
    
    @MockBean
    private ReceiveDeliveryUseCase receiveDeliveryUseCase;
    
    @Autowired
    private MockMvc mockMvc;
}

// 3. Use TestContainers para testes com banco de dados
@Testcontainers
class DeliveryRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
}
```

### Nomenclatura

| Tipo | Convenção | Exemplo |
|------|-----------|---------|
| **Classes** | PascalCase | `DeliveryBox`, `BasketRepository` |
| **Métodos** | camelCase | `receiveDelivery()`, `calculatePrice()` |
| **Constantes** | UPPER_SNAKE_CASE | `MAX_RETRIES`, `DEFAULT_MARGIN` |
| **Pacotes** | lowercase | `com.dio.warehouse.domain` |
| **Variáveis** | camelCase | `totalCost`, `profitMargin` |

---

## 💬 Commits e Pull Requests

### Conventional Commits

Use o padrão [Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>[escopo opcional]: <descrição>

[corpo opcional]

[rodapé opcional]
```

**Tipos permitidos:**

- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Mudanças em documentação
- `style`: Formatação, ponto e vírgula faltando, etc.
- `refactor`: Refatoração de código
- `test`: Adição ou correção de testes
- `chore`: Mudanças em build, CI, ferramentas

**Exemplos:**

```bash
feat: adiciona endpoint de relatórios mensais
fix: corrige cálculo de margem de lucro
docs: atualiza README com instruções de deploy
refactor: extrai lógica de validação para service
test: adiciona testes para BasketService
chore: atualiza Spring Boot para 3.5.6
```

### Template de Pull Request

```markdown
## Descrição

[Descreva suas mudanças aqui]

## Tipo de Mudança

- [ ] 🐛 Bug fix (correção de problema)
- [ ] ✨ New feature (nova funcionalidade)
- [ ] 📝 Documentation (mudança em documentação)
- [ ] ♻️ Refactoring (refatoração de código)
- [ ] ✅ Tests (adição de testes)

## Checklist

- [ ] Meu código segue os padrões do projeto
- [ ] Realizei self-review do código
- [ ] Comentei partes complexas do código
- [ ] Atualizei a documentação
- [ ] Minhas mudanças não geram novos warnings
- [ ] Adicionei testes que provam que minha correção funciona
- [ ] Testes unitários passam localmente
- [ ] Verifiquei que mudanças dependentes foram mergeadas

## Testes

[Descreva os testes realizados]

## Screenshots (se aplicável)

[Adicione screenshots]
```

---

## 🐛 Reportando Bugs

### Template de Bug Report

```markdown
**Descrição do Bug**
Descrição clara do problema.

**Passos para Reproduzir**
1. Vá para '...'
2. Clique em '....'
3. Role até '....'
4. Veja o erro

**Comportamento Esperado**
O que deveria acontecer.

**Comportamento Atual**
O que está acontecendo.

**Screenshots**
Se aplicável, adicione screenshots.

**Ambiente:**
 - OS: [e.g. Ubuntu 22.04]
 - Java Version: [e.g. 25 LTS]
 - Spring Boot Version: [e.g. 3.5.6]
 - Docker Version: [e.g. 24.0.5]

**Contexto Adicional**
Qualquer outra informação relevante.

**Logs**
```
Cole logs relevantes aqui
```
```

---

## ✨ Sugerindo Melhorias

### Template de Feature Request

```markdown
**Descrição da Feature**
Descrição clara da funcionalidade desejada.

**Motivação**
Por que essa feature seria útil? Qual problema ela resolve?

**Solução Proposta**
Como você imagina que essa feature funcionaria?

**Alternativas Consideradas**
Outras soluções que você pensou.

**Contexto Adicional**
Screenshots, mockups, exemplos de outras aplicações, etc.
```

---

## 📞 Contato

Dúvidas sobre como contribuir?

- 💬 Abra uma [Discussion](https://github.com/CanduriFranklin/warehouse-franklindex.doo/discussions)
- 📧 Entre em contato via LinkedIn
- 🐛 Abra uma [Issue](https://github.com/CanduriFranklin/warehouse-franklindex.doo/issues)

---

## 🎉 Reconhecimento

Todos os contribuidores serão reconhecidos no projeto!

Suas contribuições, grandes ou pequenas, são valorizadas e apreciadas. 💙

---

**Obrigado por contribuir!** 🚀

*Este guia foi inspirado em projetos open-source de sucesso como Spring Boot, Kubernetes, e Angular.*
