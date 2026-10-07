package kigali.clinic.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // all appointments of the given patient, most recent first
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId ORDER BY a.appointmentDate DESC")
    List<Appointment> findAppointmentsByPatientId(@Param("patientId") UUID patientId);

    // A2: appointments in the given status, earliest date first
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    // A3: appointments between two dates (both inclusive), ordered by date
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

    // A4: true when the doctor has an appointment on that date whose status is not the given one
    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate appointmentDate,
            AppointmentStatus status);
}
