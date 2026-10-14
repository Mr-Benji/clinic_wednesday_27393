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

    public List<Specialization> getUnusedSpecializations(){
        return specializationRepo.findUnusedSpecializations();
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

    // the doctors keep existing, only their link to this specialization is removed
    @Transactional
    public String deleteSpecialization(UUID id){

        Specialization existing = specializationRepo.findById(id).orElse(null);
        if(existing == null){
            return "Specialization not found";
        }
        for(Doctor doctor : existing.getDoctors()){
            doctor.getSpecializations().remove(existing);
            doctorRepo.save(doctor);
        }
        specializationRepo.delete(existing);

        return "Specialization deleted successfully";
    }

    // Doctor owns the join table, so the link is added on the doctor side
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

        if(doctor.getSpecializations().contains(specialization)){
            return "Doctor already has this specialization";
        }
        doctor.getSpecializations().add(specialization);
        doctorRepo.save(doctor);

        return "Doctor added to specialization successfully";
    }

}
