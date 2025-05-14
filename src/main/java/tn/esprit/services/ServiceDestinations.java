package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Destinations;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDestinations implements IService<Destinations> {
    private Connection cnx;

    public ServiceDestinations() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    public boolean isDestinationNameExists(String nom) {
        String qry = "SELECT COUNT(*) FROM `destinations` WHERE LOWER(`nom`) = LOWER(?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, nom.trim());
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du nom: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void add(Destinations d) {
        // Vérifier si le nom est valide
        if (d.getNom() == null || d.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la destination ne peut pas être vide");
        }

        // Vérifier si le nom contient uniquement des chiffres
        if (d.getNom().matches("\\d+")) {
            throw new IllegalArgumentException("Le nom de la destination ne peut pas contenir uniquement des chiffres");
        }

        // Vérifier si le nom existe déjà (insensible à la casse)
        if (isDestinationNameExists(d.getNom().trim())) {
            throw new IllegalArgumentException("Une destination avec ce nom existe déjà (vérification insensible à la casse)");
        }

        String qry = "INSERT INTO `destinations` (`nom`, `url`) VALUES (?, ?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, d.getNom().trim());
            pstm.setString(2, d.getUrl());
            pstm.executeUpdate();
            System.out.println("Destination ajoutée: " + d.getNom());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la destination: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Destinations> getAll() {
        List<Destinations> list = new ArrayList<>();
        String qry = "SELECT * FROM `destinations` ORDER BY `nom`";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                Destinations d = new Destinations();
                d.setId(rs.getInt("id"));
                d.setNom(rs.getString("nom"));
                d.setUrl(rs.getString("url"));
                list.add(d);
                System.out.println("Destination chargée: " + d.getNom());
            }
            System.out.println("Total destinations chargées: " + list.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des destinations: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return list;
    }

    public void update(Destinations d) {
        // Vérifier si le nom est valide
        if (d.getNom() == null || d.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la destination ne peut pas être vide");
        }

        // Vérifier si le nom contient uniquement des chiffres
        if (d.getNom().matches("\\d+")) {
            throw new IllegalArgumentException("Le nom de la destination ne peut pas contenir uniquement des chiffres");
        }

        // Vérifier si le nouveau nom existe déjà pour une autre destination (insensible à la casse)
        String checkQuery = "SELECT COUNT(*) FROM `destinations` WHERE LOWER(`nom`) = LOWER(?) AND `id` != ?";
        try {
            PreparedStatement checkStmt = cnx.prepareStatement(checkQuery);
            checkStmt.setString(1, d.getNom().trim());
            checkStmt.setInt(2, d.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new IllegalArgumentException("Une destination avec ce nom existe déjà (vérification insensible à la casse)");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du nom: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Procéder à la mise à jour
        String qry = "UPDATE `destinations` SET `nom` = ?, `url` = ? WHERE `id` = ?";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, d.getNom().trim());
            pstm.setString(2, d.getUrl());
            pstm.setInt(3, d.getId());
            pstm.executeUpdate();
            System.out.println("Destination mise à jour: " + d.getNom());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la destination: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void delete(Destinations d) {
        String qry = "DELETE FROM `destinations` WHERE `id` = ?";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, d.getId());
            pstm.executeUpdate();
            System.out.println("Destination supprimée: ID=" + d.getId());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la destination: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
