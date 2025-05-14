package tn.esprit.services;

import tn.esprit.interfaces.IServiceBus;
import tn.esprit.models.Bus;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceBus implements IServiceBus {
    private final Connection cnx;
    private final List<Runnable> busUpdateListeners = new CopyOnWriteArrayList<>();

    public ServiceBus() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public void addBusUpdateListener(Runnable listener) {
        busUpdateListeners.add(listener);
    }

    public void removeBusUpdateListener(Runnable listener) {
        busUpdateListeners.remove(listener);
    }

    private void notifyBusUpdateListeners() {
        for (Runnable listener : busUpdateListeners) {
            listener.run();
        }
    }

    @Override
    public void add(Bus bus) {
        // Validation des données
        validateBusData(bus);

        String query = "INSERT INTO `bus` (`numero_ligne`, `point_depart`, `point_arrivee`, " +
                "`heure_depart`, `heure_arrivee`, `capacite`, `places_restantes`, " +
                "`jours_circulation`, `prix`, `date_debut`, `date_fin`) " +
                "VALUES (?, ?, ?, STR_TO_DATE(?, '%H:%i:%s'), STR_TO_DATE(?, '%H:%i:%s'), ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Préparation des données
            LocalTime heureDepart = parseTime(bus.getHeureDepart().toString());
            LocalTime heureArrivee = bus.getHeureArrivee();
            if (heureArrivee == null) {
                heureArrivee = calculateArrivalTime(heureDepart);
            }

            // Formatage des heures
            String departStr = formatTime(heureDepart);
            String arriveeStr = formatTime(heureArrivee);

            System.out.println("DEBUG - Heure départ (formatée): " + departStr);
            System.out.println("DEBUG - Heure arrivée (formatée): " + arriveeStr);

            // Configuration des paramètres
            pst.setString(1, bus.getNumeroLigne());
            pst.setString(2, bus.getPointDepart());
            pst.setString(3, bus.getPointArrivee());
            pst.setString(4, departStr);
            pst.setString(5, arriveeStr);
            pst.setInt(6, bus.getCapacite());
            pst.setInt(7, bus.getCapacite()); // Places restantes = capacité initiale
            pst.setString(8, bus.getJoursCirculation() != null ? bus.getJoursCirculation() : "1111111");
            pst.setDouble(9, bus.getPrix());
            pst.setDate(10, bus.getDateDebut() != null ? Date.valueOf(bus.getDateDebut()) : Date.valueOf(LocalDate.now()));
            pst.setDate(11, bus.getDateFin() != null ? Date.valueOf(bus.getDateFin()) : Date.valueOf(LocalDate.now().plusYears(1)));

            System.out.println("Tentative d'ajout du bus avec les paramètres suivants:");
            System.out.println("Numéro ligne: " + bus.getNumeroLigne());
            System.out.println("Départ: " + bus.getPointDepart() + " à " + departStr);
            System.out.println("Arrivée: " + bus.getPointArrivee() + " à " + arriveeStr);
            System.out.println("Capacité: " + bus.getCapacite());
            System.out.println("Prix: " + bus.getPrix());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        bus.setId(rs.getInt(1));
                        System.out.println("Bus ajouté avec succès! ID: " + bus.getId());
                        notifyBusUpdateListeners();
                    }
                }
            } else {
                throw new SQLException("L'ajout du bus a échoué, aucune ligne affectée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout du bus: " + e.getMessage());
            throw new RuntimeException("Échec de l'ajout du bus: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'ajout du bus: " + e.getMessage());
            throw new RuntimeException("Erreur inattendue lors de l'ajout du bus: " + e.getMessage(), e);
        }
    }

    private void validateBusData(Bus bus) {
        List<String> errors = new ArrayList<>();

        if (bus.getNumeroLigne() == null || bus.getNumeroLigne().trim().isEmpty()) {
            errors.add("Le numéro de ligne est requis");
        }
        if (bus.getPointDepart() == null || bus.getPointDepart().trim().isEmpty()) {
            errors.add("Le point de départ est requis");
        }
        if (bus.getPointArrivee() == null || bus.getPointArrivee().trim().isEmpty()) {
            errors.add("Le point d'arrivée est requis");
        }
        if (bus.getHeureDepart() == null) {
            errors.add("L'heure de départ est requise");
        }
        if (bus.getCapacite() <= 0) {
            errors.add("La capacité doit être supérieure à 0");
        }
        if (bus.getPrix() < 0) {
            errors.add("Le prix ne peut pas être négatif");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erreurs de validation: " + String.join(", ", errors));
        }
    }

    private String formatTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("L'heure ne peut pas être null");
        }
        // Format HH:mm:ss
        return String.format("%02d:%02d:%02d", time.getHour(), time.getMinute(), time.getSecond());
    }

    private LocalTime parseTime(String timeStr) {
        try {
            // Nettoyer la chaîne de caractères
            timeStr = timeStr.trim();
            
            // Si le format est HH:mm, ajouter :00 pour les secondes
            if (timeStr.length() == 5) {
                timeStr += ":00";
            }
            
            // Vérifier si le format est valide (HH:mm:ss)
            if (!timeStr.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
                throw new IllegalArgumentException("Format d'heure invalide");
            }
            
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Format d'heure invalide: " + timeStr);
        }
    }

    private LocalTime calculateArrivalTime(LocalTime departureTime) {
        if (departureTime == null) {
            throw new IllegalArgumentException("L'heure de départ ne peut pas être null");
        }
        // Ajouter 30 minutes pour l'heure d'arrivée
        return departureTime.plusMinutes(30);
    }

    @Override
    public List<Bus> getAll() {
        List<Bus> buses = new ArrayList<>();
        String query = "SELECT * FROM `bus` ORDER BY point_depart, point_arrivee, heure_depart";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                buses.add(mapResultSetToBus(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des bus: " + e.getMessage());
            throw new RuntimeException("Échec de la récupération des bus", e);
        }
        return buses;
    }

    @Override
    public void update(Bus bus) {
        String query = "UPDATE `bus` SET " +
                "`numero_ligne` = ?, `point_depart` = ?, `point_arrivee` = ?, " +
                "`heure_depart` = ?, `heure_arrivee` = ?, `capacite` = ?, " +
                "`places_restantes` = ?, `jours_circulation` = ?, `prix` = ?, " +
                "`date_debut` = ?, `date_fin` = ? " +
                "WHERE `id` = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, bus.getNumeroLigne());
            pst.setString(2, bus.getPointDepart());
            pst.setString(3, bus.getPointArrivee());
            pst.setTime(4, Time.valueOf(bus.getHeureDepart()));
            pst.setTime(5, Time.valueOf(bus.getHeureArrivee()));
            pst.setInt(6, bus.getCapacite());
            pst.setInt(7, bus.getPlacesRestantes());
            pst.setString(8, bus.getJoursCirculation());
            pst.setDouble(9, bus.getPrix());
            pst.setDate(10, bus.getDateDebut() != null ? Date.valueOf(bus.getDateDebut()) : null);
            pst.setDate(11, bus.getDateFin() != null ? Date.valueOf(bus.getDateFin()) : null);
            pst.setInt(12, bus.getId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Bus mis à jour avec succès! ID: " + bus.getId());
                notifyBusUpdateListeners();
            } else {
                System.out.println("Aucun bus trouvé avec l'ID: " + bus.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du bus: " + e.getMessage());
            throw new RuntimeException("Échec de la mise à jour du bus", e);
        }
    }

    @Override
    public void delete(Bus bus) {
        String query = "DELETE FROM `bus` WHERE `id` = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, bus.getId());
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Bus supprimé avec succès! ID: " + bus.getId());
                notifyBusUpdateListeners();
            } else {
                System.out.println("Aucun bus trouvé avec l'ID: " + bus.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du bus: " + e.getMessage());
            throw new RuntimeException("Échec de la suppression du bus", e);
        }
    }

    @Override
    public boolean reserverPlace(int idBus) {
        String query = "UPDATE `bus` SET `places_restantes` = `places_restantes` - 1 " +
                "WHERE `id` = ? AND `places_restantes` > 0";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, idBus);
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réservation: " + e.getMessage());
            throw new RuntimeException("Échec de la réservation", e);
        }
    }

    @Override
    public List<Bus> getByTrajet(String depart, String arrivee, LocalDate date) {
        List<Bus> buses = new ArrayList<>();
        String jourSemaine = convertToFrenchDay(date.getDayOfWeek().toString());

        String query = "SELECT * FROM `bus` WHERE " +
                "(`point_depart` LIKE ? OR ? IS NULL) AND " +
                "(`point_arrivee` LIKE ? OR ? IS NULL) AND " +
                "`places_restantes` > 0 AND " +
                "(`jours_circulation` LIKE ? OR `jours_circulation` = 'Tous' OR " +
                "(`date_debut` <= ? AND `date_fin` >= ?)) " +
                "ORDER BY heure_depart";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, "%" + depart + "%");
            pst.setString(2, depart.isEmpty() ? null : depart);
            pst.setString(3, "%" + arrivee + "%");
            pst.setString(4, arrivee.isEmpty() ? null : arrivee);
            pst.setString(5, "%" + jourSemaine + "%");
            pst.setDate(6, Date.valueOf(date));
            pst.setDate(7, Date.valueOf(date));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    buses.add(mapResultSetToBus(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de trajets: " + e.getMessage());
            throw new RuntimeException("Échec de la recherche de trajets", e);
        }
        return buses;
    }

    // Méthodes utilitaires privées
    private Bus mapResultSetToBus(ResultSet rs) throws SQLException {
        Bus bus = new Bus();
        bus.setId(rs.getInt("id"));
        bus.setNumeroLigne(rs.getString("numero_ligne"));
        bus.setPointDepart(rs.getString("point_depart"));
        bus.setPointArrivee(rs.getString("point_arrivee"));
        bus.setHeureDepart(rs.getTime("heure_depart").toLocalTime());
        bus.setHeureArrivee(rs.getTime("heure_arrivee").toLocalTime());
        bus.setCapacite(rs.getInt("capacite"));
        bus.setPlacesRestantes(rs.getInt("places_restantes"));
        bus.setJoursCirculation(rs.getString("jours_circulation"));
        bus.setPrix(rs.getDouble("prix"));

        Date dateDebut = rs.getDate("date_debut");
        Date dateFin = rs.getDate("date_fin");
        if (dateDebut != null) bus.setDateDebut(dateDebut.toLocalDate());
        if (dateFin != null) bus.setDateFin(dateFin.toLocalDate());

        return bus;
    }

    private String convertToFrenchDay(String englishDay) {
        return switch (englishDay.toUpperCase()) {
            case "MONDAY" -> "Lundi";
            case "TUESDAY" -> "Mardi";
            case "WEDNESDAY" -> "Mercredi";
            case "THURSDAY" -> "Jeudi";
            case "FRIDAY" -> "Vendredi";
            case "SATURDAY" -> "Samedi";
            case "SUNDAY" -> "Dimanche";
            default -> englishDay;
        };
    }

    // Méthode supplémentaire pour trouver un bus par son ID
    public Bus getById(int id) {
        String query = "SELECT * FROM `bus` WHERE `id` = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBus(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du bus par ID: " + e.getMessage());
            throw new RuntimeException("Échec de la recherche du bus", e);
        }
        return null;
    }
}