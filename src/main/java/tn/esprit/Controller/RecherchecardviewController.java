package tn.esprit.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import tn.esprit.models.Bus;
import tn.esprit.services.ServiceBus;
import tn.esprit.services.ServiceVilles;
import tn.esprit.utils.NavigationUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class RecherchecardviewController {
    @FXML private FlowPane cardContainer;
    @FXML private ComboBox<String> cbDepart;
    @FXML private ComboBox<String> cbArrivee;
    @FXML private DatePicker dpDate;
    @FXML private Label lblMessage;
    @FXML private Button btnHome;
    @FXML private Button btnChatbot;

    private final ServiceBus serviceBus = new ServiceBus();
    private final ServiceVilles serviceVilles = new ServiceVilles();

    @FXML
    private void initialize() {
        // Initialiser les listes déroulantes avec les villes disponibles
        updateComboBoxes();
        
        // Ajouter un listener pour mettre à jour les villes quand un bus est ajouté
        serviceBus.addBusUpdateListener(this::updateComboBoxes);

        // Configurer le DatePicker pour n'accepter que les dates futures
        dpDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
                if (date.compareTo(today) < 0) {
                    setStyle("-fx-background-color: #ffc0cb;"); // Légère teinte rouge pour les dates passées
                }
            }
        });
    }

    /**
     * Méthode pour naviguer vers l'écran d'accueil
     */
    @FXML
    private void navigateToHome() {
        NavigationUtils.navigateTo(btnHome, "/Home.fxml", "Accueil - Rehelty");
    }
    
    /**
     * Méthode pour naviguer vers l'écran du chatbot
     */
    @FXML
    private void navigateToChatbot() {
        NavigationUtils.navigateTo(btnChatbot, "/chatbot.fxml", "Assistant Virtuel - Rehelty");
    }

    private void updateComboBoxes() {
        var villes = serviceVilles.getAllVilles();
        cbDepart.setItems(villes);
        cbArrivee.setItems(villes);
    }

    @FXML
    private void rechercherBus() {
        cardContainer.getChildren().clear();
        
        // Vérifier que tous les champs sont remplis
        if (cbDepart.getValue() == null || cbArrivee.getValue() == null || dpDate.getValue() == null) {
            showError("Champs manquants", "Veuillez remplir tous les champs de recherche");
            return;
        }

        // Vérifier si la date sélectionnée n'est pas dans le passé
        if (dpDate.getValue().isBefore(LocalDate.now())) {
            showError("Date invalide", "Veuillez sélectionner une date future pour votre voyage");
            return;
        }

        List<Bus> buses = serviceBus.getByTrajet(
                cbDepart.getValue(),
                cbArrivee.getValue(),
                dpDate.getValue()
        );

        if (buses.isEmpty()) {
            lblMessage.setText("Aucun bus trouvé pour ces critères");
            return;
        }

        lblMessage.setText(String.format("%d bus trouvé(s)", buses.size()));

        for (Bus bus : buses) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bus_card.fxml"));
                Node busCard = loader.load();
                
                BusCardController controller = loader.getController();
                controller.setBus(bus);
                controller.setServiceBus(serviceBus);
                controller.setRefreshCallback(this::rechercherBus);
                
                cardContainer.getChildren().add(busCard);
            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur", "Impossible de charger l'affichage du bus: " + e.getMessage());
            }
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}