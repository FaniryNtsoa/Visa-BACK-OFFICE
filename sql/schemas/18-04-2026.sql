-- Situation Familiale
CREATE TABLE situation_familiale (
    id_situation_familiale SERIAL PRIMARY KEY,
    situation_familiale VARCHAR(255) -- Célibataire, Marié(e), Divorcé(e), Veuf/Veuve
);

-- Nationalité
CREATE TABLE nationalite (
    id_nationalite SERIAL PRIMARY KEY,
    nationalite VARCHAR(255),
    code_pays VARCHAR(10)
);

-- Passeport
CREATE TABLE  passeport (
    id_passeport SERIAL PRIMARY KEY,
    numero_passeport VARCHAR(255),
    date_delivrance DATE,
    date_expiration DATE,
    lieu_delivrance VARCHAR(255),
    pays_delivrance VARCHAR(255)
);

-- Demandeur
CREATE TABLE demandeur (
    id_demandeur SERIAL PRIMARY KEY,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    date_naissance DATE,
    lieu_naissance VARCHAR(255),
    genre VARCHAR(50),
    adresse_mada TEXT,
    id_situation_familiale INTEGER REFERENCES situation_familiale(id_situation_familiale),
    id_nationalite INTEGER REFERENCES nationalite(id_nationalite),
    id_passeport INTEGER REFERENCES  passeport(id_passeport)
);

-- Employeur Madagascar (Visa Travailleur)
CREATE TABLE employeur_madagascar (
    id_employeur SERIAL PRIMARY KEY,
    raison_sociale VARCHAR(255),
    numero_nif VARCHAR(255),
    numero_stat VARCHAR(255),
    secteur_activite VARCHAR(255),
    adresse TEXT,
    telephone VARCHAR(255),
    email VARCHAR(255),
    nom_responsable VARCHAR(255),
    fonction_responsable VARCHAR(255)
);

-- Projet Investissement (Visa Investisseur)
CREATE TABLE projet_investissement (
    id_projet SERIAL PRIMARY KEY,
    nom_projet VARCHAR(255),
    secteur VARCHAR(255),
    description_projet TEXT,
    montant_investissement NUMERIC,
    devise VARCHAR(10),
    zone_investissement VARCHAR(255),
    nombre_emplois_crees INTEGER,
    duree_projet_mois INTEGER
);

-- Type Visa
CREATE TABLE type_visa (
    id_type_visa SERIAL PRIMARY KEY,
    type_visa VARCHAR(255),
    duree_validite_mois INTEGER,
    description TEXT
);

-- Type Demande
CREATE TABLE type_demande (
    id_type_demande SERIAL PRIMARY KEY,
    type_demande VARCHAR(255)
);

-- Status Demande
CREATE TABLE status_demande (
    id_status SERIAL PRIMARY KEY,
    status VARCHAR(255)
);

-- Num Visa Transformable
CREATE TABLE num_visa_transformable (
    id_visa_transformable SERIAL PRIMARY KEY,
    id_demandeur INTEGER REFERENCES demandeur(id_demandeur),
    num_visa_transformable VARCHAR(255),
    date_expiration_visa DATE
);

-- Demande
CREATE TABLE demande (
    id_demande SERIAL PRIMARY KEY,
    date_demande DATE,
    id_visa_transformable INTEGER REFERENCES num_visa_transformable(id_visa_transformable),
    id_type_demande INTEGER REFERENCES type_demande(id_type_demande),
    id_status INTEGER REFERENCES status_demande(id_status),
    id_demandeur INTEGER REFERENCES demandeur(id_demandeur),
    id_type_visa INTEGER REFERENCES type_visa(id_type_visa)
);

-- Demande Travailleur (Lien demande <-> employeur)
CREATE TABLE demande_travailleur (
    id_demande_travailleur SERIAL PRIMARY KEY,
    id_demande INTEGER REFERENCES demande(id_demande),
    id_employeur INTEGER REFERENCES employeur_madagascar(id_employeur),
    poste_occupe VARCHAR(255),
    type_contrat VARCHAR(255),
    duree_contrat_mois INTEGER,
    salaire_mensuel NUMERIC,
    devise_salaire VARCHAR(10)
);

-- Demande Investisseur (Lien demande <-> investissement)
CREATE TABLE demande_investisseur (
    id_demande_investisseur SERIAL PRIMARY KEY,
    id_demande INTEGER REFERENCES demande(id_demande),
    id_projet INTEGER REFERENCES projet_investissement(id_projet),
    forme_juridique VARCHAR(255),
    numero_registre_commerce VARCHAR(255)
);

-- Visa
CREATE TABLE visa (
    id_visa SERIAL PRIMARY KEY,
    date_debut DATE,
    date_fin DATE,
    numero_visa VARCHAR(255),
    id_passeport INTEGER REFERENCES  passeport(id_passeport),
    id_demande INTEGER REFERENCES demande(id_demande)
);

-- Carte Résidence
CREATE TABLE carte_residence (
    id_carte_residence SERIAL PRIMARY KEY,
    numero_carte VARCHAR(255),
    date_debut DATE,
    date_fin DATE,
    is_duplicata BOOLEAN,
    id_carte_residence_duplicata INTEGER REFERENCES carte_residence(id_carte_residence),
    id_passeport INTEGER REFERENCES  passeport(id_passeport),
    id_demande INTEGER REFERENCES demande(id_demande)
);

-- Pièce Justificative
CREATE TABLE piece_justificative (
    id_piece_justificative SERIAL PRIMARY KEY,
    piece_justificative VARCHAR(255),
    description TEXT
);

-- Demande Pièce Justificative
CREATE TABLE demande_piece_justificative (
    id_demande_piece_justificative SERIAL PRIMARY KEY,
    id_demande INTEGER REFERENCES demande(id_demande),
    id_piece_justificative INTEGER REFERENCES piece_justificative(id_piece_justificative),
    photo_piece_justificative TEXT,
    date_depot DATE
);

-- Obligatoire
CREATE TABLE obligatoire (
    id_obligatoire SERIAL PRIMARY KEY,
    id_type_visa INTEGER REFERENCES type_visa(id_type_visa),
    nom_colonne_obligatoire VARCHAR(255),
    nom_table VARCHAR(255)
);