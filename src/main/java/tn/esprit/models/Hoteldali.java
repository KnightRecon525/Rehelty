package tn.esprit.models;

public class Hoteldali {
    private int id;
    private String nom;
    private String localisation;
    private String description;
    private int nbrChambres;
    private float prixParNuite;
    private int etoiles;
    private String equipements;
    private String imageUrl;
    private float note;

    public Hoteldali() {}

    public Hoteldali(int id, String nom, String localisation, String description, int nbrChambres,
                     float prixParNuite, int etoiles, String equipements, String imageUrl, float note) {
        this.id = id;
        this.nom = nom;
        this.localisation = localisation;
        this.description = description;
        this.nbrChambres = nbrChambres;
        this.prixParNuite = prixParNuite;
        this.etoiles = etoiles;
        this.equipements = equipements;
        this.imageUrl = imageUrl;
        this.note = note;
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getLocalisation() { return localisation; }
    public String getDescription() { return description; }
    public int getNbrChambres() { return nbrChambres; }
    public float getPrixParNuite() { return prixParNuite; }
    public int getEtoiles() { return etoiles; }
    public String getEquipements() { return equipements; }
    public String getImageUrl() { return imageUrl; }
    public float getNote() { return note; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public void setDescription(String description) { this.description = description; }
    public void setNbrChambres(int nbrChambres) { this.nbrChambres = nbrChambres; }
    public void setPrixParNuite(float prixParNuite) { this.prixParNuite = prixParNuite; }
    public void setEtoiles(int etoiles) { this.etoiles = etoiles; }
    public void setEquipements(String equipements) { this.equipements = equipements; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setNote(float note) { this.note = note; }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", localisation='" + localisation + '\'' +
                ", description='" + description + '\'' +
                ", nbrChambres=" + nbrChambres +
                ", prixParNuite=" + prixParNuite +
                ", etoiles=" + etoiles +
                ", equipements='" + equipements + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", note=" + note +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hoteldali hotel = (Hoteldali) o;

        // Two hotels are considered equal if they have the same name and location
        if (!nom.equals(hotel.nom)) return false;
        return localisation.equals(hotel.localisation);
    }

    @Override
    public int hashCode() {
        int result = nom.hashCode();
        result = 31 * result + localisation.hashCode();
        return result;
    }
}