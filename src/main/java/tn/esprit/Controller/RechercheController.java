package tn.esprit.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import tn.esprit.models.Hotel;
import tn.esprit.services.ServiceHotel;
import tn.esprit.utils.NavigationUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.time.LocalDate;
import java.io.InputStream;

public class RechercheController {
    @FXML private TextField searchField;
    @FXML private DatePicker checkInDate;
    @FXML private DatePicker checkOutDate;
    @FXML private ComboBox<String> guestsCombo;
    @FXML private VBox hotelCardsContainer;
    @FXML private Slider priceSlider;
    @FXML private Label priceLabel;
    @FXML private CheckBox stars5;
    @FXML private CheckBox stars4;
    @FXML private CheckBox stars3;
    @FXML private CheckBox hasPool;
    @FXML private CheckBox hasWifi;
    @FXML private CheckBox hasRestaurant;
    @FXML private CheckBox hasSpa;
    @FXML private Button btnHome;

    private ServiceHotel serviceHotel;
    private List<Hotel> allHotels;

    @FXML
    public void initialize() {
        try {
            // Initialiser le service
            serviceHotel = new ServiceHotel();
            
            // Initialiser le combo des voyageurs
            guestsCombo.getItems().addAll("1 Personne", "2 Personnes", "3 Personnes", "4+ Personnes");
            guestsCombo.setValue("2 Personnes");
            
            // Initialiser les dates
            LocalDate today = LocalDate.now();
            checkInDate.setValue(today);
            checkOutDate.setValue(today.plusDays(1));
            
            // Configuration du slider de prix
            priceSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                priceLabel.setText(String.format("%.0f DT", newValue.doubleValue()));
                filterHotels();
            });
            
            // Configuration des checkboxes pour les étoiles
            stars5.setSelected(true);
            stars4.setSelected(true);
            stars3.setSelected(true);
            
            // S'assurer que tous les équipements sont désactivés par défaut
            hasPool.setSelected(false);
            hasWifi.setSelected(false);
            hasRestaurant.setSelected(false);
            hasSpa.setSelected(false);
            
            // Ajouter des écouteurs d'événements pour tous les filtres
            stars5.selectedProperty().addListener((obs, old, newVal) -> filterHotels());
            stars4.selectedProperty().addListener((obs, old, newVal) -> filterHotels());
            stars3.selectedProperty().addListener((obs, old, newVal) -> filterHotels());
            hasPool.selectedProperty().addListener((obs, old, newVal) -> filterHotels());
            hasWifi.selectedProperty().addListener((obs, old, newVal) -> filterHotels());
            hasRestaurant.selectedProperty().addListener((obs, old, newVal) -> filterHotels());
            hasSpa.selectedProperty().addListener((obs, old, newVal) -> filterHotels());
            
            // Écouteur de texte pour le champ de recherche
            searchField.textProperty().addListener((obs, old, newVal) -> filterHotels());
            
