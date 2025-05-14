package tn.esprit.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import tn.esprit.chatbot.ChatbotService;
import tn.esprit.chatbot.DialogflowConfig;
import tn.esprit.utils.NavigationUtils;

public class ChatbotController {
    @FXML private TextArea chatArea;
    @FXML private TextField inputField;
    @FXML private Button btnRetour;

    private ChatbotService chatbotService;

    @FXML
    public void initialize() {
        try {
            DialogflowConfig.initialize();
            this.chatbotService = new ChatbotService();
            chatArea.appendText("Assistant: Bonjour! Posez-moi vos questions sur les voyages.\n\n");
        } catch (Exception e) {
            chatArea.appendText("Erreur d'initialisation: " + e.getMessage() + "\n");
            e.printStackTrace();
            // Mode démo si échec
            this.chatbotService = new ChatbotService() {
                public String detectIntent(String text, String sessionId) {
                    return "[Mode démo] Vous avez demandé: " + text;
                }
            };
        }
    }

    @FXML
    private void handleSendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        chatArea.appendText("Vous: " + message + "\n");
        inputField.clear();

        try {
            String response = chatbotService.detectIntent(message, "session-" + System.currentTimeMillis());
            chatArea.appendText("Assistant: " + response + "\n\n");
        } catch (Exception e) {
            chatArea.appendText("Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode pour naviguer vers l'écran de réservation de bus
     */
    @FXML
    private void navigateToReservationBus() {
        NavigationUtils.navigateTo(btnRetour, "/ReservationBus.fxml", "Réservation de Bus - Rehelty");
    }
}