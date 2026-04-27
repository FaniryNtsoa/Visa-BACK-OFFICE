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


-- ==========================================================
-- 3. CAS N°1 : NOUVELLE DEMANDE TRAVAILLEUR — "Dossier cre"
-- ==========================================================

-- INSERT INTO passeport (numero_passeport, date_delivrance, date_expiration, lieu_delivrance, pays_delivrance) 
-- VALUES ('FR-TEST-001', '2024-01-01', '2034-01-01', 'Paris', 'France');

-- INSERT INTO demandeur (nom, prenom, date_naissance, genre, id_situation_familiale, id_nationalite, id_passeport)
-- VALUES (
--     'RAZAFY', 'Faniry', '1990-05-20', 'Masculin',
--     (SELECT id FROM situation_familiale WHERE situation_familiale = 'Celibataire'),
--     (SELECT id FROM nationalite          WHERE code_pays = 'FR'),
--     (SELECT id FROM passeport            WHERE numero_passeport = 'FR-TEST-001')
-- );

-- INSERT INTO num_visa_transformable (id_demandeur, num_visa_transformable, date_expiration_visa)
-- VALUES (
--     (SELECT id FROM demandeur WHERE nom = 'RAZAFY' AND prenom = 'Faniry'),
--     'TR-9988-MG',
--     '2026-07-15'
-- );

-- INSERT INTO demande (date_demande, id_visa_transformable, id_type_demande, id_status, id_demandeur, id_type_visa)
-- VALUES (
--     '2026-04-22',
--     (SELECT id FROM num_visa_transformable WHERE num_visa_transformable = 'TR-9988-MG'),
--     (SELECT id FROM type_demande   WHERE type_demande = 'Nouvelle demande de titre'),
--     (SELECT id FROM status_demande WHERE status = 'Dossier cre'),
--     (SELECT id FROM demandeur      WHERE nom = 'RAZAFY' AND prenom = 'Faniry'),
--     (SELECT id FROM type_visa      WHERE type_visa = 'Travailleur')
-- );

-- INSERT INTO employeur_madagascar (raison_sociale, numero_nif, secteur_activite)
-- VALUES ('SOCIETE EXEMPLE SARL', '400123456', 'Technologie');

-- INSERT INTO demande_travailleur (id_demande, id_employeur, poste_occupe, salaire_mensuel, devise_salaire)
-- VALUES (
--     (SELECT d.id FROM demande d
--      JOIN demandeur dm ON dm.id = d.id_demandeur
--      WHERE dm.nom = 'RAZAFY' AND d.date_demande = '2026-04-22'
--      ORDER BY d.id DESC LIMIT 1),
--     (SELECT id FROM employeur_madagascar WHERE numero_nif = '400123456'),
--     'Ingenieur Reseau',
--     4500000,
--     'MGA'
-- );


-- -- ==========================================================
-- -- 4. CAS N°2 : NOUVELLE DEMANDE INVESTISSEUR — "Scan termine"
-- -- ==========================================================

-- INSERT INTO passeport (numero_passeport, date_delivrance, date_expiration, lieu_delivrance, pays_delivrance) 
-- VALUES ('CN-TEST-888', '2025-06-10', '2035-06-10', 'Beijing', 'Chine');

-- INSERT INTO demandeur (nom, prenom, date_naissance, genre, id_situation_familiale, id_nationalite, id_passeport)
-- VALUES (
--     'WANG', 'Li', '1982-11-30', 'Feminin',
--     (SELECT id FROM situation_familiale WHERE situation_familiale = 'Marie(e)'),
--     (SELECT id FROM nationalite          WHERE code_pays = 'CN'),
--     (SELECT id FROM passeport            WHERE numero_passeport = 'CN-TEST-888')
-- );

-- INSERT INTO demande (date_demande, id_type_demande, id_status, id_demandeur, id_type_visa)
-- VALUES (
--     '2026-04-20',
--     (SELECT id FROM type_demande   WHERE type_demande = 'Nouvelle demande de titre'),
--     (SELECT id FROM status_demande WHERE status = 'Scan termine'),
--     (SELECT id FROM demandeur      WHERE nom = 'WANG' AND prenom = 'Li'),
--     (SELECT id FROM type_visa      WHERE type_visa = 'Investisseur')
-- );

-- INSERT INTO projet_investissement (nom_projet, montant_investissement, devise)
-- VALUES ('Usine de Recyclage', 150000000, 'MGA');

