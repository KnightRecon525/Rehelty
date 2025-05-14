-- Créer la table destinations si elle n'existe pas
CREATE TABLE IF NOT EXISTS `destinations` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `nom` VARCHAR(100) NOT NULL,
    `url` VARCHAR(1000) NOT NULL
); 