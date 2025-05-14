package tn.esprit.chatbot;

import com.google.cloud.dialogflow.v2.*;
import com.google.protobuf.Value;
import com.google.protobuf.Struct;
import tn.esprit.models.Bus;
import tn.esprit.services.ServiceBus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;

public class ChatbotService {
    private final ServiceBus serviceBus;
    private final Random random;

    public ChatbotService() {
        this.serviceBus = new ServiceBus();
        this.random = new Random();
    }

    public String detectIntent(String text, String sessionId) {
        try {
            SessionsClient sessionsClient = DialogflowConfig.getSessionsClient();
            SessionName session = SessionName.of(DialogflowConfig.getProjectId(), sessionId);

            TextInput.Builder textInput = TextInput.newBuilder()
                    .setText(text)
                    .setLanguageCode("fr-FR");

            QueryInput queryInput = QueryInput.newBuilder()
                    .setText(textInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            QueryResult queryResult = response.getQueryResult();

            logDialogflowResponse(queryResult);

            if (queryResult.getIntent() != null) {
                switch (queryResult.getIntent().getDisplayName()) {
                    case "Default Welcome Intent":
                        return getWelcomeResponse();

                    case "Greetings":
                        return getRandomGreeting();

                    case "RechercheBus":
                        return processBusRequest(queryResult);

                    case "Default Fallback Intent":
                        return handleSmartFallback(text, queryResult.getParameters());

                    default:
                        return queryResult.getFulfillmentText().isEmpty()
                                ? handleSmartFallback(text, null)
                                : queryResult.getFulfillmentText();
                }
            }

            return handleSmartFallback(text, null);

        } catch (Exception e) {
            System.err.println("Erreur dans detectIntent: " + e.getMessage());
            return "Désolé, service temporairement indisponible. Veuillez réessayer plus tard.";
        }
    }

    private String processBusRequest(QueryResult queryResult) {
        try {
            System.out.println("\n=== DEBUG: Processing Bus Request ===");
            String queryText = queryResult.getQueryText();
            
            // Correction de l'encodage et normalisation du texte
            queryText = normalizeText(queryText);
            System.out.println("Query Text (normalized): " + queryText);
            System.out.println("Intent: " + queryResult.getIntent().getDisplayName());
            System.out.println("Raw parameters: " + queryResult.getParameters());
            
            Map<String, Value> params = queryResult.getParameters() != null
                    ? queryResult.getParameters().getFieldsMap()
                    : Collections.emptyMap();

            String depart = extractCity(params, "depart");
            String arrivee = extractCity(params, "arrivee");
            String dateStr = extractDate(params);

            System.out.println("Extracted depart: " + depart);
            System.out.println("Extracted arrivee: " + arrivee);
            System.out.println("Extracted date: " + dateStr);

            // Si les paramètres sont vides, essayer l'extraction manuelle
            if (depart.isEmpty() || arrivee.isEmpty()) {
                extractCitiesFromText(queryText, depart, arrivee);
            }

            // Si toujours vide, essayer l'extraction directe
            if (depart.isEmpty() || arrivee.isEmpty()) {
                String[] cities = extractCitiesDirectly(queryText);
                if (depart.isEmpty() && cities[0] != null) {
                    depart = cities[0];
                }
                if (arrivee.isEmpty() && cities[1] != null) {
                    arrivee = cities[1];
                }
            }

            // Capitalisation des noms de villes
            depart = capitalizeCity(depart);
            arrivee = capitalizeCity(arrivee);

            System.out.println("Final depart: " + depart);
            System.out.println("Final arrivee: " + arrivee);

            if (depart.isEmpty() || arrivee.isEmpty()) {
                return getBusHelpMessage(depart, arrivee);
            }

            LocalDate date = parseDate(dateStr);
            List<Bus> buses = serviceBus.getByTrajet(depart, arrivee, date);

            return formatBusResults(buses, depart, arrivee, date);

        } catch (Exception e) {
            System.err.println("Erreur traitement bus: " + e.getMessage());
            e.printStackTrace();
            return "Désolé, impossible d'accéder aux horaires. Veuillez reformuler.";
        }
    }

    private String normalizeText(String text) {
        try {
            // Remplacer les caractères spéciaux problématiques
            text = text.replace('?', 'à')

                      .replace('a', 'à')
                      .replace("vers", "à");

            // Normaliser les espaces
            text = text.replaceAll("\\s+", " ").trim();

            return text;
        } catch (Exception e) {
            System.err.println("Erreur de normalisation: " + e.getMessage());
            return text;
        }
    }

    private void extractCitiesFromText(String text, String depart, String arrivee) {
        text = text.toLowerCase();
        System.out.println("Extracting from normalized text: " + text);

        // Pattern pour trouver "de VILLE à VILLE"
        Pattern pattern = Pattern.compile("(?:de|depuis)\\s+([\\w\\s-]+?)\\s+(?:à|vers|pour)\\s+([\\w\\s-]+?)(?:\\s+|$)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            if (depart.isEmpty()) {
                depart = cleanCityName(matcher.group(1));
                System.out.println("Found departure city: " + depart);
            }
            if (arrivee.isEmpty()) {
                arrivee = cleanCityName(matcher.group(2));
                System.out.println("Found arrival city: " + arrivee);
            }
        }
    }

