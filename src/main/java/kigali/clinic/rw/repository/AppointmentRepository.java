package kigali.clinic.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Patient;

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

    // D1: the doctor's appointments on that date, without the given status
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate appointmentDate,
            AppointmentStatus status);

    // D2: the patient's appointments from the given date on, in one of the statuses, soonest first
    List<Appointment> findByPatientAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            Patient patient, LocalDate appointmentDate, List<AppointmentStatus> statuses);

    // D4: appointments on the given date
    long countByAppointmentDate(LocalDate appointmentDate);

    // D4: appointments in the given status
    long countByStatus(AppointmentStatus status);

    // C1: number of appointments for each status
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus();

    // C4: cancels every appointment of the doctor on that date, except the completed ones
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Appointment a SET a.status = :cancelled WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> :completed")
    int cancelDoctorDay(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date,
            @Param("cancelled") AppointmentStatus cancelled, @Param("completed") AppointmentStatus completed);

    // bonus: deletes the appointments in the given status dated before the given date
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteByStatusBefore(@Param("status") AppointmentStatus status, @Param("date") LocalDate date);
}
