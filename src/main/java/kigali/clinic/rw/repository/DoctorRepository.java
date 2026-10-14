package kigali.clinic.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    // the doctor who owns the given office, or null when the office is free
    @Query("SELECT d FROM Doctor d WHERE d.office.id = :officeId")
    Doctor findByOfficeId(@Param("officeId") UUID officeId);

    // doctors with the same first name, last name (case insensitive) and date of birth
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) = LOWER(:firstName) AND LOWER(d.lastName) = LOWER(:lastName) AND d.dateOfBirth = :dateOfBirth")
    List<Doctor> findByFullNameAndDateOfBirth(@Param("firstName") String firstName, @Param("lastName") String lastName,
            @Param("dateOfBirth") LocalDate dateOfBirth);

    // B1: doctors holding the specialization with the given name (case insensitive)
    @Query("SELECT d FROM Doctor d JOIN d.specializations s WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findDoctorsBySpecializationName(@Param("name") String name);

    // B2: doctors with no office assigned, sorted by last name
    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName ASC")
    List<Doctor> findDoctorsWithoutOffice();

    // A4: true when the office belongs to a doctor other than the given one
    boolean existsByOfficeAndIdNot(Office office, UUID id);
}
