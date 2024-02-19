package hr.pmatkovic.spring.repository;

import hr.pmatkovic.spring.entity.Customer;
import hr.pmatkovic.spring.entity.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRespository extends CrudRepository<Reservation, Long> {
}
