CREATE TABLE IF NOT EXISTS usuarios (
    id              VARCHAR(128) PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    cpf             CHAR(11) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    senha           VARCHAR(255) NOT NULL,
    telefone        VARCHAR(15) NOT NULL UNIQUE,
    rua             VARCHAR(200),
    numero          VARCHAR(20),
    cidade          VARCHAR(100),
    estado          VARCHAR(2),
    criado_em       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
