package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    Office findByOfficeNumber(int offNumber);

    // offices whose name contains the given text (case insensitive)
    @Query("SELECT o FROM Office o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Office> findByName(@Param("name") String name);

    // C3: office name, number and appointment count, office with the most appointments first
    @Query("SELECT o.name, o.officeNumber, COUNT(a) FROM Appointment a JOIN a.doctor d JOIN d.office o GROUP BY o.id, o.name, o.officeNumber ORDER BY COUNT(a) DESC")
    List<Object[]> findBusiestOffices(Limit limit);
}
