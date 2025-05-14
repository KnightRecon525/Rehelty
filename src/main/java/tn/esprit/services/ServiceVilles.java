package tn.esprit.services;

import tn.esprit.utils.MyDataBase;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServiceVilles {
    private Connection cnx;

    public ServiceVilles() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public ObservableList<String> getAllVilles() {
        Set<String> villes = new HashSet<>();
        String query = "SELECT DISTINCT point_depart FROM bus UNION SELECT DISTINCT point_arrivee FROM bus ORDER BY point_depart";
        
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String ville = rs.getString(1);
                if (ville != null && !ville.trim().isEmpty()) {
                    villes.add(ville.trim());
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des villes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return FXCollections.observableArrayList(villes);
    }

    public void ajouterVille(String ville) {
        // Cette méthode est utilisée uniquement pour la validation
        if (ville == null || ville.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la ville ne peut pas être vide");
        }
        
        // La ville sera automatiquement ajoutée via les points de départ/arrivée des bus
    }

    public boolean villeExiste(String ville) {
        String query = "SELECT COUNT(*) FROM bus WHERE point_depart = ? OR point_arrivee = ?";
        
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, ville.trim());
            pst.setString(2, ville.trim());
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la ville: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public void rafraichirVilles() {
        // Cette méthode peut être appelée après l'ajout d'un bus pour mettre à jour les listes déroulantes
        getAllVilles();
    }
} 