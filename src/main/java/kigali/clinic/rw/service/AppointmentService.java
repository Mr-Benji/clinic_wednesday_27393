package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;


    public String saveAppointment(Appointment appointment, UUID patientId, UUID doctorId){

        if(appointment.getAppointmentDate() == null){
            return "Appointment date is required";
        }

        Patient patient = patientRepo.findById(patientId).orElse(null);
        if(patient == null){
            return "Patient not found";
        }

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if(doctor == null){
            return "Doctor not found";
        }

        if(appointment.getStatus() == null){
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointmentRepo.save(appointment);

        return "Appointment saved successfully";
    }

    public List<Appointment> getAllAppointments(){
        return appointmentRepo.findAll();
    }

    public Appointment getAppointmentById(UUID id){
        return appointmentRepo.findById(id).orElse(null);
    }

    public String updateAppointment(UUID id, Appointment appointment, UUID patientId, UUID doctorId){

        Appointment existing = appointmentRepo.findById(id).orElse(null);
        if(existing == null){
            return "Appointment not found";
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
        if(appointment.getStatus() != null){
            existing.setStatus(appointment.getStatus());
        }
        appointmentRepo.save(existing);

        return "Appointment updated successfully";
    }

    public String deleteAppointment(UUID id){

        Appointment existing = appointmentRepo.findById(id).orElse(null);
        if(existing == null){
            return "Appointment not found";
        }
        appointmentRepo.delete(existing);

        return "Appointment deleted successfully";
    }

}
