-- ==========================================================
-- SCRIPT DE DONNeES DE TEST - SYSTÈME GESTION VISA
-- VERSION CORRIGeE (IDs dynamiques via sous-requêtes)
-- ==========================================================

BEGIN;

-- ==========================================================
-- 1. ReFeRENTIELS
-- ==========================================================

INSERT INTO status_demande (status) VALUES 
('Dossier cree'), 
('Scan termine'), 
('Visa approuve');

INSERT INTO type_demande (type_demande) VALUES 
('Nouvelle demande de titre'), 
('Transfert visa'), 
('Duplicata'), 
('Transfert visa sans donnees anterieures'), 
('Duplicata sans donnees anterieures');

INSERT INTO situation_familiale (situation_familiale) VALUES 
('Celibataire'), ('Marie(e)'), ('Divorce(e)'), ('Veuf/Veuve');

INSERT INTO nationalite (nationalite, code_pays) VALUES 
('Francaise',      'FR'), 
('Chinoise',       'CN'), 
('Indienne',       'IN'), 
('Italienne',      'IT'),
('Americaine',     'US'),
('Allemande',      'DE'),
('Espagnole',      'ES'),
('Britannique',    'GB'),
('Japonaise',      'JP'),
('Bresilienne',    'BR'),
('Canadienne',     'CA'),
('Australienne',   'AU'),
('Sud-Africaine',  'ZA'),
('Mexicaine',      'MX'),
('Russe',          'RU'),
('Coreenne',       'KR'),
('Marocaine',      'MA'),
('Turque',         'TR'),
('Neerlandaise',   'NL'),
('Suedoise',       'SE'),
('Suisse',         'CH'),
('Belge',          'BE'),
('Portugaise',     'PT'),
('Vietnamienne',   'VN');

INSERT INTO type_visa (type_visa, duree_validite_mois, description) VALUES 
('Travailleur',  24, 'Visa professionnel pour expatries sous contrat'),
('Investisseur', 36, 'Visa pour operateurs economiques et gerants');

INSERT INTO piece_justificative (piece_justificative, description) VALUES 
('Contrat de Travail',        'Contrat vise par l''EDBM ou Ministère'),
('Statuts Societe',           'Document juridique de l''entreprise'),
('Certificat d''Hebergement', 'Preuve de logement à Madagascar'),
('Copie Passeport',           'Scan de la page d''identite');


-- ==========================================================
-- 2. CONFIGURATION DES CHAMPS OBLIGATOIRES
--    Utilisation de sous-requêtes pour recuperer les IDs
--    reels, evitant toute erreur de cle etrangère.
-- ==========================================================

-- Obligatoires pour Visa Travailleur
INSERT INTO obligatoire (id_type_visa, nom_table, nom_colonne_obligatoire)
SELECT id, 'demandeur',          'nom'              FROM type_visa WHERE type_visa = 'Travailleur'
UNION ALL
SELECT id, 'demandeur',          'date_naissance'   FROM type_visa WHERE type_visa = 'Travailleur'
UNION ALL
SELECT id, 'demandeur',          'adresse_mada'     FROM type_visa WHERE type_visa = 'Travailleur'
UNION ALL
SELECT id, 'nationalite',        'nationalite'      FROM type_visa WHERE type_visa = 'Travailleur'
UNION ALL
SELECT id, 'situation_familiale','situation_familiale' FROM type_visa WHERE type_visa = 'Travailleur'
UNION ALL
SELECT id, 'passeport',          'numero_passeport' FROM type_visa WHERE type_visa = 'Travailleur'
UNION ALL
SELECT id, 'demande_travailleur','salaire_mensuel'  FROM type_visa WHERE type_visa = 'Travailleur';

-- Obligatoires pour Visa Investisseur
INSERT INTO obligatoire (id_type_visa, nom_table, nom_colonne_obligatoire)
SELECT id, 'demandeur',            'nom'                    FROM type_visa WHERE type_visa = 'Investisseur'
UNION ALL
SELECT id, 'demandeur',            'date_naissance'         FROM type_visa WHERE type_visa = 'Investisseur'
UNION ALL
SELECT id, 'demandeur',            'adresse_mada'           FROM type_visa WHERE type_visa = 'Investisseur'
UNION ALL
SELECT id, 'nationalite',          'nationalite'            FROM type_visa WHERE type_visa = 'Investisseur'
UNION ALL
SELECT id, 'situation_familiale',  'situation_familiale'    FROM type_visa WHERE type_visa = 'Investisseur'
UNION ALL
SELECT id, 'passeport',            'numero_passeport'       FROM type_visa WHERE type_visa = 'Investisseur'
UNION ALL
SELECT id, 'projet_investissement','montant_investissement' FROM type_visa WHERE type_visa = 'Investisseur'
UNION ALL
SELECT id, 'demande_investisseur', 'numero_registre_commerce' FROM type_visa WHERE type_visa = 'Investisseur';

COMMIT;