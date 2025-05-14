package tn.esprit.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Bus {
    private int id;
    private String numeroLigne;
    private String pointDepart;
    private String pointArrivee;
    private LocalTime heureDepart;
    private LocalTime heureArrivee;
    private int capacite;
    private int placesRestantes;
    private String joursCirculation;
    private double prix;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Constructeur vide
    public Bus() {}

    // Constructeur complet
    public Bus(int id, String numeroLigne, String pointDepart, String pointArrivee,
               LocalTime heureDepart, LocalTime heureArrivee, int capacite,
               int placesRestantes, String joursCirculation, double prix) {
        this.id = id;
        this.numeroLigne = numeroLigne;
        this.pointDepart = pointDepart;
        this.pointArrivee = pointArrivee;
        this.heureDepart = heureDepart;
        this.heureArrivee = heureArrivee;
        this.capacite = capacite;
        this.placesRestantes = placesRestantes;
        this.joursCirculation = joursCirculation;
        this.prix = prix;
        this.dateDebut = LocalDate.now();
        this.dateFin = LocalDate.now();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }
    public LocalDate getDateFin() {
        return dateFin;
    }
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroLigne() {
        return numeroLigne;
    }

    public void setNumeroLigne(String numeroLigne) {
        this.numeroLigne = numeroLigne;
    }

    public String getPointDepart() {
        return pointDepart;
    }

    public void setPointDepart(String pointDepart) {
        this.pointDepart = pointDepart;
    }

    public String getPointArrivee() {
        return pointArrivee;
    }

    public void setPointArrivee(String pointArrivee) {
        this.pointArrivee = pointArrivee;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public LocalTime getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(LocalTime heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public int getPlacesRestantes() {
        return placesRestantes;
    }

    public void setPlacesRestantes(int placesRestantes) {
        this.placesRestantes = placesRestantes;
    }

    public String getJoursCirculation() {
        return joursCirculation;
    }

    public void setJoursCirculation(String joursCirculation) {
        this.joursCirculation = joursCirculation;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    // Méthode toString()
    @Override
    public String toString() {
        return "Bus{" +
                "id=" + id +
                ", numeroLigne='" + numeroLigne + '\'' +
                ", pointDepart='" + pointDepart + '\'' +
                ", pointArrivee='" + pointArrivee + '\'' +
                ", heureDepart=" + heureDepart +
                ", heureArrivee=" + heureArrivee +
                ", capacite=" + capacite +
                ", placesRestantes=" + placesRestantes +
                ", joursCirculation='" + joursCirculation + '\'' +
                ", prix=" + prix +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                '}';
    }
}