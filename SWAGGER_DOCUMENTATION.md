# 📚 Documentação Swagger/OpenAPI

> Guia completo da documentação interativa da API CentralPet

---

## 🌐 Acessar Swagger UI

### URL Principal
```
http://localhost:8080/swagger-ui.html
```

### URLs Alternativas
```
http://localhost:8080/api-docs                    (JSON)
http://localhost:8080/swagger-ui/index.html       (UI)
http://localhost:8080/v3/api-docs                 (OpenAPI v3)
```

---

## 📖 O que é Swagger/OpenAPI?

Swagger (agora chamado OpenAPI) é um padrão aberto para documentar APIs REST. Permite:

✅ Visualizar todos os endpoints
✅ Ver models e estruturas de dados
✅ Testar endpoints diretamente
✅ Gerar código SDK automaticamente
✅ Documentação sempre sincronizada com código

---

## 🔐 Autenticação no Swagger

### Adicionar Token Bearer

1. **Abra Swagger UI** em `http://localhost:8080/swagger-ui.html`
2. **Clique no botão "Authorize"** (cadeado 🔒 no topo direito)
3. **Cole o token** recebido no login:
   ```
   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
4. **Clique em "Authorize"**
5. **Clique em "Close"**

Agora todos os endpoints protegidos funcionarão automaticamente!

---

## 📝 Endpoints Documentados

### 1️⃣ POST /auth/cadastro

**Descrição:** Criar novo usuário

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "nome": "João Silva",
  "cpf": "12345678900",
  "dataNascimento": "2001-09-18",
  "email": "joao@example.com",
  "senha": "senha123",
  "telefone": "32999999999",
  "endereco": "Rua X, 123"
}
```

**Respostas:**
- `201 Created`: Usuário criado com sucesso
- `400 Bad Request`: Dados inválidos

**Response Body (201):**
```
Usuário cadastrado com sucesso: João Silva
```

---

### 2️⃣ POST /auth/login

**Descrição:** Fazer login e receber token JWT

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "joao@example.com",
  "senha": "senha123"
}
```

**Respostas:**
- `200 OK`: Login realizado com sucesso
- `400 Bad Request`: Email ou senha incorretos

**Response Body (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2FvQGV4YW1wbGUuY29tIiwianRpIjoiZWI3YzBkYzAtODY1Yi00ZTU1LTliODUtYmI5NDc0ZTY1MWQwIiwiaWF0IjoxNzE0Nzc1MzQ5LCJleHAiOjE3MTQ4NjE3NDl9.9PjX5z6K8L2M3n4O5p6Q7r8S9t0U1v2W3x4Y5z6A",
  "tipo": "Bearer",
  "expiraEmMs": 86400000,
  "nome": "João Silva",
  "email": "joao@example.com"
}
```

---

### 3️⃣ GET /auth/me

**Descrição:** Obter dados do usuário logado

**Autenticação:** ✅ Requer Bearer Token

**Headers:**
```
Authorization: Bearer {token}
```

**Respostas:**
- `200 OK`: Dados do usuário retornados
- `401 Unauthorized`: Token inválido ou expirado

