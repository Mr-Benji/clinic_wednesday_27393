package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;


    public String saveAppointment(Appointment appointment, UUID patientId, UUID doctorId){

        if(appointment.getAppointmentDate() == null){
            return "Appointment date is required";
        }

        if(appointment.getAppointmentDate().isBefore(LocalDate.now())){
            return "Appointment date cannot be in the past";
        }

        Patient patient = patientRepo.findById(patientId).orElse(null);
        if(patient == null){
            return "Patient not found";
        }

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if(doctor == null){
            return "Doctor not found";
        }

        // a doctor can not have two active appointments on the same date
        boolean booked = appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, appointment.getAppointmentDate(), AppointmentStatus.CANCELLED);
        if(booked){
            return "Doctor is already booked on that date";
        }

        // a new appointment always starts as SCHEDULED, whatever the body says
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointmentRepo.save(appointment);

        return "Appointment saved successfully";
    }

    public List<Appointment> getAllAppointments(){
        return appointmentRepo.findAll();
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status){
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(LocalDate start, LocalDate end){
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> countAppointmentsByStatus(){
        return appointmentRepo.countAppointmentsByStatus();
    }

    @Transactional
    public String cancelDoctorDay(UUID doctorId, LocalDate date){

        int cancelled = appointmentRepo.cancelDoctorDay(doctorId, date,
                AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);

        return cancelled + " appointments cancelled";
    }

    public Page<Appointment> getAppointmentsPage(Pageable pageable){
        return appointmentRepo.findAll(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date){

        int deleted = appointmentRepo.deleteByStatusBefore(AppointmentStatus.CANCELLED, date);

        return deleted + " appointments deleted";
    }

    public Appointment getAppointmentById(UUID id){
        return appointmentRepo.findById(id).orElse(null);
    }

    public String updateAppointment(UUID id, Appointment appointment, UUID patientId, UUID doctorId){

        Appointment existing = appointmentRepo.findById(id).orElse(null);
        if(existing == null){
            return "The appointment with that id does not exist";
        }

        if(appointment.getAppointmentDate() == null){
            return "Appointment date is required";
        }

        // patient and doctor are only changed when their id is given
        if(patientId != null){
            Patient patient = patientRepo.findById(patientId).orElse(null);
            if(patient == null){
                return "Patient not found";
            }
            existing.setPatient(patient);
        }

        if(doctorId != null){
            Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
            if(doctor == null){
                return "Doctor not found";
            }
            existing.setDoctor(doctor);
        }

        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setReason(appointment.getReason());
        // the status is not changed here, only through the status endpoint
        appointmentRepo.save(existing);

        return "Appointment updated successfully";
    }

    public String changeStatus(UUID id, AppointmentStatus newStatus){

        Appointment appointment = appointmentRepo.findById(id).orElse(null);
        if(appointment == null){
            return "The appointment with that id does not exist";
        }

        AppointmentStatus currentStatus = appointment.getStatus();
        if(!canChangeStatus(currentStatus, newStatus)){
            return "Cannot change status from " + currentStatus + " to " + newStatus;
        }
        appointment.setStatus(newStatus);
        appointmentRepo.save(appointment);

        return "Appointment status updated successfully";
    }

    public String reschedule(UUID id, LocalDate newDate){

        Appointment appointment = appointmentRepo.findById(id).orElse(null);
        if(appointment == null){
            return "The appointment with that id does not exist";
        }

        AppointmentStatus status = appointment.getStatus();
        if(status == AppointmentStatus.COMPLETED || status == AppointmentStatus.CANCELLED){
            return "A " + status + " appointment cannot be rescheduled";
        }

        if(newDate.isBefore(LocalDate.now())){
            return "Appointment date cannot be in the past";
        }

        boolean booked = appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED);
        if(booked){
            return "Doctor is already booked on that date";
        }

        // a rescheduled appointment has to be confirmed again
        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);

        return "Appointment rescheduled successfully";
    }

    // SCHEDULED -> CONFIRMED or CANCELLED, CONFIRMED -> COMPLETED or CANCELLED
    // COMPLETED and CANCELLED are final
    private boolean canChangeStatus(AppointmentStatus current, AppointmentStatus next){

        if(current == AppointmentStatus.SCHEDULED){
            return next == AppointmentStatus.CONFIRMED || next == AppointmentStatus.CANCELLED;
        }
        if(current == AppointmentStatus.CONFIRMED){
            return next == AppointmentStatus.COMPLETED || next == AppointmentStatus.CANCELLED;
        }
        return false;
    }

    public String deleteAppointment(UUID id){

        Appointment existing = appointmentRepo.findById(id).orElse(null);
        if(existing == null){
            return "The appointment with that id does not exist";
        }

        if(prescriptionRepo.existsByAppointmentId(id)){
            return "That appointment has prescriptions, delete them first";
        }
        appointmentRepo.delete(existing);

        return "Appointment deleted successfully";
    }

}
