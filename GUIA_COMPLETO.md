# 🚀 GUIA COMPLETO - CentralPet Backend

> Tudo que você precisa saber para usar o CentralPet Backend

---

## 📖 Índice Rápido

1. [Setup Inicial](#setup-inicial)
2. [Iniciando a API](#iniciando-a-api)
3. [Acessar Swagger](#acessar-swagger)
4. [Entender Autenticação](#entender-autenticação)
5. [Fazer Requisições](#fazer-requisições)
6. [Frontend Integration](#frontend-integration)

---

## 🔧 Setup Inicial

### Pré-requisitos
- Java 25+
- Maven 3.8+
- PostgreSQL 12+

### 1. Criar Banco de Dados

```sql
CREATE DATABASE centralpet;
```

### 2. Configurar Aplicação

Edite `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/centralpet
spring.datasource.username=postgres
spring.datasource.password=admin123

# JWT
app.jwt.secret=centralpet-secret-key-do-breno-2026
app.jwt.expiration-ms=86400000
```

### 3. Instalar Dependências

```bash
cd C:\Users\breno\IdeaProjects\CentralPet-Backend
mvn clean install
```

---

## 🚀 Iniciando a API

### Comando para Rodar

```bash
mvn spring-boot:run
```

Ou, se usar Maven Wrapper:

```bash
./mvnw spring-boot:run
```

### Verificar se Está Rodando

Acesse: `http://localhost:8080`

Se retornar um erro 404, significa que a API está rodando! ✅

---

## 📚 Acessar Swagger

### URL Principal

```
http://localhost:8080/swagger-ui.html
```

### O que Ver Lá

- ✅ Todos os endpoints listados
- ✅ Estrutura dos requests/responses
- ✅ Botão para testar cada endpoint
- ✅ Autorização com Bearer token

### Teste Rápido no Swagger

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Procure por `POST /auth/cadastro`
3. Clique "Try it out"
4. Preencha dados (veja exemplos abaixo)
5. Clique "Execute"

---

## 🔐 Entender Autenticação

### Fluxo Básico

```
Usuário
   ↓
[1] POST /auth/cadastro → Cria conta
   ↓
[2] POST /auth/login → Recebe JWT
   ↓
[3] GET /auth/me → Valida token
   ↓
[4] POST /auth/logout → Invalida token
```

### O que é JWT?

JWT (JSON Web Token) é um token único e assinado:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwianRpIjoiZWI3YzBkYzAtODY1Yi00ZTU1LTliODUtYmI5NDc0ZTY1MWQwIiwiaWF0IjoxNzE0Nzc1MzQ5LCJleHAiOjE3MTQ4NjE3NDl9.9PjX5z6K8L2M3n4O5p6Q7r8S9t0U1v2W3x4Y5z6A
```

**Propriedades:**
- ✅ Assinado com HMAC-SHA256
- ✅ Válido por 24 horas
- ✅ Pode ser decodificado (mas não alterado)
- ✅ Este token específico expira após logout

---

## 📝 Fazer Requisições

### Com cURL (Windows PowerShell)

#### 1️⃣ Cadastro

```powershell
$cadastro = @{
    nome = "João Silva"
    cpf = "12345678900"
    dataNascimento = "2001-09-18"
    email = "joao@example.com"
    senha = "senha123"
    telefone = "32999999999"
    endereco = "Rua X, 123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/auth/cadastro" `
  -Method Post `
  -Headers @{"Content-Type"="application/json"} `
  -Body $cadastro
```

**Resposta:**
```
Usuário cadastrado com sucesso: João Silva
```

#### 2️⃣ Login

```powershell
$login = @{
    email = "joao@example.com"
    senha = "senha123"
} | ConvertTo-Json

$resposta = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" `
  -Method Post `
  -Headers @{"Content-Type"="application/json"} `
  -Body $login

$token = $resposta.token
Write-Host "Token recebido: $token"
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "expiraEmMs": 86400000,
  "nome": "João Silva",
  "email": "joao@example.com"
}
```

#### 3️⃣ Verificar Sessão

```powershell
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

Invoke-RestMethod -Uri "http://localhost:8080/auth/me" `
  -Method Get `
  -Headers @{"Authorization"="Bearer $token"}
```

**Resposta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "João Silva",
  "email": "joao@example.com"
}
```

#### 4️⃣ Logout

```powershell
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

Invoke-RestMethod -Uri "http://localhost:8080/auth/logout" `
  -Method Post `
  -Headers @{"Authorization"="Bearer $token"}
```

**Resposta:**
```
Logout realizado com sucesso
```

---

### Com JavaScript/Node.js

```javascript
const API_URL = 'http://localhost:8080/auth';

// 1️⃣ Cadastro
async function cadastrar() {
  const res = await fetch(`${API_URL}/cadastro`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      nome: 'João Silva',
      cpf: '12345678900',
      dataNascimento: '2001-09-18',
      email: 'joao@example.com',
      senha: 'senha123',
      telefone: '32999999999',
      endereco: 'Rua X, 123'
    })
  });
  console.log(await res.text());
}

// 2️⃣ Login
async function fazerLogin() {
  const res = await fetch(`${API_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: 'joao@example.com',
      senha: 'senha123'
    })
  });
  const data = await res.json();
  return data.token;
}

// 3️⃣ Verificar Sessão
async function verificarSessao(token) {
  const res = await fetch(`${API_URL}/me`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  console.log(await res.json());
}

// 4️⃣ Logout
async function fazerLogout(token) {
  const res = await fetch(`${API_URL}/logout`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  console.log(await res.text());
}

// Testar
const token = await fazerLogin();
await verificarSessao(token);
await fazerLogout(token);
```

---

## 🌐 Frontend Integration

### 1. Guardar Token no Frontend

Após login, guarde o token:

```javascript
// Depois de fazer login
localStorage.setItem('token', response.token);
localStorage.setItem('usuario', JSON.stringify({
  nome: response.nome,
  email: response.email
}));
```

### 2. Usar Token em Requisições Protegidas

```javascript
const token = localStorage.getItem('token');

// Requisição com token
fetch('/api/protegido', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

### 3. Validar Sessão ao Carregar Página

```javascript
// No início da página
async function validarLogin() {
  const token = localStorage.getItem('token');
  
  if (!token) {
    // Redirecionar ao login
    window.location.href = '/login';
    return;
  }

  const res = await fetch('http://localhost:8080/auth/me', {
    headers: { 'Authorization': `Bearer ${token}` }
  });

  if (!res.ok) {
    // Token inválido
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
}

validarLogin();
```

### 4. Fazer Logout

```javascript
const token = localStorage.getItem('token');

await fetch('http://localhost:8080/auth/logout', {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` }
});

localStorage.removeItem('token');
localStorage.removeItem('usuario');
window.location.href = '/login';
```

---

## ✅ Validações Importantes

### CPF
- ❌ Não: `123.456.789-00` (com máscara)
- ✅ Sim: `12345678900` (só números)

### Telefone
- ❌ Não: `(32) 99999-9999` (com padrão)
- ✅ Sim: `32999999999` (só números)

### Senha
- ✅ Mínimo: 6 caracteres
- ✅ Máximo: 255 caracteres
- ✅ Criptografada (não pode recuperar)

### Email
- ✅ Formato válido
- ✅ Único no sistema

---

## 📋 Documentação Completa

### README
- Documentação do projeto
- Estrutura do código
- Todos os endpoints

**Arquivo:** `README.md`

### Swagger
- Documentação interativa
- Testar endpoints no navegador
- Exemplos automáticos

**URL:** `http://localhost:8080/swagger-ui.html`

### Frontend
- Código pronto para usar no frontend
- Serviço de autenticação
- Componentes de exemplo

**Arquivo:** `FRONTEND_AUTH_EXEMPLO.md`

---

## 🐛 Troubleshooting

### Erro: "Cannot connect to localhost:8080"

**Causa:** API não está rodando

**Solução:**
```bash
mvn spring-boot:run
```

### Erro: "Database does not exist"

**Causa:** Banco não foi criado

**Solução:**
```sql
CREATE DATABASE centralpet;
```

### Erro: "Token invalided (logout)"

**Causa:** Você fez logout de outra aba

**Solução:** Faça login novamente

### Erro: "Email already registered"

**Causa:** Email já existe no sistema

**Solução:** Use outro email

---

## 📞 Próximas Features

- [ ] Gerenciamento de Pets
- [ ] Upload de Fotos
- [ ] Dashboard
- [ ] Notificações
- [ ] Integração com Google Maps

---

## 📚 Arquivos Importantes

```
CentralPet-Backend/
├── README.md                          ← Documentação geral
├── SWAGGER_DOCUMENTATION.md           ← Guia Swagger
├── FRONTEND_AUTH_EXEMPLO.md           ← Código frontend pronto
├── GUIA_COMPLETO.md                   ← Este arquivo
├── pom.xml                            ← Dependências
└── src/main/resources/
    └── application.properties         ← Configuração
```

---

## 🎉 Você Está Pronto!

✅ Backend rodando
✅ Swagger disponível
✅ Autenticação implementada
✅ Frontend pronto para integrar

**Próximo passo:** Integrar com o frontend! 🚀

---

**Dúvidas? Veja os arquivos de documentação listados acima!**

