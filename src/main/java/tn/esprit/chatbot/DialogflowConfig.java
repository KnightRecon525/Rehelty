package tn.esprit.chatbot;

import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class DialogflowConfig {
    private static final String PROJECT_ID = "agencevoyagechatbot-rdba";
    private static final String CREDENTIALS_PATH =
            Paths.get("src", "main", "resources", "agencevoyagechatbot-rdba-cb9101072f1b.json").toString();
    private static SessionsClient sessionsClient;

    public static void initialize() throws IOException {
        // Vérifie que le fichier existe
        if (!Paths.get(CREDENTIALS_PATH).toFile().exists()) {
            throw new IOException("Fichier credentials introuvable à: " + CREDENTIALS_PATH);
        }

        // Charge les credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream(CREDENTIALS_PATH));

        // Configure les paramètres
        SessionsSettings settings = SessionsSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        sessionsClient = SessionsClient.create(settings);
    }

    public static SessionsClient getSessionsClient() {
        if (sessionsClient == null) {
            throw new IllegalStateException("SessionsClient non initialisé. Appelez initialize() d'abord.");
        }
        return sessionsClient;
    }

    public static String getProjectId() {
        return PROJECT_ID;
    }

    public static void shutdown() {
        if (sessionsClient != null) {
            sessionsClient.close();
            sessionsClient = null;
        }
    }
}