package tn.esprit.interfaces;

import tn.esprit.models.Bus;

import java.time.LocalDate;
import java.util.List;

public interface IServiceBus {
    void add(Bus bus);
    List<Bus> getAll();
    void update(Bus bus);
    void delete(Bus bus);
    boolean reserverPlace(int idBus);
    List<Bus> getByTrajet(String depart, String arrivee, LocalDate date);
}