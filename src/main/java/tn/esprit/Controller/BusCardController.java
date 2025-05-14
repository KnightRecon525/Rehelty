package tn.esprit.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import tn.esprit.models.Bus;
import tn.esprit.services.ServiceBus;
import java.util.Optional;

public class BusCardController {
    @FXML private Label numeroLigneLabel;
    @FXML private Label departLabel;
    @FXML private Label arriveeLabel;
    @FXML private Label horaireLabel;
    @FXML private Label placesLabel;
    @FXML private Button reserverButton;

    private Bus bus;
    private ServiceBus serviceBus;
    private Runnable refreshCallback;

    @FXML
    private void initialize() {
        // Le bouton est configuré dans le FXML avec onAction="#handleReservation"
    }

    public void setBus(Bus bus) {
        this.bus = bus;
        updateCardContent();
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void setServiceBus(ServiceBus serviceBus) {
        this.serviceBus = serviceBus;
    }

    private void updateCardContent() {
        if (bus != null) {
            numeroLigneLabel.setText(String.valueOf(bus.getNumeroLigne()));
            departLabel.setText(bus.getPointDepart());
            arriveeLabel.setText(bus.getPointArrivee());
            horaireLabel.setText(bus.getHeureDepart().toString());
            placesLabel.setText(String.valueOf(bus.getPlacesRestantes()));
            
            // Désactiver le bouton s'il n'y a plus de places
            if (reserverButton != null) {
                reserverButton.setDisable(bus.getPlacesRestantes() <= 0);
            }
        }
    }

    @FXML
    private void handleReservation() {
        if (bus == null || serviceBus == null) {
            showError("Erreur", "Impossible de procéder à la réservation");
            return;
        }

        // Créer le dialogue de réservation
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Réservation de bus");
        dialog.setHeaderText("Choisissez le nombre de places");

        // Créer les boutons
        ButtonType confirmerButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmerButtonType, ButtonType.CANCEL);

        // Créer le contenu
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Spinner<Integer> spinner = new Spinner<>(1, bus.getPlacesRestantes(), 1);
        spinner.setEditable(true);
        
        content.getChildren().addAll(
            new Label("Nombre de places à réserver:"),
            spinner
        );

        dialog.getDialogPane().setContent(content);

        // Convertir le résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmerButtonType) {
                return spinner.getValue();
            }
            return null;
        });

        // Afficher le dialogue et traiter le résultat
        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(places -> {
            try {
                // Mettre à jour les places restantes
                bus.setPlacesRestantes(bus.getPlacesRestantes() - places);
                serviceBus.update(bus);
                
                // Afficher une confirmation
                showInfo("Réservation confirmée", 
                        String.format("Vous avez réservé %d place(s) pour le bus de la ligne %s", 
                                    places, bus.getNumeroLigne()));
                
                // Rafraîchir l'affichage
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } catch (Exception e) {
                showError("Erreur", "Une erreur est survenue lors de la réservation: " + e.getMessage());
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 