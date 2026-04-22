-- ==========================================================
-- SCRIPT DE DONNÉES DE TEST - SYSTÈME GESTION VISA
-- ==========================================================

BEGIN;

-- 1. RÉFÉRENTIELS (Données de base pour les listes déroulantes)
-- ----------------------------------------------------------

INSERT INTO status_demande (status) VALUES 
('Dossier créé'), 
('Scan terminé'), 
('Visa approuvé');

INSERT INTO type_demande (type_demande) VALUES 
('Nouvelle demande de titre'), 
('Transfert visa'), 
('Duplicata');

INSERT INTO situation_familiale (situation_familiale) VALUES 
('Célibataire'), ('Marié(e)'), ('Divorcé(e)'), ('Veuf/Veuve');

INSERT INTO nationalite (nationalite, code_pays) VALUES 
('Française', 'FR'), ('Chinoise', 'CN'), ('Indienne', 'IN'), ('Italienne', 'IT');

INSERT INTO type_visa (type_visa, duree_validite_mois, description) VALUES 
('Travailleur', 24, 'Visa professionnel pour expatriés sous contrat'),
('Investisseur', 36, 'Visa pour opérateurs économiques et gérants');

INSERT INTO piece_justificative (piece_justificative, description) VALUES 
('Contrat de Travail', 'Contrat visé par l''EDBM ou Ministère'),
('Statuts Société', 'Document juridique de l''entreprise'),
('Certificat d''Hébergement', 'Preuve de logement à Madagascar'),
('Copie Passeport', 'Scan de la page d''identité');

-- 2. CONFIGURATION DES OBLIGATOIRES
-- ----------------------------------------------------------

-- Obligatoires pour Travailleur
INSERT INTO obligatoire (id_type_visa, nom_table, nom_colonne_obligatoire) VALUES 
(1, 'demandeur', 'nom'),
(1, 'demandeur', 'prenom'),
(1, 'passeport', 'numero_passeport'),
(1, 'demande_travailleur', 'salaire_mensuel');

-- Obligatoires pour Investisseur
INSERT INTO obligatoire (id_type_visa, nom_table, nom_colonne_obligatoire) VALUES 
(2, 'demandeur', 'nom'),
(2, 'projet_investissement', 'montant_investissement'),
(2, 'demande_investisseur', 'numero_registre_commerce');


-- 3. CAS N°1 : NOUVELLE DEMANDE (TRAVAILLEUR) - STATUS : Dossier créé
-- ----------------------------------------------------------

-- Création du passeport
INSERT INTO passeport (id, numero_passeport, date_delivrance, date_expiration, lieu_delivrance, pays_delivrance) 
VALUES (1, 'FR-TEST-001', '2024-01-01', '2034-01-01', 'Paris', 'France');

-- Création du demandeur
INSERT INTO demandeur (id, nom, prenom, date_naissance, genre, id_situation_familiale, id_nationalite, id_passeport)
VALUES (1, 'RAZAFY', 'Faniry', '1990-05-20', 'Masculin', 1, 1, 1);

-- Visa Transformable d'entrée
INSERT INTO num_visa_transformable (id, id_demandeur, num_visa_transformable, date_expiration_visa)
VALUES (1, 1, 'TR-9988-MG', '2026-07-15');

-- La Demande principale
INSERT INTO demande (id, date_demande, id_visa_transformable, id_type_demande, id_status, id_demandeur, id_type_visa)
VALUES (1, '2026-04-22', 1, 1, 1, 1, 1);

-- Infos Employeur
INSERT INTO employeur_madagascar (id, raison_sociale, numero_nif, secteur_activite)
VALUES (1, 'SOCIETE EXEMPLE SARL', '400123456', 'Technologie');

-- Lien Demande <-> Travailleur
INSERT INTO demande_travailleur (id_demande, id_employeur, poste_occupe, salaire_mensuel, devise_salaire)
VALUES (1, 1, 'Ingénieur Réseau', 4500000, 'MGA');


-- 4. CAS N°2 : NOUVELLE DEMANDE (INVESTISSEUR) - STATUS : Scan terminé
-- ----------------------------------------------------------

INSERT INTO passeport (id, numero_passeport, date_delivrance, date_expiration, lieu_delivrance, pays_delivrance) 
VALUES (2, 'CN-TEST-888', '2025-06-10', '2035-06-10', 'Beijing', 'Chine');

INSERT INTO demandeur (id, nom, prenom, date_naissance, genre, id_situation_familiale, id_nationalite, id_passeport)
VALUES (2, 'WANG', 'Li', '1982-11-30', 'Féminin', 2, 2, 2);

INSERT INTO demande (id, date_demande, id_type_demande, id_status, id_demandeur, id_type_visa)
VALUES (2, '2026-04-20', 1, 2, 2, 2); -- Status 2 = Scan terminé

INSERT INTO projet_investissement (id, nom_projet, montant_investissement, devise)
VALUES (1, 'Usine de Recyclage', 150000000, 'MGA');

INSERT INTO demande_investisseur (id_demande, id_projet, forme_juridique, numero_registre_commerce)
VALUES (2, 1, 'SA', 'RCS-TANA-2026-B-001');

-- Simulation des fichiers uploadés pour le Scan Terminé
INSERT INTO demande_piece_justificative (id_demande, id_piece_justificative, photo_piece_justificative, date_depot)
VALUES 
(2, 2, 'path/to/statuts.pdf', '2026-04-21'),
(2, 4, 'path/to/passport_scan.jpg', '2026-04-21');


-- 5. CAS N°3 : TRANSFERT VISA (PERTE PASSEPORT)
-- ----------------------------------------------------------

-- Nouveau passeport pour le demandeur 1 (Faniry)
INSERT INTO passeport (id, numero_passeport, date_delivrance, date_expiration) 
VALUES (3, 'FR-NOUVEAU-22', '2026-04-10', '2036-04-10');

-- Nouvelle demande de transfert
INSERT INTO demande (id, date_demande, id_type_demande, id_status, id_demandeur, id_type_visa)
VALUES (3, '2026-04-22', 2, 1, 1, 1);


-- 6. CAS N°4 : DUPLICATA (PERTE CARTE)
-- ----------------------------------------------------------

-- Création d'une ancienne carte existante
INSERT INTO carte_residence (id, numero_carte, date_debut, date_fin, is_duplicata, id_passeport)
VALUES (1, 'CR-776655', '2025-01-01', '2027-01-01', FALSE, 1);

-- Demande de duplicata
INSERT INTO demande (id, date_demande, id_type_demande, id_status, id_demandeur, id_type_visa)
VALUES (4, '2026-04-22', 3, 1, 1, 1);

-- Lien vers le duplicata (Prêt pour impression)
INSERT INTO carte_residence (id, numero_carte, date_debut, date_fin, is_duplicata, id_carte_residence_duplicata, id_passeport, id_demande)
VALUES (2, 'CR-776655', '2026-04-22', '2027-01-01', TRUE, 1, 1, 4);

COMMIT;