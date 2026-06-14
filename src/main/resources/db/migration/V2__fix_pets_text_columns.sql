-- V2 -- Corrige colunas de filtros de pets que eventualmente foram salvas como bytea.
-- Evita erro: function lower(bytea) does not exist.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'pets' AND column_name = 'nome' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE pets
            ALTER COLUMN nome TYPE VARCHAR(100)
            USING convert_from(nome, 'UTF8');
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'pets' AND column_name = 'especie' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE pets
            ALTER COLUMN especie TYPE VARCHAR(50)
            USING convert_from(especie, 'UTF8');
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'pets' AND column_name = 'cor' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE pets
            ALTER COLUMN cor TYPE VARCHAR(80)
            USING convert_from(cor, 'UTF8');
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'pets' AND column_name = 'porte' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE pets
            ALTER COLUMN porte TYPE VARCHAR(20)
            USING convert_from(porte, 'UTF8');
    END IF;
END $$;

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

