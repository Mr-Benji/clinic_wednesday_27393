package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specializationRepo;

    @Autowired
    private DoctorRepository doctorRepo;


    public String saveSpecialization(Specialization specialization){

        specializationRepo.save(specialization);

        return "Specialization saved successfully";
    }

    public List<Specialization> getAllSpecializations(){
        return specializationRepo.findAll();
    }

    public Specialization getSpecializationById(UUID id){
        return specializationRepo.findById(id).orElse(null);
    }

    public String updateSpecialization(UUID id, Specialization specialization){

        Specialization existing = specializationRepo.findById(id).orElse(null);
        if(existing == null){
            return "Specialization not found";
        }

        existing.setName(specialization.getName());
        specializationRepo.save(existing);

        return "Specialization updated successfully";
    }

    // the rows in doctor_specialization are removed with it, the doctors stay
    public String deleteSpecialization(UUID id){

        Specialization existing = specializationRepo.findById(id).orElse(null);
        if(existing == null){
            return "Specialization not found";
        }
        specializationRepo.delete(existing);

        return "Specialization deleted successfully";
    }

    // Specialization owns the join table, so the doctor is added on this side
    @Transactional
    public String addDoctor(UUID specializationId, UUID doctorId){

        Specialization specialization = specializationRepo.findById(specializationId).orElse(null);
        if(specialization == null){
            return "Specialization not found";
        }

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if(doctor == null){
            return "Doctor not found";
        }

        if(specialization.getDoctors().contains(doctor)){
            return "Doctor already has this specialization";
        }
        specialization.getDoctors().add(doctor);
        specializationRepo.save(specialization);

        return "Doctor added to specialization successfully";
    }

}
