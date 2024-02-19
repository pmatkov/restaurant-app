package hr.pmatkovic.threads;

import hr.pmatkovic.entities.service.Reservation;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

import static hr.pmatkovic.utils.DatabaseUtils.getReservations;

/**
 * A task which retrieves a list of reservations
 */

public class GetReservationTask extends Task<List<Reservation>> {

    List<Reservation> listOfReservations = new ArrayList<>();

    @Override
    protected List<Reservation> call() throws Exception {

        synchronized (this) {
            listOfReservations = getReservations(null);
        }

        return listOfReservations;
    }
}