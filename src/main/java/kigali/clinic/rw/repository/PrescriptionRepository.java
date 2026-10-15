package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    // C2: prescriptions written for the given appointment
    List<Prescription> findByAppointment(Appointment appointment);

    // C3: every prescription of the patient across all appointments, newest first
    @Query("SELECT p FROM Prescription p WHERE p.appointment.patient.ssNumber = :ssNumber ORDER BY p.issuedDate DESC")
    List<Prescription> findPatientHistory(@Param("ssNumber") String ssNumber);

    // C4: true when the appointment has at least one prescription
    boolean existsByAppointmentId(UUID appointmentId);
}
