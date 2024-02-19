package hr.pmatkovic.threads;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.service.Reservation;
import javafx.concurrent.Task;

import static hr.pmatkovic.utils.DatabaseUtils.*;

/**
 * A task which makes changes to a reservation
 */

public class ModifyReservationTask extends Task<Reservation> {

    final Reservation reservation;
    private ChangeType changeType;

    public ModifyReservationTask(Reservation reservation, ChangeType changeType) {
        this.reservation = reservation;
        this.changeType = changeType;
    }

    @Override
    protected Reservation call() {

        synchronized (this) {

            if (changeType.equals(ChangeType.ADD))
                addReservation(reservation);
            else if (changeType.equals(ChangeType.EDIT))
                editReservation(reservation);
            else if (changeType.equals(ChangeType.DELETE))
                deleteReservation(reservation);
        }
        return null;
    }
}