package tn.esprit.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.models.Destinations;
import tn.esprit.services.ServiceDestinations;

public class DestinationCardController {
    @FXML private Label nomLabel;
    @FXML private Label urlLabel;
    @FXML private ImageView destinationImage;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
    @FXML private Tooltip urlTooltip;

    private Destinations destination;
    private ServiceDestinations serviceDestinations;
    private Runnable refreshCallback;
    private AdminDestinationController parentController;
    
    // Base64 encoded 1x1 pixel transparent PNG
    private static final String FALLBACK_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";

    @FXML
    private void initialize() {
        modifierBtn.setOnAction(e -> handleModifier());
        supprimerBtn.setOnAction(e -> handleSupprimer());
        
        // Set default background color for the ImageView
        destinationImage.setStyle("-fx-background-color: #f0f0f0;");
    }

    public void setDestination(Destinations destination) {
        this.destination = destination;
        updateCardContent();
    }

    public void setServiceDestinations(ServiceDestinations serviceDestinations) {
        this.serviceDestinations = serviceDestinations;
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    public void setParentController(AdminDestinationController controller) {
        this.parentController = controller;
    }

    private void updateCardContent() {
        if (destination != null) {
            nomLabel.setText(destination.getNom());
            String url = destination.getUrl();
            
            // Truncate URL for display
            String displayUrl = url.length() > 40 ? url.substring(0, 37) + "..." : url;
            urlLabel.setText(displayUrl);
            
            // Set full URL as tooltip
            urlTooltip.setText(url);
            
            loadImage(url);
        }
    }

    private void loadImage(String url) {
        try {
            // Create placeholder image first
            Image placeholder = new Image(FALLBACK_IMAGE);
            destinationImage.setImage(placeholder);
            
            // Then try to load the actual image
            Image image = new Image(url, true);
            
            image.errorProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    System.err.println("Erreur de chargement de l'image: " + url);
                    // Keep the placeholder image
                    destinationImage.setImage(placeholder);
                }
            });
            
            image.progressProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() == 1.0 && !image.isError()) {
                    destinationImage.setImage(image);
                }
            });
            
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
            // The placeholder image will remain
        }
    }

    private void handleModifier() {
        if (parentController != null) {
            parentController.setSelectedDestination(destination);
        }
    }

    private void handleSupprimer() {
        if (destination != null && serviceDestinations != null) {
            serviceDestinations.delete(destination);
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        }
    }
} 