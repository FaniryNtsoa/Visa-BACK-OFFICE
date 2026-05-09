ALTER TABLE demandeur
    ADD COLUMN IF NOT EXISTS photo_identite TEXT,
    ADD COLUMN IF NOT EXISTS signature_digital TEXT;
