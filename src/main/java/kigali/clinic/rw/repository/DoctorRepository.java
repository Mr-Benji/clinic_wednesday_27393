package kigali.clinic.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.AppointmentStatus;
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

    // D3: doctors whose first or last name contains the text (case insensitive), sorted by last name
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.lastName ASC")
    List<Doctor> searchByName(@Param("name") String name);

    // bonus: first name, last name and number of appointments in the given status between two dates
    // (both inclusive), busiest doctor first; doctors with none are not in the result
    @Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d WHERE a.status = :status AND a.appointmentDate BETWEEN :start AND :end GROUP BY d.id, d.firstName, d.lastName ORDER BY COUNT(a) DESC")
    List<Object[]> findWorkload(@Param("status") AppointmentStatus status, @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // A4: true when the office belongs to a doctor other than the given one
    boolean existsByOfficeAndIdNot(Office office, UUID id);
}
