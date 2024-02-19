package hr.pmatkovic.threads;

import hr.pmatkovic.entities.service.Reservation;
import hr.pmatkovic.entities.service.ReservationType;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import java.util.Optional;

import static hr.pmatkovic.utils.DatabaseUtils.checkNumberOfReservations;
import static hr.pmatkovic.utils.DatabaseUtils.getLastReservation;

/**
 * A service that verifies the availability of new reservations
 */

public class ReservationQueryService extends ScheduledService<Reservation> {

    private boolean firstRun;
    private int count;

    private Reservation lastReservation;

    public ReservationQueryService() {

        this.firstRun = true;
    }

    @Override
    protected Task<Reservation> createTask() {
        return new Task<>() {
            @Override
            protected Reservation call() {

                if (firstRun) {

                    count = checkNumberOfReservations();
//                    Optional<Reservation> reservation = getLastReservation();
//                    reservation.ifPresent(value -> lastReservation = value);
                    firstRun = false;
                }
                synchronized (this) {

                    int newCount = checkNumberOfReservations();
                    count = Math.min(newCount, count);

                    Optional<Reservation> reservation = getLastReservation();
                    if (reservation.isPresent() && reservation.get().getReservationType().equals(ReservationType.WEB) && newCount > count) {
                    Platform.runLater(() -> updateValue(reservation.get()));

//                    lastReservation = reservation.get();
                    count = newCount;

                    }
                }
                return null;
            }
        };
    }

}