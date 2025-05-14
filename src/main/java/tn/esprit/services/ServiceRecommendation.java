
package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Recommendation;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecommendation implements IService<Recommendation> {

    private Connection cnx;

    public ServiceRecommendation() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Recommendation recommendation) {
        String qry = "INSERT INTO recommendation (name, localisation, typeRecommendation, dateRecommendation) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, recommendation.getName());
            pstm.setString(2, recommendation.getLocalisation());
            pstm.setString(3, recommendation.getTypeRecommendation());
            pstm.setTimestamp(4, recommendation.getDateRecommendation());

            pstm.executeUpdate();
            System.out.println(" Recommendation ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(" Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public List<Recommendation> getAll() {
        List<Recommendation> recommendations = new ArrayList<>();
        String qry = "SELECT * FROM recommendation ORDER BY dateRecommendation DESC";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()) {
                Recommendation r = new Recommendation();
                r.setIdRecommendation(rs.getInt("idRecommendation"));
                r.setName(rs.getString("name"));
                r.setLocalisation(rs.getString("localisation"));
                r.setTypeRecommendation(rs.getString("typeRecommendation"));
                r.setDateRecommendation(rs.getTimestamp("dateRecommendation"));

                recommendations.add(r);
            }

        } catch (SQLException e) {
            System.out.println(" Erreur récupération : " + e.getMessage());
        }

        return recommendations;
    }
    /*
    // Méthode mch fl crud : récupérer les recommandations d'un utilisateur
    public List<Recommendation> getByUserId(String name) {
        List<Recommendation> recommendations = new ArrayList<>();
        String qry = "SELECT * FROM recommendation WHERE name = ? ORDER BY dateRecommendation DESC";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, name);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Recommendation r = new Recommendation();
                r.setIdRecommendation(rs.getInt("idRecommendation"));
                r.setName(rs.getString("name"));
                r.setLocalisation(rs.getString("localisation"));
                r.setTypeRecommendation(rs.getString("typeRecommendation"));
                r.setDateRecommendation(rs.getTimestamp("dateRecommendation"));

                recommendations.add(r);
            }

        } catch (SQLException e) {
            System.out.println(" Erreur récupération par utilisateur : " + e.getMessage());
        }

        return recommendations;
    }
    */
    @Override
    public void update(Recommendation recommendation) {

    }

    @Override
    public void delete(Recommendation recommendation) {
        String qry = "DELETE FROM recommendation WHERE idRecommendation = ?";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, recommendation.getIdRecommendation());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(" Recommendation supprimée !");
            } else {
                System.out.println(" Aucune recommendation trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println(" Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
