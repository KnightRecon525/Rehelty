package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.utils.DatabaseInitializer;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialiser la base de données
            DatabaseInitializer.initializeDatabase();
            
            // Charger l'interface utilisateur
            Parent root = FXMLLoader.load(getClass().getResource("/admin_destination_crud.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Gestion des Destinations");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 