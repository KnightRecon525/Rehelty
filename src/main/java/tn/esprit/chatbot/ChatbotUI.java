package tn.esprit.chatbot;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

public class ChatbotUI extends VBox {
    private TextArea chatArea;
    private TextField inputField;
    private ChatbotService chatbotService;

    public ChatbotUI() {
        // Initialisation du service
        try {
            DialogflowConfig.initialize();
            chatbotService = new ChatbotService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuration de l'interface
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(300);

        inputField = new TextField();
        inputField.setPromptText("Posez votre question...");

        Button sendButton = new Button("Envoyer");
        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        HBox inputBox = new HBox(inputField, sendButton);
        inputBox.setSpacing(10);

        this.getChildren().addAll(chatArea, inputBox);
        this.setSpacing(10);
        this.setPadding(new Insets(10));
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        chatArea.appendText("Vous: " + message + "\n");
        inputField.clear();

        try {
            String response = chatbotService.detectIntent(message, "user-session-123");
            chatArea.appendText("Assistant: " + response + "\n\n");
        } catch (Exception e) {
            chatArea.appendText("Assistant: Désolé, je rencontre un problème technique.\n\n");
            e.printStackTrace();
        }
    }
}