-- Créer la table hotel si elle n'existe pas
CREATE TABLE IF NOT EXISTS `hotel` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `nom` VARCHAR(100) NOT NULL,
    `localisation` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `nbrChambres` INT NOT NULL,
    `prixParNuite` FLOAT NOT NULL,
    `etoiles` INT NOT NULL,
    `equipements` TEXT,
    `image_url` VARCHAR(1000), -- Attention: utiliser le même nom que dans le code Java (imageUrl ou image_url)
    `note` FLOAT DEFAULT 0,
    UNIQUE KEY `unique_hotel` (`nom`, `localisation`)
);

-- Supprimer les données existantes pour éviter les doublons
TRUNCATE TABLE `hotel`;

-- Ajouter la colonne imageUrl si elle n'existe pas
ALTER TABLE `hotel` ADD COLUMN IF NOT EXISTS `imageUrl` VARCHAR(1000);

-- Insérer des données de test
INSERT INTO `hotel` (`nom`, `localisation`, `description`, `nbrChambres`, `prixParNuite`, `etoiles`, `equipements`, `image_url`, `note`) VALUES
-- Tunis
('Hôtel Dar El Jeld', 'Tunis', 'Un hôtel de luxe au cœur de la Médina', 50, 450.0, 5, 'Piscine,Spa,Wi-Fi,Restaurant', '/image/dareljeld.jpg', 4.8),
('Movenpick Hotel Lac 1', 'Tunis', 'Vue imprenable sur le lac de Tunis', 120, 350.0, 5, 'Piscine,Spa,Wi-Fi,Restaurant,Salle de Gym', '/image/default-hotel.jpg', 4.5),
('Laico Tunis', 'Tunis', 'Hôtel élégant au centre-ville', 140, 280.0, 5, 'Piscine,Wi-Fi,Restaurant,Business Center', '/image/default-hotel.jpg', 4.2),
('Golden Tulip El Mechtel', 'Tunis', 'Près des principales attractions', 250, 220.0, 4, 'Wi-Fi,Restaurant,Parking', '/image/default-hotel.jpg', 4.0),
('Hotel Carlton', 'Tunis', 'Hôtel historique au centre de Tunis', 85, 120.0, 3, 'Wi-Fi,Restaurant', '/image/default-hotel.jpg', 3.5),

-- Hammamet
('The Sindbad', 'Hammamet', 'Complexe de luxe en bord de mer', 145, 380.0, 5, 'Piscine,Plage Privée,Spa,Wi-Fi,Restaurant', '/image/hammamet.jpg', 4.6),
('La Badira', 'Hammamet', 'Hôtel de luxe adultes uniquement', 130, 420.0, 5, 'Piscine,Spa,Wi-Fi,Restaurant,Accès Plage', '/image/default-hotel.jpg', 4.9),
('Hasdrubal Thalassa & Spa', 'Hammamet', 'Centre de thalassothérapie de renommée', 192, 300.0, 4, 'Thalasso,Piscine,Wi-Fi,Restaurant', '/image/default-hotel.jpg', 4.4),
('Medina Solaria & Thalasso', 'Hammamet', 'Hôtel familial avec parc aquatique', 239, 220.0, 4, 'Piscine,Parc Aquatique,Wi-Fi,Animation', '/image/default-hotel.jpg', 4.1),
('Hotel Nesrine', 'Hammamet', 'Bon rapport qualité-prix près de la plage', 280, 150.0, 3, 'Piscine,Wi-Fi,Restaurant', '/image/default-hotel.jpg', 3.7),

-- Sousse
('Movenpick Resort & Marine Spa', 'Sousse', 'Luxe contemporain en bord de mer', 250, 350.0, 5, 'Piscine,Spa,Wi-Fi,Restaurant,Plage Privée', '/image/sousse.webp', 4.7),
('Marhaba Palace', 'Sousse', 'Style architectural arabo-mauresque', 322, 190.0, 4, 'Piscine,Wi-Fi,Animation,Jardins', '/image/default-hotel.jpg', 4.0),
('Jaz Tour Khalef', 'Sousse', 'Station balnéaire complète', 230, 230.0, 4, 'Piscine,Mini-Golf,Spa,Wi-Fi', '/image/default-hotel.jpg', 4.2),
('Kaiser Hotel', 'Sousse', 'Au cœur de la médina', 110, 140.0, 3, 'Wi-Fi,Restaurant', '/image/default-hotel.jpg', 3.6),
('Hotel Tej Marhaba', 'Sousse', 'Ambiance conviviale pour famille', 260, 160.0, 3, 'Piscine,Animation,Wi-Fi', '/image/default-hotel.jpg', 3.5),

-- Djerba
('Radisson Blu Palace Resort & Thalasso', 'Djerba', 'Oasis de luxe sur la plage', 296, 400.0, 5, 'Piscine,Thalasso,Wi-Fi,Restaurant,Golf', '/image/djerba.jpg', 4.8),
('Seabel Rym Beach', 'Djerba', 'Architecture insulaire traditionnelle', 354, 200.0, 4, 'Piscine,Plage Privée,Wi-Fi,Sports Nautiques', '/image/default-hotel.jpg', 4.1),
('Djerba Holiday Beach', 'Djerba', 'Parfait pour vacances familiales', 308, 180.0, 3, 'Piscine,Animation,Mini-Club,Wi-Fi', '/image/default-hotel.jpg', 3.8),
('Cesar Thalasso', 'Djerba', 'Centre de thalassothérapie renommé', 286, 260.0, 4, 'Thalasso,Piscine,Wi-Fi,Restaurant', '/image/default-hotel.jpg', 4.3),
('Hotel Sidi Mansour', 'Djerba', 'Style architectural local authentique', 145, 160.0, 3, 'Piscine,Wi-Fi,Restaurant', '/image/default-hotel.jpg', 3.7),

