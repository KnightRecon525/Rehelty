package tn.esprit.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import tn.esprit.models.Bus;
import tn.esprit.services.ServiceBus;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public class AdminBusController {
    @FXML private FlowPane busCardsContainer;
    @FXML private TextField tfLigne;
    @FXML private TextField tfDepart;
    @FXML private TextField tfArrivee;
    @FXML private TextField tfHeure;
    @FXML private TextField tfJours;
    @FXML private TextField tfCapacite;
    @FXML private TextField tfPrix;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnActualiser;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;

    private ServiceBus serviceBus;
    private Bus selectedBus;

    @FXML
    private void initialize() {
        serviceBus = new ServiceBus();
        setupSearchAndFilter();
        setupButtons();
        refreshBusCards();
    }

    private void setupSearchAndFilter() {
        filterComboBox.getItems().addAll("Tous", "Ligne", "Départ", "Arrivée");
        filterComboBox.setValue("Tous");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterBusCards());
        filterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterBusCards());
    }

    private void setupButtons() {
        btnAjouter.setOnAction(e -> handleAjouter());
        btnModifier.setOnAction(e -> handleModifier());
        btnSupprimer.setOnAction(e -> handleSupprimer());
        btnActualiser.setOnAction(e -> refreshBusCards());
    }

    private void refreshBusCards() {
        busCardsContainer.getChildren().clear();
        List<Bus> buses = serviceBus.getAll();
        
        for (Bus bus : buses) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bus_card.fxml"));
                Node busCard = loader.load();
                BusCardController controller = loader.getController();
                
                controller.setBus(bus);
                controller.setServiceBus(serviceBus);
                controller.setRefreshCallback(this::refreshBusCards);
                
                // Ajouter un écouteur de clic pour la sélection
                busCard.setOnMouseClicked(e -> setSelectedBus(bus));
                
                busCardsContainer.getChildren().add(busCard);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la carte du bus");
            }
        }
    }

    private void filterBusCards() {
        String searchText = searchField.getText().toLowerCase();
        String filterType = filterComboBox.getValue();

        busCardsContainer.getChildren().clear();
        List<Bus> buses = serviceBus.getAll();

        for (Bus bus : buses) {
            boolean matches = switch (filterType) {
                case "Ligne" -> bus.getNumeroLigne().toLowerCase().contains(searchText);
                case "Départ" -> bus.getPointDepart().toLowerCase().contains(searchText);
                case "Arrivée" -> bus.getPointArrivee().toLowerCase().contains(searchText);
                default -> // "Tous"
                    bus.getNumeroLigne().toLowerCase().contains(searchText) ||
                    bus.getPointDepart().toLowerCase().contains(searchText) ||
                    bus.getPointArrivee().toLowerCase().contains(searchText);
            };

            if (matches || searchText.isEmpty()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/bus_card.fxml"));
                    Node busCard = loader.load();
                    BusCardController controller = loader.getController();
                    
                    controller.setBus(bus);
                    controller.setServiceBus(serviceBus);
                    controller.setRefreshCallback(this::refreshBusCards);
                    
                    // Ajouter un écouteur de clic pour la sélection
                    busCard.setOnMouseClicked(e -> setSelectedBus(bus));
                    
                    busCardsContainer.getChildren().add(busCard);
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la carte du bus");
                }
            }
        }
    }

    private void handleAjouter() {
        try {
            validateFields();
            
            Bus newBus = new Bus();
            updateBusFromFields(newBus);

            serviceBus.add(newBus);
            clearFields();
            refreshBusCards();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Bus ajouté avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du bus: " + e.getMessage());
        }
    }

    private void handleModifier() {
        if (selectedBus == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un bus à modifier");
            return;
        }

        try {
            validateFields();
            updateBusFromFields(selectedBus);

            serviceBus.update(selectedBus);
            clearFields();
            refreshBusCards();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Bus modifié avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du bus: " + e.getMessage());
        }
    }

    private void handleSupprimer() {
        if (selectedBus == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un bus à supprimer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le bus");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce bus ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                serviceBus.delete(selectedBus);
                clearFields();
                refreshBusCards();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Bus supprimé avec succès");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du bus: " + e.getMessage());
            }
        }
    }

    private void validateFields() {
        if (tfLigne.getText().isEmpty() || tfDepart.getText().isEmpty() || 
            tfArrivee.getText().isEmpty() || tfHeure.getText().isEmpty() ||
            tfJours.getText().isEmpty() || tfCapacite.getText().isEmpty() ||
            tfPrix.getText().isEmpty()) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires");
        }
        
        try {
            LocalTime.parse(tfHeure.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException("Format d'heure invalide. Utilisez le format HH:mm");
        }
        
        try {
            Integer.parseInt(tfCapacite.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException("La capacité doit être un nombre entier");
        }
        
        try {
            Double.parseDouble(tfPrix.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException("Le prix doit être un nombre valide");
        }
    }

    private void updateBusFromFields(Bus bus) {
        bus.setNumeroLigne(tfLigne.getText());
        bus.setPointDepart(tfDepart.getText());
        bus.setPointArrivee(tfArrivee.getText());
        bus.setHeureDepart(LocalTime.parse(tfHeure.getText()));
        bus.setJoursCirculation(tfJours.getText());
        bus.setCapacite(Integer.parseInt(tfCapacite.getText()));
        bus.setPrix(Double.parseDouble(tfPrix.getText()));
    }

    public void setSelectedBus(Bus bus) {
        this.selectedBus = bus;
        if (bus != null) {
            tfLigne.setText(bus.getNumeroLigne());
            tfDepart.setText(bus.getPointDepart());
            tfArrivee.setText(bus.getPointArrivee());
            tfHeure.setText(bus.getHeureDepart().toString());
            tfJours.setText(bus.getJoursCirculation());
            tfCapacite.setText(String.valueOf(bus.getCapacite()));
            tfPrix.setText(String.valueOf(bus.getPrix()));
        }
    }

    private void clearFields() {
        tfLigne.clear();
        tfDepart.clear();
        tfArrivee.clear();
        tfHeure.clear();
        tfJours.clear();
        tfCapacite.clear();
        tfPrix.clear();
        selectedBus = null;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}