package tn.esprit.utils;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import javax.imageio.ImageIO;

public class DatabaseInitializer {
    
    private static final String ROOT_URL = "jdbc:mysql://localhost:3306";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    public static void initializeDatabase() {
        try {
            // Créer d'abord la base de données
            createDatabase();
            
            // Obtenir la connexion à la base de données spécifique
            Connection connection = MyDataBase.getInstance().getCnx();
            
            // Créer les tables si elles n'existent pas
            System.out.println("Initialisation de la base de données...");
            
            // Table destinations
            executeSqlScript(connection, "/sql/create_destinations.sql");
            executeSqlScript(connection, "/sql/alter_destinations.sql");
            executeSqlScript(connection, "/sql/insert_test_data.sql");
            
            // Table hotel
            System.out.println("Tentative de création de la table hotel...");
            String hotelScript = readResourceFile("/sql/create_hotel.sql");
            if (hotelScript != null && !hotelScript.trim().isEmpty()) {
                executeSqlScript(connection, "/sql/create_hotel.sql");
            } else {
                System.err.println("Le script create_hotel.sql est vide ou n'a pas pu être lu!");
            }
            
            // Table bus
            System.out.println("Tentative de création de la table bus...");
            executeSqlScript(connection, "/sql/create_bus.sql");
            
            // Vérifier les tables
            verifyTables(connection);
            
            System.out.println("Base de données initialisée avec succès!");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createDatabase() {
        try {
            System.out.println("Tentative de création de la base de données...");
            Connection rootConnection = DriverManager.getConnection(ROOT_URL, USERNAME, PASSWORD);
            Statement statement = rootConnection.createStatement();
            
            // Lire le script de création de la base de données
            String scriptPath = "/sql/create_database.sql";
            System.out.println("Tentative de lecture du fichier: " + scriptPath);
            String createDbScript = readResourceFile(scriptPath);
            System.out.println("Fichier lu avec succès: " + scriptPath);
            
            System.out.println("Tentative de connexion à la base de données...");
            System.out.println("URL: " + ROOT_URL);
            
            // Exécuter les requêtes séparément
            String[] queries = createDbScript.split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    try {
                        statement.execute(query.trim());
                        System.out.println("Requête exécutée avec succès: " + query.substring(0, Math.min(100, query.length())) + "...");
                    } catch (Exception e) {
                        // Ignorer les erreurs si la modification a déjà été appliquée
                        if (!e.getMessage().contains("Duplicate") && !e.getMessage().contains("already exists")) {
                            throw e;
                        }
                        System.out.println("Note: " + e.getMessage());
                    }
                }
            }
            
            // Vérifier et créer le fichier default-hotel.jpg s'il n'existe pas
            createDefaultImage();
            
            System.out.println("Base de données créée ou vérifiée avec succès!");
            rootConnection.close();
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verifyTables(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            
            // Vérifier la table destinations
            verifyTable(connection, "destinations");
            
            // Vérifier la table hotel
            verifyTable(connection, "hotel");
            
            // Vérifier la table bus
            verifyTable(connection, "bus");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification des tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verifyTable(Connection connection, String tableName) {
        try {
            Statement stmt = connection.createStatement();
            
            // Vérifier si la table existe
            ResultSet tables = connection.getMetaData().getTables(null, null, tableName, null);
            if (tables.next()) {
                System.out.println("Table '" + tableName + "' existe.");
                
                // Compter le nombre d'enregistrements
                ResultSet count = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
                if (count.next()) {
                    int rowCount = count.getInt(1);
                    System.out.println("Nombre d'enregistrements dans la table " + tableName + ": " + rowCount);
                    
                    if (rowCount > 0) {
                        // Afficher les enregistrements existants selon la table
                        String displayColumn = switch(tableName) {
                            case "bus" -> "numero_ligne";
                            case "hotel" -> "nom";
                            case "destinations" -> "nom";
                            default -> "nom";
                        };
                        
                        ResultSet rs = stmt.executeQuery("SELECT " + displayColumn + " FROM " + tableName);
                        System.out.println("Enregistrements existants dans " + tableName + ":");
                        while (rs.next()) {
                            System.out.println("- " + rs.getString(1));
                        }
                    }
                }
            } else {
                System.out.println("Table '" + tableName + "' n'existe pas!");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de la table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String readResourceFile(String resourcePath) {
        System.out.println("Tentative de lecture du fichier: " + resourcePath);
        
        // Essayer d'abord avec getResourceAsStream
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Impossible de trouver le fichier dans le classpath: " + resourcePath);
                // Essayer avec le chemin absolu
                File file = new File("src/main/resources" + resourcePath);
                if (file.exists()) {
                    System.out.println("Fichier trouvé dans le système de fichiers: " + file.getAbsolutePath());
                    return new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                } else {
                    System.err.println("Fichier introuvable même dans le système de fichiers: " + file.getAbsolutePath());
                    return null;
                }
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                        content.append(line).append(" ");
                    }
                }
                System.out.println("Fichier lu avec succès: " + resourcePath);
                return content.toString();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier " + resourcePath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static void executeSqlScript(Connection connection, String scriptPath) {
        try {
            System.out.println("Exécution du script: " + scriptPath);
            String script = readResourceFile(scriptPath);
            
            if (script == null || script.trim().isEmpty()) {
                System.err.println("Le script est vide ou n'a pas pu être lu: " + scriptPath);
                return;
            }
            
            // Exécuter les requêtes SQL
            Statement statement = connection.createStatement();
            String[] queries = script.split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    try {
                        statement.execute(query.trim());
                        System.out.println("Requête exécutée avec succès: " + query.substring(0, Math.min(100, query.length())) + "...");
                    } catch (Exception e) {
                        // Ignorer les erreurs si la modification a déjà été appliquée
                        if (!e.getMessage().contains("Duplicate") && !e.getMessage().contains("already exists")) {
                            throw e;
                        }
                        System.out.println("Note: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du script SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crée une image par défaut pour les hôtels si elle n'existe pas déjà
     */
    private static void createDefaultImage() {
        try {
            // Chemin où l'image devrait être
            String defaultImagePath = "src/main/resources/image/default-hotel.jpg";
            File defaultImage = new File(defaultImagePath);
            
            if (!defaultImage.exists()) {
                System.out.println("L'image par défaut n'existe pas, création en cours...");
                
                // Vérifier si le dossier existe, sinon le créer
                File directory = defaultImage.getParentFile();
                if (!directory.exists()) {
                    directory.mkdirs();
                    System.out.println("Dossier image créé: " + directory.getAbsolutePath());
                }
                
                // Essayer de copier une image existante comme image par défaut
                File[] imageFiles = directory.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".jpg") || 
                    name.toLowerCase().endsWith(".png") || 
                    name.toLowerCase().endsWith(".jpeg"));
                
                if (imageFiles != null && imageFiles.length > 0) {
                    // Utiliser la première image trouvée
                    File sourceImage = imageFiles[0];
                    java.nio.file.Files.copy(sourceImage.toPath(), defaultImage.toPath());
                    System.out.println("Image par défaut créée à partir de: " + sourceImage.getName());
                } else {
                    // Créer une image par défaut simple
                    createSimpleDefaultImage(defaultImage);
                    System.out.println("Image par défaut simple créée");
                }
            } else {
                System.out.println("L'image par défaut existe déjà: " + defaultImage.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'image par défaut: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crée une image simple comme image par défaut
     */
    private static void createSimpleDefaultImage(File outputFile) throws IOException {
        // Taille de l'image
        int width = 400;
        int height = 300;
        
        // Créer une image vide
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Dessiner un fond de couleur claire
        g.setColor(new Color(220, 220, 240));
        g.fillRect(0, 0, width, height);
        
        // Dessiner un cadre
        g.setColor(new Color(100, 100, 180));
        g.setStroke(new BasicStroke(10));
        g.drawRect(5, 5, width-10, height-10);
        
        // Ajouter du texte
        g.setColor(new Color(60, 60, 120));
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String text = "Hôtel";
        FontMetrics metrics = g.getFontMetrics();
        int x = (width - metrics.stringWidth(text)) / 2;
        int y = ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, x, y);
        
        // Ajouter "Image non disponible"
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String subtext = "Image non disponible";
        metrics = g.getFontMetrics();
        x = (width - metrics.stringWidth(subtext)) / 2;
        y = y + metrics.getHeight() + 10;
        g.drawString(subtext, x, y);
        
        g.dispose();
        
        // Enregistrer l'image
        ImageIO.write(img, "jpg", outputFile);
    }
} 