package tn.esprit.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import tn.esprit.models.Destinations;
import tn.esprit.services.ServiceDestinations;

import java.io.IOException;
import java.util.List;

public class AdminDestinationController {
    @FXML private FlowPane destinationCardsContainer;
    @FXML private TextField tfNom;
    @FXML private TextField tfUrl;
    @FXML private TextField searchField;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnActualiser;

    private ServiceDestinations serviceDestinations;
    private Destinations selectedDestination;

    @FXML
    private void initialize() {
        serviceDestinations = new ServiceDestinations();
        setupSearch();
        setupButtons();
        refreshDestinationCards();
        System.out.println("Controller initialized"); // Debug log
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterDestinationCards();
            }
        });
    }

    private void setupButtons() {
        btnAjouter.setOnAction(e -> handleAjouter());
        btnModifier.setOnAction(e -> handleModifier());
        btnSupprimer.setOnAction(e -> handleSupprimer());
        btnActualiser.setOnAction(e -> refreshDestinationCards());
    }

    private void refreshDestinationCards() {
        try {
            destinationCardsContainer.getChildren().clear();
            List<Destinations> destinations = serviceDestinations.getAll();
            System.out.println("Nombre de destinations chargées: " + destinations.size()); // Debug log
            
            for (Destinations destination : destinations) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/destination_card.fxml"));
                    Node destinationCard = loader.load();
                    DestinationCardController controller = loader.getController();
                    
                    controller.setDestination(destination);
                    controller.setServiceDestinations(serviceDestinations);
                    controller.setRefreshCallback(this::refreshDestinationCards);
                    controller.setParentController(this);
                    
                    destinationCardsContainer.getChildren().add(destinationCard);
                    System.out.println("Carte ajoutée pour: " + destination.getNom()); // Debug log
                } catch (IOException e) {
                    System.err.println("Erreur lors du chargement de la carte: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la carte de destination");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du rafraîchissement des cartes: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les destinations");
        }
    }

    private void filterDestinationCards() {
        String searchText = searchField.getText().toLowerCase();
        destinationCardsContainer.getChildren().clear();
        List<Destinations> destinations = serviceDestinations.getAll();

        for (Destinations destination : destinations) {
            if (destination.getNom().toLowerCase().contains(searchText)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/destination_card.fxml"));
                    Node destinationCard = loader.load();
                    DestinationCardController controller = loader.getController();
                    controller.setDestination(destination);
                    controller.setServiceDestinations(serviceDestinations);
                    controller.setRefreshCallback(this::refreshDestinationCards);
                    controller.setParentController(this);
                    destinationCardsContainer.getChildren().add(destinationCard);
                } catch (IOException e) {
                    System.err.println("Erreur lors du filtrage: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleAjouter() {
        try {
            validateFields();
            
            Destinations newDestination = new Destinations();
            newDestination.setNom(tfNom.getText());
            newDestination.setUrl(tfUrl.getText());

            serviceDestinations.add(newDestination);
            clearFields();
            refreshDestinationCards();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Destination ajoutée avec succès");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void handleModifier() {
        if (selectedDestination == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une destination à modifier");
            return;
        }

        try {
            validateFields();
            
            selectedDestination.setNom(tfNom.getText());
            selectedDestination.setUrl(tfUrl.getText());

            serviceDestinations.update(selectedDestination);
            clearFields();
            refreshDestinationCards();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Destination modifiée avec succès");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void handleSupprimer() {
        if (selectedDestination == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une destination à supprimer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la destination");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette destination ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            serviceDestinations.delete(selectedDestination);
            clearFields();
            refreshDestinationCards();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Destination supprimée avec succès");
        }
    }

    public void setSelectedDestination(Destinations destination) {
        this.selectedDestination = destination;
        if (destination != null) {
            tfNom.setText(destination.getNom());
            tfUrl.setText(destination.getUrl());
        }
    }

    private void validateFields() {
        if (tfNom.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la destination est requis");
        }
        if (tfUrl.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("L'URL de l'image est requise");
        }
        /*if (!tfUrl.getText().matches("^https?://.*")) {
            throw new IllegalArgumentException("L'URL doit commencer par http:// ou https://");
        }

         */
    }

    private void clearFields() {
        tfNom.clear();
        tfUrl.clear();
        selectedDestination = null;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 