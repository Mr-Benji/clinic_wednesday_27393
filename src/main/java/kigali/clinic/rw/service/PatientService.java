package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;


    public String savePatient(Patient patient){

        patientRepo.save(patient);

        return "Patient saved successfully";
    }

    public List<Patient> getAllPatients(){
        return patientRepo.findAll();
    }

    public Patient getPatientById(UUID id){
        return patientRepo.findById(id).orElse(null);
    }

    public String updatePatient(UUID id, Patient patient){

        Patient existing = patientRepo.findById(id).orElse(null);
        if(existing == null){
            return "Patient not found";
        }

        existing.setFirstName(patient.getFirstName());
        existing.setLastName(patient.getLastName());
        existing.setDateOfBirth(patient.getDateOfBirth());
        patientRepo.save(existing);

        return "Patient updated successfully";
    }

    @Transactional
    public String deletePatient(UUID id){

        Patient existing = patientRepo.findById(id).orElse(null);
        if(existing == null){
            return "Patient not found";
        }
        if(!existing.getAppointments().isEmpty()){
            return "Patient has appointments";
        }
        patientRepo.delete(existing);

        return "Patient deleted successfully";
    }

}