**Response Body (200):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "João Silva",
  "email": "joao@example.com"
}
```

---

### 4️⃣ POST /auth/logout

**Descrição:** Fazer logout e invalidar token

**Autenticação:** ✅ Requer Bearer Token

**Headers:**
```
Authorization: Bearer {token}
```

**Respostas:**
- `200 OK`: Logout realizado com sucesso
- `400 Bad Request`: Token não fornecido

**Response Body (200):**
```
Logout realizado com sucesso
```

---

## 🧪 Teste Rápido no Swagger

### Cenário: Cadastro → Login → Verificar Sessão

**Passo 1: Cadastro**
1. Abra Swagger e procure por `POST /auth/cadastro`
2. Clique em "Try it out"
3. Preencha o form com dados válidos:
   - Nome: João Silva
   - CPF: 12345678900 (11 dígitos)
   - Data Nascimento: 2001-09-18
   - Email: joao@example.com
   - Senha: senha123
   - Telefone: 32999999999 (10-15 dígitos)
   - Endereço: Rua X, 123
4. Clique em "Execute"
5. Veja a resposta `201 Created`

**Passo 2: Login**
1. Procure por `POST /auth/login`
2. Clique em "Try it out"
3. Preencha:
   - Email: joao@example.com
   - Senha: senha123
4. Clique em "Execute"
5. Copie o `token` retornado

**Passo 3: Adicionar Token no Swagger**
1. Clique no botão "Authorize" (🔒)
2. Cole o token em "Bearer"
3. Clique "Authorize"
4. Clique "Close"

**Passo 4: Verificar Sessão**
1. Procure por `GET /auth/me`
2. Clique em "Try it out"
3. Clique em "Execute"
4. Veja seus dados retornados!

**Passo 5: Logout**
1. Procure por `POST /auth/logout`
2. Clique em "Try it out"
3. Clique em "Execute"
4. Mensagem: "Logout realizado com sucesso"

---

## 📊 Modelos de Dados

### LoginDTO
```json
{
  "email": "string (email válido)",
  "senha": "string (mínimo 6 caracteres)"
}
```

### CadastroDTO
```json
{
  "nome": "string (máximo 150 caracteres)",
  "cpf": "string (11 dígitos)",
  "dataNascimento": "string (formato: YYYY-MM-DD)",
  "email": "string (email válido, único)",
  "senha": "string (mínimo 6 caracteres)",
  "telefone": "string (10-15 dígitos)",
  "endereco": "string (opcional, máximo 300 caracteres)"
}
```

### LoginResponseDTO
```json
{
  "token": "string (JWT token)",
  "tipo": "string ('Bearer')",
  "expiraEmMs": "number (86400000)",
  "nome": "string",
  "email": "string"
}
```

### UsuarioLogadoDTO
```json
{
  "id": "string (UUID)",
  "nome": "string",
  "email": "string"
}
```

---

## ❌ Tratamento de Erros (Swagger)

### Exemplo: Email Duplicado

**Request:**
```json
{
  "nome": "João Silva",
  "cpf": "12345678900",
  "dataNascimento": "2001-09-18",
  "email": "joao@example.com",  ← Já cadastrado
  "senha": "senha123",
  "telefone": "32999999999",
  "endereco": "Rua X, 123"
}
```

**Response (400):**
```json
{
  "timestamp": "2026-05-03T19:00:29.366Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email já está cadastrado",
  "path": "/auth/cadastro"
}
```

---

### Exemplo: CPF Inválido

**Request:**
```json
{
  "nome": "João Silva",
  "cpf": "123",  ← Menos de 11 dígitos
  "dataNascimento": "2001-09-18",
  "email": "joao@example.com",
  "senha": "senha123",
  "telefone": "32999999999",
  "endereco": "Rua X, 123"
}
```

**Response (400):**
```json
{
  "timestamp": "2026-05-03T19:00:29.366Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados de entrada inválidos",
  "path": "/auth/cadastro",
  "erros": [
    "cpf: CPF deve conter 11 dígitos numéricos"
  ]
}
```

---

### Exemplo: Token Expirado

**Response (401):**
```json
{
  "timestamp": "2026-05-03T19:00:29.366Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token expirado",
  "path": "/auth/me"
}
```

---

## 🔄 Import no Postman/Insomnia

### Opção 1: OpenAPI JSON

1. Abra o Postman
2. Clique em "Import"
3. Cole a URL: `http://localhost:8080/api-docs`
4. Clique em "Import"

### Opção 2: Adicionar Manualmente

```bash
# Copiar curl direto do Swagger
# No Swagger, em cada endpoint:
# → Clique em "..." (menu)
# → "Copy curl"
# → Cole no seu terminal ou Postman
```

---

## 🎯 Dicas Úteis

### 1. Sempre usar JSON

- Headers devem conter: `Content-Type: application/json`
- Dados devem ser JSON válido

### 2. Token Bearer

- Formato: `Bearer {token}`
- Não esqueça do espaço entre "Bearer" e o token
- Token válido por 24 horas

### 3. Validações

- CPF: Exatamente 11 dígitos (sem pontos/traços)
- Telefone: 10-15 dígitos (sem parênteses/traços)
- Email: Formato válido
- Senha: Mínimo 6 caracteres

### 4. Timezone

- Datas em UTC/ISO 8601
- Exemplo: `2001-09-18T00:00:00Z`

---

## 📱 Testar via cURL

### Cadastro
```bash
curl -X POST http://localhost:8080/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{
    "nome":"João Silva",
    "cpf":"12345678900",
    "dataNascimento":"2001-09-18",
    "email":"joao@example.com",
    "senha":"senha123",
    "telefone":"32999999999",
    "endereco":"Rua X, 123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"joao@example.com",
    "senha":"senha123"
  }'
```

### Verificar Sessão
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Logout
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🔗 Links Úteis

- 📖 [OpenAPI Specification](https://swagger.io/specification/)
- 🧪 [Swagger Editor](https://editor.swagger.io/)
- 📚 [SpringDoc Documentation](https://springdoc.org/)
- 🚀 [JWT.io](https://jwt.io/) - Decodificar tokens

---

## 📞 Suporte

Se há problemas com Swagger:
1. Reinicie a aplicação: `mvn spring-boot:run`
2. Limpe o cache: `Ctrl + Shift + Delete` (navegador)
3. Acesse novamente: `http://localhost:8080/swagger-ui.html`

---

**Swagger sempre sincronizado com seu código! ✨**

