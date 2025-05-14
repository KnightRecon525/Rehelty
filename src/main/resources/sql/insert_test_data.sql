-- Insérer des destinations de test si la table est vide
INSERT INTO destinations (nom, url) 
SELECT * FROM (
    SELECT 'Djerba' as nom, 'https://i.imgur.com/8XcQ2u2.jpg' as url
    UNION ALL
    SELECT 'Tunis', 'https://i.imgur.com/YqbwB2P.jpg'
    UNION ALL
    SELECT 'Hammamet', 'https://i.imgur.com/pZHZhHt.jpg'
    UNION ALL
    SELECT 'Gabès', 'https://i.imgur.com/L5XqLr6.jpg'
) AS temp
WHERE NOT EXISTS (
    SELECT nom FROM destinations WHERE nom IN ('Djerba', 'Tunis', 'Hammamet', 'Gabès')
); 