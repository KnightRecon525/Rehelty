-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS rehelty;
USE rehelty;

-- Create hotel table if it doesn't exist
CREATE TABLE IF NOT EXISTS `hotel` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `nom` VARCHAR(100) NOT NULL,
    `localisation` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `nbrChambres` INT NOT NULL,
    `prixParNuite` DOUBLE NOT NULL,
    `etoiles` INT NOT NULL,
    `note` DOUBLE,
    `equipements` TEXT,
    `image_url` VARCHAR(1000)
);

-- Create bus table if it doesn't exist
CREATE TABLE IF NOT EXISTS `bus` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `numero_ligne` VARCHAR(50) NOT NULL,
    `point_depart` VARCHAR(100) NOT NULL,
    `point_arrivee` VARCHAR(100) NOT NULL,
    `heure_depart` TIME NOT NULL,
    `heure_arrivee` TIME NOT NULL,
    `capacite` INT NOT NULL,
    `places_restantes` INT NOT NULL,
    `jours_circulation` VARCHAR(7) NOT NULL DEFAULT '1111111',
    `prix` DOUBLE NOT NULL,
    `date_debut` DATE NOT NULL,
    `date_fin` DATE NOT NULL
);

-- Insert some test data if table is empty
INSERT INTO `hotel` (`nom`, `localisation`, `description`, `nbrChambres`, `prixParNuite`, `etoiles`, `note`, `equipements`, `image_url`)
SELECT * FROM (
    SELECT 
        'Hôtel Dar El Jeld' as nom,
        'Tunis' as localisation,
        'Un hôtel de luxe au cœur de la Médina' as description,
        50 as nbrChambres,
        450.0 as prixParNuite,
        5 as etoiles,
        4.8 as note,
        'Wi-Fi gratuit, Restaurant, Spa, Piscine' as equipements,
        '/images/hotels/dar-el-jeld.jpg' as image_url
) AS tmp
WHERE NOT EXISTS (
    SELECT nom FROM hotel WHERE nom = 'Hôtel Dar El Jeld'
) LIMIT 1; 