-- INSERT INTO demande_investisseur (id_demande, id_projet, forme_juridique, numero_registre_commerce)
-- VALUES (
--     (SELECT d.id FROM demande d
--      JOIN demandeur dm ON dm.id = d.id_demandeur
--      WHERE dm.nom = 'WANG' AND d.date_demande = '2026-04-20'
--      ORDER BY d.id DESC LIMIT 1),
--     (SELECT id FROM projet_investissement WHERE nom_projet = 'Usine de Recyclage'),
--     'SA',
--     'RCS-TANA-2026-B-001'
-- );

-- -- Documents scannes
-- INSERT INTO demande_piece_justificative (id_demande, id_piece_justificative, photo_piece_justificative, date_depot)
-- VALUES 
-- (
--     (SELECT d.id FROM demande d
--      JOIN demandeur dm ON dm.id = d.id_demandeur
--      WHERE dm.nom = 'WANG' ORDER BY d.id DESC LIMIT 1),
--     (SELECT id FROM piece_justificative WHERE piece_justificative = 'Statuts Societe'),
--     'path/to/statuts.pdf',
--     '2026-04-21'
-- ),
-- (
--     (SELECT d.id FROM demande d
--      JOIN demandeur dm ON dm.id = d.id_demandeur
--      WHERE dm.nom = 'WANG' ORDER BY d.id DESC LIMIT 1),
--     (SELECT id FROM piece_justificative WHERE piece_justificative = 'Copie Passeport'),
--     'path/to/passport_scan.jpg',
--     '2026-04-21'
-- );


-- -- ==========================================================
-- -- 5. CAS N°3 : TRANSFERT VISA (PERTE PASSEPORT) — Faniry
-- -- ==========================================================

-- -- Nouveau passeport (sans lieu/pays car inconnu au moment du transfert)
-- INSERT INTO passeport (numero_passeport, date_delivrance, date_expiration)
-- VALUES ('FR-NOUVEAU-22', '2026-04-10', '2036-04-10');

-- -- Mise à jour du demandeur avec le nouveau passeport
-- UPDATE demandeur 
-- SET id_passeport = (SELECT id FROM passeport WHERE numero_passeport = 'FR-NOUVEAU-22')
-- WHERE nom = 'RAZAFY' AND prenom = 'Faniry';

-- INSERT INTO demande (date_demande, id_type_demande, id_status, id_demandeur, id_type_visa)
-- VALUES (
--     '2026-04-22',
--     (SELECT id FROM type_demande   WHERE type_demande = 'Transfert visa'),
--     (SELECT id FROM status_demande WHERE status = 'Dossier cre'),
--     (SELECT id FROM demandeur      WHERE nom = 'RAZAFY' AND prenom = 'Faniry'),
--     (SELECT id FROM type_visa      WHERE type_visa = 'Travailleur')
-- );


-- -- ==========================================================
-- -- 6. CAS N°4 : DUPLICATA (PERTE CARTE) — Faniry
-- -- ==========================================================

-- -- Carte d'origine (liee à l'ancien passeport FR-TEST-001)
-- INSERT INTO carte_residence (numero_carte, date_debut, date_fin, is_duplicata, id_passeport)
-- VALUES (
--     'CR-776655',
--     '2025-01-01',
--     '2027-01-01',
--     FALSE,
--     (SELECT id FROM passeport WHERE numero_passeport = 'FR-TEST-001')
-- );

-- -- Demande de duplicata
-- INSERT INTO demande (date_demande, id_type_demande, id_status, id_demandeur, id_type_visa)
-- VALUES (
--     '2026-04-22',
--     (SELECT id FROM type_demande   WHERE type_demande = 'Duplicata'),
--     (SELECT id FROM status_demande WHERE status = 'Dossier cre'),
--     (SELECT id FROM demandeur      WHERE nom = 'RAZAFY' AND prenom = 'Faniry'),
--     (SELECT id FROM type_visa      WHERE type_visa = 'Travailleur')
-- );

-- -- Carte duplicata (liee à la demande de duplicata)
-- INSERT INTO carte_residence (numero_carte, date_debut, date_fin, is_duplicata, id_carte_residence_duplicata, id_passeport, id_demande)
-- VALUES (
--     'CR-776655',
--     '2026-04-22',
--     '2027-01-01',
--     TRUE,
--     (SELECT id FROM carte_residence WHERE numero_carte = 'CR-776655' AND is_duplicata = FALSE),
--     (SELECT id FROM passeport WHERE numero_passeport = 'FR-TEST-001'),
--     (SELECT id FROM demande d
--      JOIN demandeur dm ON dm.id = d.id_demandeur
--      WHERE dm.nom = 'RAZAFY'
--        AND d.id_type_demande = (SELECT id FROM type_demande WHERE type_demande = 'Duplicata')
--      ORDER BY d.id DESC LIMIT 1)
-- );

COMMIT;