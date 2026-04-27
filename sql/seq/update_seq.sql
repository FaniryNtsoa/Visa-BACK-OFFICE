-- Synchroniser la table employeur_madagascar (celle qui a causé l'erreur)
SELECT setval('employeur_madagascar_id_seq', (SELECT MAX(id) FROM employeur_madagascar));

-- Synchroniser les autres tables où vous avez forcé les IDs
SELECT setval('passeport_id_seq', (SELECT MAX(id) FROM passeport));
SELECT setval('demandeur_id_seq', (SELECT MAX(id) FROM demandeur));
SELECT setval('demande_id_seq', (SELECT MAX(id) FROM demande));
SELECT setval('projet_investissement_id_seq', (SELECT MAX(id) FROM projet_investissement));
SELECT setval('carte_residence_id_seq', (SELECT MAX(id) FROM carte_residence));
SELECT setval('num_visa_transformable_id_seq', (SELECT MAX(id) FROM num_visa_transformable));