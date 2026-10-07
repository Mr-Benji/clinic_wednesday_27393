package kigali.clinic.rw.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    // the doctor who owns the given office, or null when the office is free
    @Query("SELECT d FROM Doctor d WHERE d.office.id = :officeId")
    Doctor findByOfficeId(@Param("officeId") UUID officeId);

    // doctors with the same first name, last name (case insensitive) and date of birth
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) = LOWER(:firstName) AND LOWER(d.lastName) = LOWER(:lastName) AND d.dateOfBirth = :dateOfBirth")
    List<Doctor> findByFullNameAndDateOfBirth(@Param("firstName") String firstName, @Param("lastName") String lastName,
            @Param("dateOfBirth") Date dateOfBirth);
}