-- Monastir
('Royal Thalassa Monastir', 'Monastir', 'Sur la côte dorée de Monastir', 280, 290.0, 5, 'Thalasso,Piscine,Wi-Fi,Restaurant,Spa', '/image/default-hotel.jpg', 4.5),
('Skanes Serail', 'Monastir', 'Au cœur de la zone touristique de Skanes', 214, 170.0, 4, 'Piscine,Animation,Wi-Fi,Restaurant', '/image/default-hotel.jpg', 4.0),
('One Resort Aqua Park', 'Monastir', 'Complexe avec parc aquatique', 390, 210.0, 4, 'Parc Aquatique,Piscine,Animation,Wi-Fi', '/image/default-hotel.jpg', 4.2),
('Sahara Beach', 'Monastir', 'Grand resort en bord de mer', 630, 160.0, 3, 'Piscine,Animation,Mini-Club,Wi-Fi', '/image/default-hotel.jpg', 3.6),
('Delphin El Habib', 'Monastir', 'Proche du centre historique', 200, 140.0, 3, 'Piscine,Wi-Fi,Restaurant', '/image/default-hotel.jpg', 3.5),

-- Tabarka
('La Cigale Tabarka', 'Tabarka', 'Luxe avec vue sur la Méditerranée', 248, 320.0, 5, 'Golf,Piscine,Spa,Wi-Fi,Plage Privée', '/image/default-hotel.jpg', 4.6),
('Mimosa Tabarka', 'Tabarka', 'Entouré de pins et d\'eucalyptus', 230, 180.0, 4, 'Piscine,Wi-Fi,Restaurant,Jardins', '/image/default-hotel.jpg', 4.0),
('Hotel El Morjane', 'Tabarka', 'Établissement familial confortable', 160, 130.0, 3, 'Piscine,Wi-Fi,Restaurant', '/image/default-hotel.jpg', 3.7),

-- Bizerte
('Bizerta Resort', 'Bizerte', 'Vue panoramique sur la mer', 172, 270.0, 4, 'Piscine,Spa,Wi-Fi,Restaurant', '/image/bizerte.avif', 4.3),
('Hotel Corniche Palace', 'Bizerte', 'Sur la corniche de Bizerte', 150, 190.0, 4, 'Piscine,Wi-Fi,Restaurant,Vue Mer', '/image/default-hotel.jpg', 4.1),
('Dahlia Inn', 'Bizerte', 'Charme méditerranéen', 86, 135.0, 3, 'Wi-Fi,Restaurant,Terrasse', '/image/default-hotel.jpg', 3.6),

-- Zaghouan
('Dar Zaghouan', 'Zaghouan', 'Maison d\'hôtes traditionnelle dans les montagnes', 30, 280.0, 4, 'Piscine,Wi-Fi,Restaurant Bio,Randonnées', '/image/zaghouan1.jpg', 4.5),
('Eden Village Zaghouan', 'Zaghouan', 'Immersion dans la nature', 45, 220.0, 3, 'Piscine,Wi-Fi,Produits Locaux,Jardin', '/image/default-hotel.jpg', 4.2),

-- Tozeur
('Anantara Tozeur Resort', 'Tozeur', 'Luxe au cœur du désert', 93, 700.0, 5, 'Piscine,Spa,Wi-Fi,Restaurant Gastronomique,Excursions', '/image/default-hotel.jpg', 4.9),
('Palm Beach Palace', 'Tozeur', 'Élégance saharienne', 120, 380.0, 5, 'Piscine,Spa,Wi-Fi,Tennis', '/image/default-hotel.jpg', 4.6),
('Hotel Ksar Rouge', 'Tozeur', 'Architecture traditionnelle', 86, 220.0, 4, 'Piscine,Wi-Fi,Restaurant', '/image/default-hotel.jpg', 4.1),

-- Kairouan
('La Kasbah', 'Kairouan', 'Au cœur de la médina', 100, 260.0, 4, 'Piscine,Wi-Fi,Restaurant,Vue Médina', '/image/default-hotel.jpg', 4.2),
('Hotel Continental', 'Kairouan', 'Style colonial rénové', 80, 190.0, 3, 'Wi-Fi,Restaurant,Terrasse', '/image/default-hotel.jpg', 3.8),

-- Gabes
('Oasis Hotel', 'Gabes', 'Au milieu des palmeraies', 120, 220.0, 4, 'Piscine,Wi-Fi,Restaurant', '/image/gabes-600.jpg', 4.0),
('Hotel Seabel Aladin', 'Gabes', 'En bord de mer', 130, 190.0, 3, 'Piscine,Wi-Fi,Animation', '/image/default-hotel.jpg', 3.7),

-- Sfax
('Les Oliviers Palace', 'Sfax', 'Hôtel d\'affaires de luxe', 137, 260.0, 5, 'Piscine,Spa,Wi-Fi,Business Center', '/image/default-hotel.jpg', 4.4),
('Mercure Sfax', 'Sfax', 'Standard international au centre-ville', 126, 210.0, 4, 'Wi-Fi,Restaurant,Salle de Conférences', '/image/default-hotel.jpg', 4.1); 