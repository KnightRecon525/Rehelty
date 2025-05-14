package tn.esprit.models;

import java.sql.Timestamp;

public class Recommendation {
    private int idRecommendation;
    private String name; // anciennement userId
    private String localisation;
    private String typeRecommendation;
    private Timestamp dateRecommendation;

    // Constructeur par défaut
    public Recommendation() {
    }

    // Constructeur avec tous les paramètres
    public Recommendation(int idRecommendation, String name, String localisation, String typeRecommendation, Timestamp dateRecommendation) {
        this.idRecommendation = idRecommendation;
        this.name = name;
        this.localisation = localisation;
        this.typeRecommendation = typeRecommendation;
        this.dateRecommendation = dateRecommendation;
    }

    // Constructeur sans l'id, utilisé pour les ajouts de nouvelles recommandations
    public Recommendation(String name, String localisation, String typeRecommendation) {
        this.name = name;
        this.localisation = localisation;
        this.typeRecommendation = typeRecommendation;
    }

    // Getters et Setters
    public int getIdRecommendation() {
        return idRecommendation;
    }

    public void setIdRecommendation(int idRecommendation) {
        this.idRecommendation = idRecommendation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getTypeRecommendation() {
        return typeRecommendation;
    }

    public void setTypeRecommendation(String typeRecommendation) {
        this.typeRecommendation = typeRecommendation;
    }

    public Timestamp getDateRecommendation() {
        return dateRecommendation;
    }

    public void setDateRecommendation(Timestamp dateRecommendation) {
        this.dateRecommendation = dateRecommendation;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "idRecommendation=" + idRecommendation +
                ", name='" + name + '\'' +
                ", localisation='" + localisation + '\'' +
                ", typeRecommendation='" + typeRecommendation + '\'' +
                ", dateRecommendation=" + dateRecommendation +
                "}\n";
    }
}
