package hr.pmatkovic.spring.controller;

import hr.pmatkovic.spring.ReservationService;
import hr.pmatkovic.spring.entity.*;
import hr.pmatkovic.spring.repository.CustomerRepository;
import hr.pmatkovic.spring.repository.ReservationRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
public class ReservationController {

    private ReservationService reservationService;

    @Autowired
    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    private CustomerRepository customerRepository;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/reservations")
    private ResponseEntity<List<Reservation>> getAllReservations() {

        try {
            List<Reservation> list = reservationService.getAllReservations();

            if (list.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(list, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reservations/{id}")
    private ResponseEntity<Reservation> getReservationById(@PathVariable("id") Long id) {

        try {
            Optional<Reservation> reservation = reservationService.getReservationById(id);
            if (reservation.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reservations")
    private ResponseEntity<Reservation> saveReservation(@RequestBody ReservationRequest request) {

        try {

            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setSurname(request.getSurname());
            customer.setEmail(request.getEmail());
            customer.setRegular(request.getRegular());
            customer.setVip(request.getVip());
            customer = customerRepository.save(customer);

            Reservation reservation = new Reservation(request);
            reservation.setCustomer(customer);

            reservation = reservationService.saveReservation(reservation);

            return new ResponseEntity<>(reservation, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/reservations/{id}")
    private ResponseEntity<String> deleteReservation(@PathVariable("id") Long id) {

        try {
            reservationService.deleteReservation(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
