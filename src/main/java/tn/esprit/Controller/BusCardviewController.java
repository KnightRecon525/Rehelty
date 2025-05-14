package tn.esprit.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import tn.esprit.models.Bus;
import tn.esprit.services.ServiceBus;

public class BusCardviewController {
    @FXML private Label lblRoute;
    @FXML private Label lblTime;
    @FXML private Label lblSeats;
    @FXML private Label lblPrice;
    @FXML private Button btnReserve;

    private Bus bus;
    private Runnable refreshCallback;
    private final ServiceBus serviceBus = new ServiceBus();

    public void setData(Bus bus, Runnable refreshCallback) {
        this.bus = bus;
        this.refreshCallback = refreshCallback;
        updateUI();
    }

    private void updateUI() {
        lblRoute.setText(bus.getNumeroLigne() + ": " + bus.getPointDepart() + " → " + bus.getPointArrivee());
        lblTime.setText("Départ: " + bus.getHeureDepart() + " - Arrivée: " + bus.getHeureArrivee());
        lblSeats.setText("Places: " + bus.getPlacesRestantes() + "/" + bus.getCapacite());
        lblPrice.setText(bus.getPrix() + " DT");

        if (bus.getPlacesRestantes() == 0) {
            lblSeats.getStyleClass().add("seats-none");
            lblSeats.setText("COMPLET");
            btnReserve.setDisable(true);
        } else if (bus.getPlacesRestantes() < 10) {
            lblSeats.getStyleClass().add("seats-low");
        } else {
            lblSeats.getStyleClass().add("seats-available");
        }
    }

    @FXML
    private void handleReserve() {
        // Création du dialogue de réservation
        Spinner<Integer> spinner = new Spinner<>(1, bus.getPlacesRestantes(), 1);
        spinner.setEditable(true);

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Réservation");
        dialog.setHeaderText("Combien de places pour le bus " + bus.getNumeroLigne() + "?");
        dialog.getDialogPane().setContent(new HBox(10, new Label("Nombre:"), spinner));

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            int places = spinner.getValue();
            boolean success = serviceBus.reserverPlace(bus.getId());

            Alert resultAlert = new Alert(
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR
            );
            resultAlert.setContentText(success
                    ? places + " place(s) réservée(s) avec succès!"
                    : "Échec de réservation");
            resultAlert.show();

            if (success && refreshCallback != null) {
                refreshCallback.run();
            }
        }
    }
}