package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Recherche;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecherche implements IService<Recherche> {

    private Connection cnx;

    public ServiceRecherche() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Recherche recherche) {
        String qry = "INSERT INTO recherche (name, localisation, typeActivite, plagePrixMin, plagePrixMax) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, recherche.getName()); // ou getUserId()
            pstm.setString(2, recherche.getLocalisation());
            pstm.setString(3, recherche.getTypeActivite());
            pstm.setDouble(4, recherche.getPlagePrixMin());
            pstm.setDouble(5, recherche.getPlagePrixMax());

            pstm.executeUpdate();
            System.out.println(" Recherche ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(" Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public List<Recherche> getAll() {
        List<Recherche> recherches = new ArrayList<>();
        String qry = "SELECT * FROM recherche ORDER BY dateRecherche DESC";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()) {
                Recherche r = new Recherche();
                r.setIdRecherche(rs.getInt("idRecherche"));
                r.setName(rs.getString("name")); // ou setUserId()
                r.setLocalisation(rs.getString("localisation"));
                r.setTypeActivite(rs.getString("typeActivite"));
                r.setPlagePrixMin(rs.getDouble("plagePrixMin"));
                r.setPlagePrixMax(rs.getDouble("plagePrixMax"));
                r.setDateRecherche(rs.getTimestamp("dateRecherche"));

                recherches.add(r);
            }

        } catch (SQLException e) {
            System.out.println(" Erreur récupération : " + e.getMessage());
        }

        return recherches;
    }

    // Méthode personnalisée : récupérer les recherches d'un utilisateur
    public List<Recherche> getByUserId(String name) {
        List<Recherche> recherches = new ArrayList<>();
        String qry = "SELECT * FROM recherche WHERE name = ? ORDER BY dateRecherche DESC";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, name);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Recherche r = new Recherche();
                r.setIdRecherche(rs.getInt("idRecherche"));
                r.setName(rs.getString("name"));
                r.setLocalisation(rs.getString("localisation"));
                r.setTypeActivite(rs.getString("typeActivite"));
                r.setPlagePrixMin(rs.getDouble("plagePrixMin"));
                r.setPlagePrixMax(rs.getDouble("plagePrixMax"));
                r.setDateRecherche(rs.getTimestamp("dateRecherche"));

                recherches.add(r);
            }

        } catch (SQLException e) {
            System.out.println(" Erreur récupération par utilisateur : " + e.getMessage());
        }

        return recherches;
    }

    @Override
    public void update(Recherche recherche) {

    }


    @Override
    public void delete(Recherche recherche) {
        String qry = "DELETE FROM recherche WHERE idRecherche = ?";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, recherche.getIdRecherche());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(" Recherche supprimée !");
            } else {
                System.out.println(" Aucune recherche trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println(" Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
