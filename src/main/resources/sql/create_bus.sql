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

-- Insert sample data
INSERT INTO `bus` (
    `numero_ligne`, 
    `point_depart`, 
    `point_arrivee`, 
    `heure_depart`, 
    `heure_arrivee`, 
    `capacite`, 
    `places_restantes`, 
    `jours_circulation`, 
    `prix`, 
    `date_debut`, 
    `date_fin`
) VALUES 
-- Tunis vers autres villes
('L1', 'Tunis', 'Sousse', '08:00:00', '10:30:00', 50, 50, '1111111', 25.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L2', 'Tunis', 'Monastir', '09:00:00', '11:45:00', 45, 45, '1111111', 30.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L3', 'Tunis', 'Sfax', '07:30:00', '11:30:00', 55, 55, '1111111', 35.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L4', 'Tunis', 'Nabeul', '10:00:00', '11:15:00', 40, 40, '1111111', 15.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L5', 'Tunis', 'Bizerte', '08:30:00', '09:45:00', 45, 45, '1111111', 18.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),

-- Sousse vers autres villes
('L6', 'Sousse', 'Monastir', '09:00:00', '09:45:00', 35, 35, '1111111', 10.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L7', 'Sousse', 'Sfax', '10:00:00', '12:00:00', 50, 50, '1111111', 25.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L8', 'Sousse', 'Kairouan', '11:30:00', '12:45:00', 40, 40, '1111111', 15.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),

-- Sfax vers autres villes
('L9', 'Sfax', 'Gabès', '14:00:00', '15:30:00', 45, 45, '1111111', 20.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L10', 'Sfax', 'Monastir', '15:30:00', '17:30:00', 40, 40, '1111111', 25.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),

-- Retours
('L11', 'Sousse', 'Tunis', '16:00:00', '18:30:00', 50, 50, '1111111', 25.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L12', 'Monastir', 'Tunis', '17:00:00', '19:45:00', 45, 45, '1111111', 30.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L13', 'Sfax', 'Tunis', '15:30:00', '19:30:00', 55, 55, '1111111', 35.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L14', 'Nabeul', 'Tunis', '18:00:00', '19:15:00', 40, 40, '1111111', 15.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L15', 'Bizerte', 'Tunis', '17:30:00', '18:45:00', 45, 45, '1111111', 18.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),

-- Routes supplémentaires
('L16', 'Gabès', 'Tunis', '06:00:00', '11:00:00', 50, 50, '1111111', 45.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L17', 'Kairouan', 'Tunis', '07:00:00', '09:30:00', 45, 45, '1111111', 22.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L18', 'Monastir', 'Sfax', '08:00:00', '10:00:00', 40, 40, '1111111', 25.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L19', 'Gabès', 'Sfax', '09:00:00', '10:30:00', 45, 45, '1111111', 20.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),
('L20', 'Nabeul', 'Sousse', '10:00:00', '11:30:00', 40, 40, '1111111', 20.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)); 