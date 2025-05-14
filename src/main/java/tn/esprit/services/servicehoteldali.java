package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Hoteldali;
import tn.esprit.models.Hoteldali;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class servicehoteldali implements IService<Hoteldali> {

    private Connection cnx;

    public servicehoteldali() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Hoteldali hotel) {
        try {
            // Vérifier si la colonne imageUrl existe
            boolean hasImageUrlColumn = checkColumnExists("imageUrl");
            boolean hasImageUnderscoreUrlColumn = checkColumnExists("image_url");

            // Construction de la requête SQL en fonction des colonnes disponibles
            String query;
            if (hasImageUrlColumn) {
                query = "INSERT INTO hotel (nom, localisation, description, nbrChambres, prixParNuite, etoiles, equipements, imageUrl, note) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else if (hasImageUnderscoreUrlColumn) {
                query = "INSERT INTO hotel (nom, localisation, description, nbrChambres, prixParNuite, etoiles, equipements, image_url, note) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                // Cas où aucune colonne d'image n'existe, on l'ajoute
                Statement stmt = cnx.createStatement();
                stmt.execute("ALTER TABLE hotel ADD COLUMN imageUrl VARCHAR(1000)");
                System.out.println("Colonne imageUrl ajoutée à la table hotel");

                query = "INSERT INTO hotel (nom, localisation, description, nbrChambres, prixParNuite, etoiles, equipements, imageUrl, note) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }

            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setString(1, hotel.getNom());
            ps.setString(2, hotel.getLocalisation());
            ps.setString(3, hotel.getDescription());
            ps.setInt(4, hotel.getNbrChambres());
            ps.setFloat(5, hotel.getPrixParNuite());
            ps.setInt(6, hotel.getEtoiles());
            ps.setString(7, hotel.getEquipements());
            ps.setString(8, hotel.getImageUrl());
            ps.setFloat(9, hotel.getNote());

            ps.executeUpdate();
            System.out.println("Hôtel ajouté avec succès: " + hotel.getNom());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'hôtel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Hoteldali> getAll() {
        List<Hoteldali> hotels = new ArrayList<>();

        try {
            // Vérifier la connexion à la base de données
            if (cnx == null || cnx.isClosed()) {
                System.out.println("Connexion à la base de données perdue, tentative de reconnexion...");
                cnx = MyDataBase.getInstance().getCnx();
            }

            Statement stmt = cnx.createStatement();

            // Vérifier si la table est vide, si oui, charger les données
            ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM hotel");
            int count = 0;
            if (countRs.next()) {
                count = countRs.getInt(1);
            }
            System.out.println("Nombre d'hôtels dans la base de données: " + count);

            if (count == 0) {
                System.out.println("🔄 La table hôtel est vide, ajout d'hôtels...");

                // Ajouter plusieurs hôtels manuellement
                addSampleHotels();
            }

            // Vérifier si les colonnes existent
            boolean hasImageUrlColumn = checkColumnExists("imageUrl");
            boolean hasImageUnderscoreUrlColumn = checkColumnExists("image_url");

            // Charger tous les hôtels
            String query = "SELECT * FROM hotel ORDER BY nom";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Hoteldali hotel = new Hoteldali();
                hotel.setId(rs.getInt("id"));
                hotel.setNom(rs.getString("nom"));
                hotel.setLocalisation(rs.getString("localisation"));
                hotel.setDescription(rs.getString("description"));
                hotel.setNbrChambres(rs.getInt("nbrChambres"));
                hotel.setPrixParNuite(rs.getFloat("prixParNuite"));
                hotel.setEtoiles(rs.getInt("etoiles"));

                // Gestion de la colonne equipements
                String equipements = rs.getString("equipements");
                hotel.setEquipements(equipements != null ? equipements : "");

                // Gestion des colonnes imageUrl ou image_url
                String imageUrl = null;
                if (hasImageUrlColumn) {
                    try {
                        imageUrl = rs.getString("imageUrl");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la lecture de imageUrl: " + e.getMessage());
                    }
                }

                if (imageUrl == null && hasImageUnderscoreUrlColumn) {
                    try {
                        imageUrl = rs.getString("image_url");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la lecture de image_url: " + e.getMessage());
                    }
                }

                hotel.setImageUrl(imageUrl);

                // Gestion de la colonne note
                hotel.setNote(rs.getFloat("note"));

                hotels.add(hotel);
                System.out.println("✅ Chargé hôtel: " + hotel.getNom() + " (" + hotel.getLocalisation() + ")");
            }

            System.out.println("🏁 Total: " + hotels.size() + " hôtels chargés");

            // Si aucun hôtel n'a été chargé, ajouter au moins un hôtel manuellement
            if (hotels.isEmpty()) {
                Hoteldali hotel = createDefaultHotel();
                hotels.add(hotel);
                System.out.println("⚠️ Aucun hôtel chargé depuis la base, ajout d'un hôtel par défaut");
            }

        } catch (SQLException ex) {
            System.out.println("❌ Erreur SQL: " + ex.getMessage());
            ex.printStackTrace();

            // En cas d'erreur, créer au moins un hôtel par défaut
            Hoteldali hotel = createDefaultHotel();
            hotels.add(hotel);
        } catch (Exception e) {
            System.out.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();

            // En cas d'erreur, créer au moins un hôtel par défaut
            Hoteldali hotel = createDefaultHotel();
            hotels.add(hotel);
        }

        return hotels;
    }

    @Override
    public void update(Hoteldali hotel) {
        String query = "UPDATE hotel SET nom=?, localisation=?, description=?, nbrChambres=?, " +
                "prixParNuite=?, etoiles=?, equipements=?, imageUrl=?, note=? WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setString(1, hotel.getNom());
            ps.setString(2, hotel.getLocalisation());
            ps.setString(3, hotel.getDescription());
            ps.setInt(4, hotel.getNbrChambres());
            ps.setFloat(5, hotel.getPrixParNuite());
            ps.setInt(6, hotel.getEtoiles());
            ps.setString(7, hotel.getEquipements());
            ps.setString(8, hotel.getImageUrl());
            ps.setFloat(9, hotel.getNote());
            ps.setInt(10, hotel.getId());

            ps.executeUpdate();
            System.out.println("Hôtel mis à jour: " + hotel.getNom());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'hôtel: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Hoteldali hotel) {
        String query = "DELETE FROM hotel WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, hotel.getId());
            ps.executeUpdate();
            System.out.println("Hôtel supprimé: " + hotel.getNom());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'hôtel: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Hoteldali> getByVille(String ville) {
        List<Hoteldali> hotels = new ArrayList<>();
        String query = "SELECT * FROM hotel WHERE localisation = ?";

        try {
            // Vérifier si les colonnes existent
            boolean hasImageUrlColumn = checkColumnExists("imageUrl");
            boolean hasImageUnderscoreUrlColumn = checkColumnExists("image_url");

            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setString(1, ville);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Hoteldali h = new Hoteldali();
                h.setId(rs.getInt("id"));
                h.setNom(rs.getString("nom"));
                h.setLocalisation(rs.getString("localisation"));
                h.setDescription(rs.getString("description"));
                h.setNbrChambres(rs.getInt("nbrChambres"));
                h.setPrixParNuite(rs.getFloat("prixParNuite"));
                h.setEtoiles(rs.getInt("etoiles"));
                h.setEquipements(rs.getString("equipements"));

                // Gestion des colonnes imageUrl ou image_url
                String imageUrl = null;
                if (hasImageUrlColumn) {
                    try {
                        imageUrl = rs.getString("imageUrl");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la lecture de imageUrl: " + e.getMessage());
                    }
                }

                if (imageUrl == null && hasImageUnderscoreUrlColumn) {
                    try {
                        imageUrl = rs.getString("image_url");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la lecture de image_url: " + e.getMessage());
                    }
                }

                h.setImageUrl(imageUrl);
                h.setNote(rs.getFloat("note"));
                hotels.add(h);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par ville: " + e.getMessage());
            e.printStackTrace();
        }
        return hotels;
    }

    public Hoteldali getByName(String nom) {
        String query = "SELECT * FROM hotel WHERE nom = ?";
        Hoteldali hotel = null;

        try {
            // Vérifier si les colonnes existent
            boolean hasImageUrlColumn = checkColumnExists("imageUrl");
            boolean hasImageUnderscoreUrlColumn = checkColumnExists("image_url");

            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                hotel = new Hoteldali();
                hotel.setId(rs.getInt("id"));
                hotel.setNom(rs.getString("nom"));
                hotel.setLocalisation(rs.getString("localisation"));
                hotel.setDescription(rs.getString("description"));
                hotel.setNbrChambres(rs.getInt("nbrChambres"));
                hotel.setPrixParNuite(rs.getFloat("prixParNuite"));
                hotel.setEtoiles(rs.getInt("etoiles"));
                hotel.setEquipements(rs.getString("equipements"));

                // Gestion des colonnes imageUrl ou image_url
                String imageUrl = null;
                if (hasImageUrlColumn) {
                    try {
                        imageUrl = rs.getString("imageUrl");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la lecture de imageUrl: " + e.getMessage());
                    }
                }

                if (imageUrl == null && hasImageUnderscoreUrlColumn) {
                    try {
                        imageUrl = rs.getString("image_url");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la lecture de image_url: " + e.getMessage());
                    }
                }

                hotel.setImageUrl(imageUrl);
                hotel.setNote(rs.getFloat("note"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'hôtel: " + e.getMessage());
            e.printStackTrace();
        }

        return hotel;
    }

    /**
     * Crée un hôtel par défaut pour l'affichage
     */
    private Hoteldali createDefaultHotel() {
        Hoteldali hotel = new Hoteldali();
        hotel.setId(1);
        hotel.setNom("Hôtel Le Paradis");
        hotel.setLocalisation("Tunis");
        hotel.setDescription("Hôtel de luxe au cœur de la capitale avec vue panoramique");
        hotel.setNbrChambres(120);
        hotel.setPrixParNuite(250);
        hotel.setEtoiles(5);
        hotel.setEquipements("Piscine,Wi-Fi,Restaurant,Spa,Salle de sport");
        hotel.setImageUrl("/image/default-hotel.jpg");
        hotel.setNote(4.8f);

        try {
            // Tenter d'ajouter cet hôtel à la base de données
            this.add(hotel);
        } catch (Exception e) {
            System.out.println("⚠️ Impossible d'ajouter l'hôtel par défaut à la base: " + e.getMessage());
        }

        return hotel;
    }

    /**
     * Ajoute des hôtels d'exemple à la base de données
     */
    private void addSampleHotels() {
        try {
            System.out.println("🏨 Ajout d'hôtels d'exemple à la base de données...");

            List<Hoteldali> sampleHotels = new ArrayList<>();

            // Hôtel 1
            Hoteldali hotel1 = new Hoteldali();
            hotel1.setNom("Hôtel Dar El Jeld");
            hotel1.setLocalisation("Tunis");
            hotel1.setDescription("Un hôtel de luxe au cœur de la Médina");
            hotel1.setNbrChambres(50);
            hotel1.setPrixParNuite(450);
            hotel1.setEtoiles(5);
            hotel1.setEquipements("Piscine,Spa,Wi-Fi,Restaurant");
            hotel1.setImageUrl("/image/dareljeld.jpg");
            hotel1.setNote(4.8f);
            sampleHotels.add(hotel1);

            // Hôtel 2
            Hoteldali hotel2 = new Hoteldali();
            hotel2.setNom("The Sindbad");
            hotel2.setLocalisation("Hammamet");
            hotel2.setDescription("Complexe de luxe en bord de mer");
            hotel2.setNbrChambres(145);
            hotel2.setPrixParNuite(380);
            hotel2.setEtoiles(5);
            hotel2.setEquipements("Piscine,Plage Privée,Spa,Wi-Fi,Restaurant");
            hotel2.setImageUrl("/image/hammamet.jpg");
            hotel2.setNote(4.6f);
            sampleHotels.add(hotel2);

            // Hôtel 3
            Hoteldali hotel3 = new Hoteldali();
            hotel3.setNom("Movenpick Resort & Marine Spa");
            hotel3.setLocalisation("Sousse");
            hotel3.setDescription("Luxe contemporain en bord de mer");
            hotel3.setNbrChambres(250);
            hotel3.setPrixParNuite(350);
            hotel3.setEtoiles(5);
            hotel3.setEquipements("Piscine,Spa,Wi-Fi,Restaurant,Plage Privée");
            hotel3.setImageUrl("/image/sousse.webp");
            hotel3.setNote(4.7f);
            sampleHotels.add(hotel3);

            // Hôtel 4
            Hoteldali hotel4 = new Hoteldali();
            hotel4.setNom("Radisson Blu Palace Resort");
            hotel4.setLocalisation("Djerba");
            hotel4.setDescription("Oasis de luxe sur la plage");
            hotel4.setNbrChambres(296);
            hotel4.setPrixParNuite(400);
            hotel4.setEtoiles(5);
            hotel4.setEquipements("Piscine,Thalasso,Wi-Fi,Restaurant,Golf");
            hotel4.setImageUrl("/image/djerba.jpg");
            hotel4.setNote(4.8f);
            sampleHotels.add(hotel4);

            // Hôtel 5
            Hoteldali hotel5 = new Hoteldali();
            hotel5.setNom("Bizerta Resort");
            hotel5.setLocalisation("Bizerte");
            hotel5.setDescription("Vue panoramique sur la mer");
            hotel5.setNbrChambres(172);
            hotel5.setPrixParNuite(270);
            hotel5.setEtoiles(4);
            hotel5.setEquipements("Piscine,Spa,Wi-Fi,Restaurant");
            hotel5.setImageUrl("/image/bizerte.avif");
            hotel5.setNote(4.3f);
            sampleHotels.add(hotel5);

            // Ajouter tous les hôtels à la base de données
            for (Hoteldali hotel : sampleHotels) {
                try {
                    this.add(hotel);
                    System.out.println("✅ Ajouté: " + hotel.getNom());
                } catch (Exception e) {
                    System.out.println("❌ Erreur lors de l'ajout de " + hotel.getNom() + ": " + e.getMessage());
                }
            }

            System.out.println("🏁 Fin de l'ajout des hôtels d'exemple");

        } catch (Exception e) {
            System.out.println("❌ Erreur générale lors de l'ajout des hôtels d'exemple: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si une colonne existe dans la table hotel
     * @param columnName Nom de la colonne à vérifier
     * @return true si la colonne existe, false sinon
     */
    private boolean checkColumnExists(String columnName) {
        try {
            DatabaseMetaData metaData = cnx.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, "hotel", columnName);
            return rs.next(); // Si rs.next() retourne true, la colonne existe
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la colonne " + columnName + ": " + e.getMessage());
            return false;
        }
    }
}