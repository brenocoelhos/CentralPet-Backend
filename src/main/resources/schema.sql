-- ─── Usuarios ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id              VARCHAR(128) PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    cpf             CHAR(11) UNIQUE,
    data_nascimento DATE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    senha           VARCHAR(255) NOT NULL,
    telefone        VARCHAR(15) UNIQUE,
    rua             VARCHAR(200),
    numero          VARCHAR(20),
    cidade          VARCHAR(100),
    estado          VARCHAR(2),
    criado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ─── Pets ─────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS pets (
    id                    BIGSERIAL PRIMARY KEY,
    usuario_id            VARCHAR(128) NOT NULL,
    nome                  VARCHAR(100) NOT NULL,
    especie               VARCHAR(50)  NOT NULL,
    raca                  VARCHAR(100),
    cor                   VARCHAR(80),
    porte                 VARCHAR(20),
    data_desaparecimento  DATE,
    local_desaparecimento VARCHAR(300),
    cep                   VARCHAR(8),
    latitude_desaparecimento DOUBLE PRECISION,
    longitude_desaparecimento DOUBLE PRECISION,
    descricao             TEXT,
    castrado              BOOLEAN,
    vacinado              BOOLEAN,
    recompensa            BOOLEAN,
    foto_url              VARCHAR(500),
    nome_tutor            VARCHAR(150) NOT NULL,
    telefone_tutor        VARCHAR(15)  NOT NULL,
    criado_em             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
        ON DELETE CASCADE
);

-- ─── Pet Tags ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS pet_tags (
    id     BIGSERIAL PRIMARY KEY,
    pet_id BIGINT      NOT NULL,
    tag    VARCHAR(80) NOT NULL,
    CONSTRAINT fk_pet_tags_pet
        FOREIGN KEY (pet_id) REFERENCES pets (id)
        ON DELETE CASCADE
);

-- ─── Pet Imagens ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS pet_imagens (
    id        BIGSERIAL PRIMARY KEY,
    pet_id    BIGINT        NOT NULL,
    url       VARCHAR(1000) NOT NULL,
    s3_key    VARCHAR(500)  NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pet_imagens_pet
        FOREIGN KEY (pet_id) REFERENCES pets (id)
        ON DELETE CASCADE
);

-- ─── Notificacoes Token ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notificacoes_token (
    id            BIGSERIAL PRIMARY KEY,
    token         VARCHAR(255) NOT NULL UNIQUE,
    user_id       VARCHAR(128) NOT NULL,
    lat           DOUBLE PRECISION NOT NULL,
    lng           DOUBLE PRECISION NOT NULL,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacoes_token_usuario
        FOREIGN KEY (user_id) REFERENCES usuarios (id)
        ON DELETE CASCADE
);

-- ─── Indices ──────────────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_pets_usuario_id
    ON pets (usuario_id);

CREATE INDEX IF NOT EXISTS idx_pets_busca_principal
    ON pets (usuario_id, data_desaparecimento, nome, especie);

CREATE INDEX IF NOT EXISTS idx_pets_location
    ON pets (latitude_desaparecimento, longitude_desaparecimento);

CREATE INDEX IF NOT EXISTS idx_pet_tags_pet_id
    ON pet_tags (pet_id);

CREATE INDEX IF NOT EXISTS idx_notificacoes_token_user_id
    ON notificacoes_token (user_id);