            // Charger et afficher les hôtels
            refreshHotels();
            
        } catch (Exception ex) {
            System.out.println("Erreur lors de l'initialisation: " + ex.getMessage());
            ex.printStackTrace();
            showError("Erreur d'initialisation", "Impossible d'initialiser l'interface: " + ex.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        refreshHotels();
    }

    /**
     * Fonction d'urgence pour recharger les données des hôtels
     * Elle crée au moins un hôtel de test si aucun n'est trouvé
     */
    @FXML
    private void handleEmergencyReload() {
        System.out.println("🚨 RECHARGEMENT D'URGENCE DES HÔTELS...");
        
        try {
            // Créer et afficher un hôtel de secours quoi qu'il arrive
            Hotel emergencyHotel = new Hotel();
            emergencyHotel.setId(1);
            emergencyHotel.setNom("Hôtel d'Urgence");
            emergencyHotel.setLocalisation("Application Rehelty");
            emergencyHotel.setDescription("Cet hôtel a été créé en urgence car aucun hôtel n'a pu être chargé depuis la base de données.");
            emergencyHotel.setNbrChambres(100);
            emergencyHotel.setPrixParNuite(300);
            emergencyHotel.setEtoiles(5);
            emergencyHotel.setEquipements("Piscine,Wi-Fi,Restaurant,Spa");
            emergencyHotel.setImageUrl("/image/default-hotel.jpg");
            emergencyHotel.setNote(5.0f);
            
            List<Hotel> emergencyList = new ArrayList<>();
            emergencyList.add(emergencyHotel);
            
            // Afficher directement cet hôtel
            displayHotels(emergencyList);
            
            // Tenter d'ajouter à la base de données
            try {
                serviceHotel.add(emergencyHotel);
                System.out.println("✅ Hôtel d'urgence ajouté à la base de données");
            } catch (Exception e) {
                System.out.println("❌ Impossible d'ajouter l'hôtel d'urgence: " + e.getMessage());
            }
            
            // Indiquer le succès
            showInfo("Rechargement d'urgence", "Un hôtel d'urgence a été affiché. Veuillez vérifier votre base de données.");
        } catch (Exception e) {
            System.out.println("❌ ÉCHEC DU RECHARGEMENT D'URGENCE: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur critique", "Impossible d'afficher même un hôtel d'urgence: " + e.getMessage());
        }
    }

    private void refreshHotels() {
        try {
            // Charger tous les hôtels
            System.out.println("Chargement de tous les hôtels...");
            allHotels = serviceHotel.getAll();
            System.out.println("Nombre d'hôtels chargés: " + allHotels.size());
            
            // Définir le slider au prix maximum
            float maxPrice = 0;
            for (Hotel hotel : allHotels) {
                if (hotel.getPrixParNuite() > maxPrice) {
                    maxPrice = hotel.getPrixParNuite();
                }
            }
            // Arrondir à la centaine supérieure
            maxPrice = (float) (Math.ceil(maxPrice / 100) * 100);
            System.out.println("Prix maximum trouvé: " + maxPrice);
            
            // Définir les valeurs du slider
            priceSlider.setMax(maxPrice);
            priceSlider.setValue(maxPrice);
            priceLabel.setText(String.format("%.0f DT", maxPrice));
            
            if (allHotels.isEmpty()) {
                System.out.println("ERREUR: Aucun hôtel n'a été chargé!");
                showError("Erreur de chargement", "Aucun hôtel n'a pu être chargé depuis la base de données.");
                return;
            }
            
            // AFFICHER DIRECTEMENT TOUS LES HÔTELS SANS FILTRER
            System.out.println("Affichage direct de tous les hôtels sans filtrage...");
            displayHotels(allHotels);
        } catch (Exception e) {
            System.out.println("Exception dans refreshHotels(): " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement des hôtels", e.getMessage());
        }
    }

    private void filterHotels() {
        if (allHotels == null) return;

        System.out.println("Début du filtrage avec " + allHotels.size() + " hôtels...");
        System.out.println("Prix maximum: " + priceSlider.getValue());
        System.out.println("Filtres étoiles: 5★=" + stars5.isSelected() + ", 4★=" + stars4.isSelected() + ", 3★=" + stars3.isSelected());
        System.out.println("Filtres équipements: Piscine=" + hasPool.isSelected() + ", WiFi=" + hasWifi.isSelected() + 
                           ", Restaurant=" + hasRestaurant.isSelected() + ", Spa=" + hasSpa.isSelected());

        // Apply filters
        List<Hotel> filteredHotels = allHotels.stream()
            .filter(hotel -> {
                // Debug log
                System.out.println("Vérification hôtel: " + hotel.getNom() + ", Prix: " + hotel.getPrixParNuite() + ", Étoiles: " + hotel.getEtoiles());
                
                // Filter by search text
                if (!searchField.getText().isEmpty()) {
                    String search = searchField.getText().toLowerCase();
                    if (!hotel.getNom().toLowerCase().contains(search) &&
                        !hotel.getLocalisation().toLowerCase().contains(search)) {
                        System.out.println("  Rejeté par recherche texte");
                        return false;
                    }
                }

                // Filter by price
                if (hotel.getPrixParNuite() > priceSlider.getValue()) {
                    System.out.println("  Rejeté par prix: " + hotel.getPrixParNuite() + " > " + priceSlider.getValue());
                    return false;
                }

                // Filter by stars
                boolean starMatch = false;
                if (stars5.isSelected() && hotel.getEtoiles() == 5) starMatch = true;
                if (stars4.isSelected() && hotel.getEtoiles() == 4) starMatch = true;
                if (stars3.isSelected() && hotel.getEtoiles() == 3) starMatch = true;
                
                // If no star filter is selected, allow all star ratings
                if (!stars5.isSelected() && !stars4.isSelected() && !stars3.isSelected()) starMatch = true;
                
                if (!starMatch) {
                    System.out.println("  Rejeté par étoiles: " + hotel.getEtoiles());
                    return false;
                }
                
                // Filter by amenities
                String equipements = hotel.getEquipements() != null ? hotel.getEquipements().toLowerCase() : "";
                System.out.println("  Équipements: " + equipements);
                
                // Vérification des équipements
                if (hasPool.isSelected()) {
                    boolean hasPiscine = equipements.contains("piscine");
                    System.out.println("  - Piscine: " + (hasPiscine ? "OUI" : "NON"));
                    if (!hasPiscine) {
                        System.out.println("  Rejeté par manque de piscine");
                        return false;
                    }
                }
                
                if (hasWifi.isSelected()) {
                    boolean hasWiFi = equipements.contains("wi-fi") || equipements.contains("wifi");
                    System.out.println("  - WiFi: " + (hasWiFi ? "OUI" : "NON"));
                    if (!hasWiFi) {
                        System.out.println("  Rejeté par manque de WiFi");
                        return false;
                    }
                }
                
                if (hasRestaurant.isSelected()) {
                    boolean hasResto = equipements.contains("restaurant");
                    System.out.println("  - Restaurant: " + (hasResto ? "OUI" : "NON"));
                    if (!hasResto) {
                        System.out.println("  Rejeté par manque de restaurant");
                        return false;
                    }
                }
                
                if (hasSpa.isSelected()) {
                    boolean hasSpaEquip = equipements.contains("spa");
                    System.out.println("  - Spa: " + (hasSpaEquip ? "OUI" : "NON"));
                    if (!hasSpaEquip) {
                        System.out.println("  Rejeté par manque de spa");
                        return false;
                    }
                }

                System.out.println("  Accepté: " + hotel.getNom());
                return true;
            })
            // Make sure we only have unique hotels (no duplicates)
            .distinct()
            .collect(Collectors.toList());

        System.out.println("Filtrage terminé, " + filteredHotels.size() + " hôtels retenus.");
        displayHotels(filteredHotels);
    }

    private void displayHotels(List<Hotel> hotels) {
        // Vider le conteneur d'hôtels
        hotelCardsContainer.getChildren().clear();
        
        System.out.println("Tentative d'affichage de " + hotels.size() + " hôtels...");
        
        if (hotels.isEmpty()) {
            System.out.println("ATTENTION: Liste d'hôtels vide!");
            Label noResults = new Label("Aucun hôtel ne correspond à vos critères");
            noResults.setStyle("-fx-padding: 20px; -fx-font-size: 14px;");
            hotelCardsContainer.getChildren().add(noResults);
            return;
        }
        
        int displayedCount = 0;
        
        // Créer une carte pour chaque hôtel
        for (Hotel hotel : hotels) {
            try {
                System.out.println("Traitement de l'hôtel: " + hotel.getNom() + ", Prix: " + hotel.getPrixParNuite() + ", Étoiles: " + hotel.getEtoiles());
                
                // Créer un conteneur pour la carte d'hôtel
                VBox card = new VBox(10);
                card.getStyleClass().add("hotel-card");
                card.setPadding(new Insets(15));
                card.setMaxWidth(800);
                
                // Layout horizontal pour l'image et les infos
                HBox layout = new HBox(15);
                layout.setAlignment(Pos.CENTER_LEFT);

                // Image de l'hôtel
                ImageView imageView = new ImageView();
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                
                try {
                    // Chemins d'images par défaut
                    String defaultImagePath = "/image/default-hotel.jpg";
                    String imagePath = hotel.getImageUrl();
                    
                    // Afficher les informations sur le chemin d'image
                    System.out.println("🖼️ Image pour " + hotel.getNom() + ":");
                    System.out.println("   - Chemin d'origine: " + imagePath);
                    
                    if (imagePath == null || imagePath.isEmpty()) {
                        System.out.println("   - Chemin vide, utilisation de l'image par défaut: " + defaultImagePath);
                        imagePath = defaultImagePath;
                    }
                    
                    // Tenter de charger l'image
                    Image image = null;
                    try {
                        if (imagePath.startsWith("http")) {
                            // Image externe
                            System.out.println("   - Chargement d'une image externe: " + imagePath);
                            image = new Image(imagePath, true);
                        } else {
                            // Image locale
                            if (!imagePath.startsWith("/")) {
                                imagePath = "/" + imagePath;
                                System.out.println("   - Ajout du / initial: " + imagePath);
                            }
                            System.out.println("   - Tentative de chargement de l'image locale: " + imagePath);
                            InputStream is = getClass().getResourceAsStream(imagePath);
                            
                            if (is == null) {
                                System.out.println("   - Flux d'entrée NULL pour: " + imagePath);
                                throw new Exception("Image introuvable dans les ressources");
                            } else {
                                image = new Image(is);
                            }
                        }
                        
                        if (image == null || image.isError()) {
                            throw new Exception("Image non chargée ou en erreur");
                        }
                        
                        System.out.println("   - Image chargée avec succès: " + imagePath);
                    } catch (Exception e) {
                        System.out.println("   - ERREUR de chargement: " + e.getMessage() + ", utilisation de l'image par défaut");
                        InputStream defaultIs = getClass().getResourceAsStream(defaultImagePath);
                        if (defaultIs != null) {
                            image = new Image(defaultIs);
                            System.out.println("   - Image par défaut chargée: " + defaultImagePath);
                        } else {
                            System.out.println("   - ERREUR critique: même l'image par défaut est introuvable!");
                        }
                    }
                    
                    imageView.setImage(image);
                } catch (Exception e) {
                    System.out.println("Erreur avec l'image: " + e.getMessage());
                    // En cas d'erreur, on continue sans image
                }
                
                // Conteneur pour les informations
                VBox info = new VBox(5);
                info.setMaxWidth(500);
                
                // Nom de l'hôtel
                Label nameLabel = new Label(hotel.getNom());
                nameLabel.getStyleClass().add("hotel-name");
                
                // Localisation
                Label locationLabel = new Label(hotel.getLocalisation());
                locationLabel.getStyleClass().add("hotel-location");
                
                // Description
                Label descriptionLabel = new Label(hotel.getDescription());
                descriptionLabel.getStyleClass().add("hotel-description");
                descriptionLabel.setWrapText(true);
                
                // Prix
                Label priceLabel = new Label(String.format("%.0f DT par nuit", hotel.getPrixParNuite()));
                priceLabel.getStyleClass().add("hotel-price");
                
                // Note/étoiles
                HBox notationBox = new HBox(10);
                notationBox.setAlignment(Pos.CENTER_LEFT);
                
                // Étoiles
                HBox starsBox = new HBox(2);
                for (int i = 0; i < hotel.getEtoiles(); i++) {
                    Label star = new Label("★");
                    star.setStyle("-fx-text-fill: gold; -fx-font-size: 16px;");
                    starsBox.getChildren().add(star);
                }
                
                // Affichage de la note sur 5
                Label ratingLabel = new Label(String.format("%.1f/5", hotel.getNote()));
                ratingLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #003366; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 5px;");
                
                notationBox.getChildren().addAll(starsBox, ratingLabel);
                
                // Équipements - Création de boutons visuels pour chaque équipement
                FlowPane equipementsPane = new FlowPane();
                equipementsPane.setHgap(5);
                equipementsPane.setVgap(5);
                equipementsPane.setPrefWrapLength(450);
                
                // Récupération des équipements
                String equipementsStr = hotel.getEquipements();
                if (equipementsStr != null && !equipementsStr.isEmpty()) {
                    String[] equipements = equipementsStr.split(",");
                    for (String equipement : equipements) {
                        Label equip = new Label(equipement.trim());
                        equip.setStyle("-fx-background-color: #e6f7ff; -fx-padding: 5 10; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #99d6ff; -fx-border-width: 1;");
                        equipementsPane.getChildren().add(equip);
                    }
                }
                
                // Bouton de réservation
                Button reserveButton = new Button("Réserver");
                reserveButton.getStyleClass().add("reserver-button");
                
                // Ajouter tous les éléments
                info.getChildren().addAll(nameLabel, locationLabel, descriptionLabel, priceLabel, notationBox, equipementsPane, reserveButton);
                layout.getChildren().addAll(imageView, info);
                card.getChildren().add(layout);
                
                // Ajouter la carte au conteneur
                hotelCardsContainer.getChildren().add(card);
                displayedCount++;
                
            } catch (Exception e) {
                System.out.println("Erreur lors de l'affichage de l'hôtel " + hotel.getNom() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Total des hôtels affichés: " + displayedCount + "/" + hotels.size());
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

    /**
     * Méthode pour naviguer vers l'écran d'accueil
     */
    @FXML
    private void navigateToHome() {
        NavigationUtils.navigateTo(btnHome, "/Home.fxml", "Accueil - Rehelty");
    }
}