package tn.esprit.models;

import java.sql.Timestamp;

public class Recherche {
    private int idRecherche;
    private String name; // anciennement userId
    private String localisation;
    private String typeActivite;
    private double plagePrixMin;
    private double plagePrixMax;
    private Timestamp dateRecherche;

    public Recherche() {
    }

    public Recherche(int idRecherche, String name, String localisation, String typeActivite, double plagePrixMin, double plagePrixMax, Timestamp dateRecherche) {
        this.idRecherche = idRecherche;
        this.name = name;
        this.localisation = localisation;
        this.typeActivite = typeActivite;
        this.plagePrixMin = plagePrixMin;
        this.plagePrixMax = plagePrixMax;
        this.dateRecherche = dateRecherche;
    }

    public Recherche(String name, String localisation, String typeActivite, double plagePrixMin, double plagePrixMax) {
        this.name = name;
        this.localisation = localisation;
        this.typeActivite = typeActivite;
        this.plagePrixMin = plagePrixMin;
        this.plagePrixMax = plagePrixMax;
    }

    public int getIdRecherche() {
        return idRecherche;
    }

    public void setIdRecherche(int idRecherche) {
        this.idRecherche = idRecherche;
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

    public String getTypeActivite() {
        return typeActivite;
    }

    public void setTypeActivite(String typeActivite) {
        this.typeActivite = typeActivite;
    }

    public double getPlagePrixMin() {
        return plagePrixMin;
    }

    public void setPlagePrixMin(double plagePrixMin) {
        this.plagePrixMin = plagePrixMin;
    }

    public double getPlagePrixMax() {
        return plagePrixMax;
    }

    public void setPlagePrixMax(double plagePrixMax) {
        this.plagePrixMax = plagePrixMax;
    }

    public Timestamp getDateRecherche() {
        return dateRecherche;
    }

    public void setDateRecherche(Timestamp dateRecherche) {
        this.dateRecherche = dateRecherche;
    }

    @Override
    public String toString() {
        return "Recherche{" +
                "idRecherche=" + idRecherche +
                ", name='" + name + '\'' +
                ", localisation='" + localisation + '\'' +
                ", typeActivite='" + typeActivite + '\'' +
                ", plagePrixMin=" + plagePrixMin +
                ", plagePrixMax=" + plagePrixMax +
                ", dateRecherche=" + dateRecherche +
                "}\n";
    }
}
