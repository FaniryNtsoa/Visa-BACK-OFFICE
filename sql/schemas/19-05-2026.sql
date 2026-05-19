-- Sprint 6 : statut "Photo terminé", pièces système photo/signature,
-- migration des chemins demandeur -> demande_piece_justificative, suppression colonnes demandeur.

BEGIN;

-- Statut "Photo terminé"
INSERT INTO status_demande (status)
SELECT 'Photo terminé'
WHERE NOT EXISTS (
    SELECT 1 FROM status_demande sd
    WHERE translate(lower(sd.status), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
        = translate(lower('Photo terminé'), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
);

-- Pièces catalogue (système)
INSERT INTO piece_justificative (piece_justificative, description)
SELECT 'Photo d''identité', 'Photo d''identité du demandeur (pièce système)'
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE translate(lower(trim(pj.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
        = translate(lower(trim('Photo d''identité')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
);

INSERT INTO piece_justificative (piece_justificative, description)
SELECT 'Signature digitale', 'Signature du demandeur (pièce système)'
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE translate(lower(trim(pj.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
        = translate(lower(trim('Signature digitale')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
);

-- Lignes demande_piece pour photo / signature (fichiers existants sur demandeur)
INSERT INTO demande_piece_justificative (id_demande, id_piece_justificative, photo_piece_justificative, date_depot)
SELECT d.id,
       (SELECT id FROM piece_justificative pj
        WHERE translate(lower(trim(pj.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
            = translate(lower(trim('Photo d''identité')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
        ORDER BY id LIMIT 1),
       trim(dem.photo_identite),
       CURRENT_TIMESTAMP
FROM demande d
JOIN demandeur dem ON dem.id = d.id_demandeur
WHERE dem.photo_identite IS NOT NULL AND length(trim(dem.photo_identite)) > 0
  AND NOT EXISTS (
      SELECT 1 FROM demande_piece_justificative x
      WHERE x.id_demande = d.id
        AND x.id_piece_justificative = (
            SELECT id FROM piece_justificative pj2
            WHERE translate(lower(trim(pj2.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
                = translate(lower(trim('Photo d''identité')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
            ORDER BY id LIMIT 1
        )
  );

INSERT INTO demande_piece_justificative (id_demande, id_piece_justificative, photo_piece_justificative, date_depot)
SELECT d.id,
       (SELECT id FROM piece_justificative pj
        WHERE translate(lower(trim(pj.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
            = translate(lower(trim('Signature digitale')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
        ORDER BY id LIMIT 1),
       trim(dem.signature_digital),
       CURRENT_TIMESTAMP
FROM demande d
JOIN demandeur dem ON dem.id = d.id_demandeur
WHERE dem.signature_digital IS NOT NULL AND length(trim(dem.signature_digital)) > 0
  AND NOT EXISTS (
      SELECT 1 FROM demande_piece_justificative x
      WHERE x.id_demande = d.id
        AND x.id_piece_justificative = (
            SELECT id FROM piece_justificative pj2
            WHERE translate(lower(trim(pj2.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
                = translate(lower(trim('Signature digitale')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
            ORDER BY id LIMIT 1
        )
  );

-- Lignes vides pour les demandes sans fichier encore (pour cohérence applicative)
INSERT INTO demande_piece_justificative (id_demande, id_piece_justificative, photo_piece_justificative, date_depot)
SELECT d.id,
       (SELECT id FROM piece_justificative pj
        WHERE translate(lower(trim(pj.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
            = translate(lower(trim('Photo d''identité')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
        ORDER BY id LIMIT 1),
       NULL,
       CURRENT_TIMESTAMP
FROM demande d
WHERE NOT EXISTS (
    SELECT 1 FROM demande_piece_justificative x
    WHERE x.id_demande = d.id
      AND x.id_piece_justificative = (
          SELECT id FROM piece_justificative pj2
          WHERE translate(lower(trim(pj2.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
              = translate(lower(trim('Photo d''identité')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
          ORDER BY id LIMIT 1
      )
);

INSERT INTO demande_piece_justificative (id_demande, id_piece_justificative, photo_piece_justificative, date_depot)
SELECT d.id,
       (SELECT id FROM piece_justificative pj
        WHERE translate(lower(trim(pj.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
            = translate(lower(trim('Signature digitale')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
        ORDER BY id LIMIT 1),
       NULL,
       CURRENT_TIMESTAMP
FROM demande d
WHERE NOT EXISTS (
    SELECT 1 FROM demande_piece_justificative x
    WHERE x.id_demande = d.id
      AND x.id_piece_justificative = (
          SELECT id FROM piece_justificative pj2
          WHERE translate(lower(trim(pj2.piece_justificative)), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
              = translate(lower(trim('Signature digitale')), 'éèêëàâäîïôöùûüç', 'eeeeaaaiioouuuc')
          ORDER BY id LIMIT 1
      )
);

ALTER TABLE demandeur DROP COLUMN IF EXISTS photo_identite;
ALTER TABLE demandeur DROP COLUMN IF EXISTS signature_digital;

COMMIT;
