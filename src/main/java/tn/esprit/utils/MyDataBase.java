package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static MyDataBase instance;
    private final String ROOT_URL = "jdbc:mysql://localhost:3306";
    private final String DB_URL = "jdbc:mysql://localhost:3306/rehelty";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private Connection cnx;

    private MyDataBase() {
        try {
            System.out.println("Tentative de connexion à la base de données...");
            System.out.println("URL: " + DB_URL);
            
            // Try to connect to the database
            try {
                cnx = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                System.out.println("Connexion établie avec succès!");
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Base 'rehelty' inconnue")) {
                    System.out.println("Base de données non trouvée, tentative de création...");
                    // Try to create the database
                    Connection rootCnx = DriverManager.getConnection(ROOT_URL, USERNAME, PASSWORD);
                    rootCnx.createStatement().execute("CREATE DATABASE IF NOT EXISTS rehelty");
                    rootCnx.close();
                    
                    // Try to connect again
                    cnx = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                    System.out.println("Base de données créée et connexion établie avec succès!");
                } else {
                    throw ex;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Erreur de connexion à la base de données:");
            System.err.println("URL: " + DB_URL);
            System.err.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null)
            instance = new MyDataBase();
        return instance;
    }

    public Connection getCnx() {
        try {
            // Vérifier si la connexion est toujours valide
            if (cnx == null || cnx.isClosed()) {
                System.out.println("Reconnexion à la base de données...");
                cnx = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                System.out.println("Reconnexion réussie!");
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la vérification/reconnexion à la base de données:");
            System.err.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }
        return cnx;
    }
}