    private String[] extractCitiesDirectly(String text) {
        String[] result = new String[2];
        text = text.toLowerCase();
        String[] words = text.split("\\s+");
        
        // Liste des mots à ignorer
        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "bus", "de", "à", "vers", "pour", "le", "la", "les", "en",
            "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"
        ));

        // Trouver les mots qui pourraient être des villes
        List<String> potentialCities = new ArrayList<>();
        for (String word : words) {
            if (!stopWords.contains(word) && word.matches("[a-zàáâãäçèéêëìíîïñòóôõöùúûü\\s-]+")) {
                potentialCities.add(word);
            }
        }

        // Si on trouve exactement deux villes potentielles
        if (potentialCities.size() >= 2) {
            result[0] = cleanCityName(potentialCities.get(0));
            result[1] = cleanCityName(potentialCities.get(1));
        }

        return result;
    }

    private String cleanCityName(String city) {
        if (city == null) return "";
        
        // Nettoyer la ville
        city = city.trim()
                  .replaceAll("[.,!?]", "")
                  .replaceAll("\\s+(le|la|les|en|par|pour|lundi|mardi|mercredi|jeudi|vendredi|samedi|dimanche)\\s*$", "")
                  .trim();

        // Vérifier si la ville est vide après nettoyage
        return city.isEmpty() ? "" : city;
    }

    private String capitalizeCity(String city) {
        if (city == null || city.isEmpty()) return "";
        // Gérer les noms de villes composés
        String[] parts = city.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (result.length() > 0) result.append(" ");
            result.append(Character.toUpperCase(part.charAt(0)))
                  .append(part.substring(1).toLowerCase());
        }
        return result.toString();
    }

    private String handleSmartFallback(String text, Struct parameters) {
        System.out.println("\n=== DEBUG: Smart Fallback ===");
        System.out.println("Text: " + text);
        System.out.println("Parameters: " + parameters);

        Map<String, Value> params = parameters != null
                ? parameters.getFieldsMap()
                : Collections.emptyMap();

        if (text.toLowerCase().contains("bus")) {
            // Try to extract cities from text
            String depart = "";
            String arrivee = "";
            String textLower = text.toLowerCase();

            if (textLower.contains("de") && textLower.contains("à")) {
                String[] parts = textLower.split("de|à");
                if (parts.length >= 3) {
                    depart = parts[1].trim();
                    arrivee = parts[2].split(" ")[0].trim();
                    System.out.println("Fallback extracted - Depart: " + depart + ", Arrivee: " + arrivee);
                }
            }

            // If we found cities in the text, try to process them
            if (!depart.isEmpty() && !arrivee.isEmpty()) {
                depart = capitalizeCity(depart);
                arrivee = capitalizeCity(arrivee);
                try {
                    List<Bus> buses = serviceBus.getByTrajet(depart, arrivee, LocalDate.now().plusDays(1));
                    return formatBusResults(buses, depart, arrivee, LocalDate.now().plusDays(1));
                } catch (Exception e) {
                    System.err.println("Erreur recherche bus dans fallback: " + e.getMessage());
                }
            }

            return getBusHelpMessage(depart, arrivee);
        }

        return getGeneralHelp();
    }

    // Méthodes helper
    private String extractCity(Map<String, Value> params, String paramName) {
        if (!params.containsKey(paramName)) {
            System.out.println("DEBUG: Parameter " + paramName + " not found in params");
            return "";
        }

        Value value = params.get(paramName);
        System.out.println("DEBUG: Raw value for " + paramName + ": " + value);

        if (value == null || value.equals(Value.getDefaultInstance())) {
            System.out.println("DEBUG: Null or default value for " + paramName);
            return "";
        }

        if (value.hasStringValue()) {
            String cityName = value.getStringValue().trim();
            System.out.println("DEBUG: Found direct string value: " + cityName);
            return cityName;
        }

        if (value.hasStructValue()) {
            Struct geoStruct = value.getStructValue();
            System.out.println("DEBUG: Struct fields: " + geoStruct.getFieldsMap().keySet());
            
            String cityName = null;
            if (geoStruct.containsFields("city")) {
                cityName = geoStruct.getFieldsMap().get("city").getStringValue();
            } else if (geoStruct.containsFields("name")) {
                cityName = geoStruct.getFieldsMap().get("name").getStringValue();
            } else {
                for (Map.Entry<String, Value> entry : geoStruct.getFieldsMap().entrySet()) {
                    if (entry.getValue().hasStringValue() && !entry.getValue().getStringValue().isEmpty()) {
                        cityName = entry.getValue().getStringValue();
                        break;
                    }
                }
            }

            if (cityName != null && !cityName.isEmpty()) {
                System.out.println("DEBUG: Found city in struct: " + cityName);
                return cityName;
            }
        }

        System.out.println("DEBUG: Could not extract city name from: " + value);
        return "";
    }

    private String extractDate(Map<String, Value> params) {
        return params.containsKey("date")
                ? params.get("date").getStringValue()
                : "";
    }

    private LocalDate parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.isEmpty()) {
                return LocalDate.now().plusDays(1);
            }
            return LocalDate.parse(dateStr.split("T")[0]);
        } catch (Exception e) {
            System.err.println("Erreur parsing date: " + dateStr);
            return LocalDate.now().plusDays(1);
        }
    }

    private String formatBusResults(List<Bus> buses, String depart, String arrivee, LocalDate date) {
        if (buses.isEmpty()) {
            return String.format("Aucun bus disponible de %s à %s pour %s.",
                    depart, arrivee, formatDisplayDate(date));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("⏱ Horaires de %s à %s (%s):\n\n",
                depart, arrivee, formatDisplayDate(date)));

        buses.forEach(bus -> {
            sb.append(String.format("🚌 Ligne %s\n", bus.getNumeroLigne()))
                    .append(String.format("   ⌚ %s - %s\n", bus.getHeureDepart(), bus.getHeureArrivee()))
                    .append(String.format("   💺 %d/%d places - 💰 %.2f DT\n\n",
                            bus.getPlacesRestantes(), bus.getCapacite(), bus.getPrix()));
        });

        return sb.toString();
    }

    private String formatDisplayDate(LocalDate date) {
        if (date.equals(LocalDate.now().plusDays(1))) return "demain";
        if (date.equals(LocalDate.now())) return "aujourd'hui";
        return date.format(DateTimeFormatter.ofPattern("EEEE dd/MM"));
    }

    private String getBusHelpMessage(String depart, String arrivee) {
        StringBuilder help = new StringBuilder();
        if (depart.isEmpty()) help.append("❌ Ville de départ manquante\n");
        if (arrivee.isEmpty()) help.append("❌ Ville d'arrivée manquante\n");

        help.append("\nℹ️ Format requis:\n")
                .append("\"bus de [départ] à [arrivée]\"\n")
                .append("Exemples:\n")
                .append("- \"Bus de Tunis à Sousse\"\n")
                .append("- \"Horaires Tunis Sfax demain\"");

        return help.toString();
    }

    private String getGeneralHelp() {
        return "Je peux vous aider avec:\n"
                + "• Recherche d'horaires de bus\n"
                + "• Information sur les trajets\n\n"
                + "Exemple: \"Bus de Tunis à Sousse lundi\"";
    }

    private void logDialogflowResponse(QueryResult queryResult) {
        System.out.println("\n=== DEBUG ===");
        System.out.println("Intent: " + queryResult.getIntent().getDisplayName());
        System.out.println("Confidence: " + queryResult.getIntentDetectionConfidence());
        System.out.println("Parameters: " + queryResult.getParameters());
        System.out.println("=============\n");
    }

    private String getWelcomeResponse() {
        String[] messages = {
                "Bonjour ! Je peux vous aider à trouver des bus. Posez-moi votre question !",
                "Salut ! Demandez-moi les horaires des bus entre deux villes.",
                "Bienvenue ! Je suis votre assistant pour les trajets en bus."
        };
        return messages[random.nextInt(messages.length)];
    }

    private String getRandomGreeting() {
        String[] greetings = {"Bonjour !", "Salut !", "Hello !"};
        return greetings[random.nextInt(greetings.length)];
    }
}