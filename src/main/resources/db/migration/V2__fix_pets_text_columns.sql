-- V2 -- Corrige colunas de filtros de pets que eventualmente foram salvas como bytea.
-- Evita erro: function lower(bytea) does not exist.

ALTER TABLE pets
    ALTER COLUMN nome TYPE VARCHAR(100)
    USING convert_from(nome::bytea, 'UTF8');

ALTER TABLE pets
    ALTER COLUMN especie TYPE VARCHAR(50)
    USING convert_from(especie::bytea, 'UTF8');

ALTER TABLE pets
    ALTER COLUMN cor TYPE VARCHAR(80)
    USING convert_from(cor::bytea, 'UTF8');

ALTER TABLE pets
    ALTER COLUMN porte TYPE VARCHAR(20)
    USING convert_from(porte::bytea, 'UTF8');

DROP INDEX IF EXISTS idx_pets_nome_lower;
DROP INDEX IF EXISTS idx_pets_especie_lower;
DROP INDEX IF EXISTS idx_pets_cor_lower;
DROP INDEX IF EXISTS idx_pets_porte_lower;

CREATE INDEX IF NOT EXISTS idx_pets_nome_lower
    ON pets (LOWER(nome));

CREATE INDEX IF NOT EXISTS idx_pets_especie_lower
    ON pets (LOWER(especie));

CREATE INDEX IF NOT EXISTS idx_pets_cor_lower
    ON pets (LOWER(cor));

CREATE INDEX IF NOT EXISTS idx_pets_porte_lower
    ON pets (LOWER(porte));

