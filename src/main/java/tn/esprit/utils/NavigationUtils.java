package tn.esprit.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe utilitaire pour gérer la navigation entre les différentes interfaces de l'application
 */
public class NavigationUtils {

    /**
     * Charge une nouvelle vue FXML et l'affiche dans la fenêtre actuelle
     * 
     * @param button Le bouton qui a déclenché la navigation (pour obtenir la scène actuelle)
     * @param fxmlPath Le chemin vers le fichier FXML à charger
     * @param title Le titre de la fenêtre (facultatif)
     */
    public static void navigateTo(Button button, String fxmlPath, String title) {
        try {
            // Récupérer la scène actuelle
            Scene currentScene = button.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            // Charger la nouvelle vue
            FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Remplacer la scène actuelle
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Mettre à jour le titre si nécessaire
            if (title != null && !title.isEmpty()) {
                stage.setTitle(title);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation vers " + fxmlPath);
            e.printStackTrace();
        }
    }
} 