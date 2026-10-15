package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private SpecializationRepository specializationRepo;


    public String saveDoctor(Doctor doctor, UUID officeId){

        if(doctorExists(doctor, null)){
            return "Doctor with the same first name, last name and date of birth already exists";
        }
        // the office is optional, a doctor can be saved without one
        if(officeId != null){
            String officeCheck = assignOffice(doctor, officeId, null);
            if(officeCheck != null){
                return officeCheck;
            }
        }
        doctorRepo.save(doctor);

        return "Doctor saved successfully";
    }

    public List<Doctor> getAllDoctors(){
        return doctorRepo.findAll();
    }

    public Doctor getDoctorById(UUID id){
        return doctorRepo.findById(id).orElse(null);
    }

    public List<Doctor> getDoctorsBySpecialization(String name){
        return doctorRepo.findDoctorsBySpecializationName(name);
    }

    public List<Doctor> getDoctorsWithoutOffice(){
        return doctorRepo.findDoctorsWithoutOffice();
    }

    public List<Doctor> searchDoctorsByName(String name){
        return doctorRepo.searchByName(name);
    }

    public String updateDoctor(UUID id, Doctor doctor, UUID officeId){

        Doctor existing = doctorRepo.findById(id).orElse(null);
        if(existing == null){
            return "Doctor not found";
        }

        if(doctorExists(doctor, id)){
            return "Doctor with the same first name, last name and date of birth already exists";
        }

        // office is only changed when an officeId is given
        if(officeId != null){
            String officeCheck = assignOffice(existing, officeId, id);
            if(officeCheck != null){
                return officeCheck;
            }
        }

        existing.setFirstName(doctor.getFirstName());
        existing.setLastName(doctor.getLastName());
        existing.setDateOfBirth(doctor.getDateOfBirth());
        doctorRepo.save(existing);

        return "Doctor updated successfully";
    }

    @Transactional
    public String deleteDoctor(UUID id){

        Doctor existing = doctorRepo.findById(id).orElse(null);
        if(existing == null){
            return "Doctor not found";
        }
        if(!existing.getAppointments().isEmpty()){
            return "Doctor has appointments";
        }
        doctorRepo.delete(existing);

        return "Doctor deleted successfully";
    }

    @Transactional
    public String addSpecialization(UUID doctorId, UUID specializationId){

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if(doctor == null){
            return "The doctor with that id does not exist";
        }

        Specialization specialization = specializationRepo.findById(specializationId).orElse(null);
        if(specialization == null){
            return "That specialization does not exist";
        }

        if(doctor.getSpecializations().contains(specialization)){
            return "The doctor already has that specialization";
        }
        doctor.getSpecializations().add(specialization);
        doctorRepo.save(doctor);

        return "Specialization added to the doctor";
    }

    // only the row in doctor_specialization is removed, the specialization stays
    @Transactional
    public String removeSpecialization(UUID doctorId, UUID specializationId){

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if(doctor == null){
            return "The doctor with that id does not exist";
        }

        Specialization specialization = specializationRepo.findById(specializationId).orElse(null);
        if(specialization == null){
            return "That specialization does not exist";
        }

        if(!doctor.getSpecializations().contains(specialization)){
            return "The doctor does not have that specialization";
        }
        doctor.getSpecializations().remove(specialization);
        doctorRepo.save(doctor);

        return "Specialization removed from the doctor";
    }

    // the old office becomes free once the doctor is saved with the new one
    public String changeOffice(UUID doctorId, int officeNumber){

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if(doctor == null){
            return "The doctor with that id does not exist";
        }

        Office office = officeRepo.findByOfficeNumber(officeNumber);
        if(office == null){
            return "The office with that office number does not exist";
        }

        if(doctorRepo.existsByOfficeAndIdNot(office, doctorId)){
            return "That office is taken by other doctor";
        }
        doctor.setOffice(office);
        doctorRepo.save(doctor);

        return "Office changed successfully";
    }

    // returns an error message, or null when the office was assigned
    private String assignOffice(Doctor doctor, UUID officeId, UUID doctorId){

        Office office = officeRepo.findById(officeId).orElse(null);
        if(office == null){
            return "Office not found";
        }

        Doctor owner = doctorRepo.findByOfficeId(officeId);
        if(owner != null && !owner.getId().equals(doctorId)){
            return "Office is already assigned to another doctor";
        }

        doctor.setOffice(office);
        return null;
    }

    // true when another doctor (not doctorId) already has this first name, last name and date of birth
    private boolean doctorExists(Doctor doctor, UUID doctorId){

        List<Doctor> matches = doctorRepo.findByFullNameAndDateOfBirth(
                doctor.getFirstName(), doctor.getLastName(), doctor.getDateOfBirth());
        for(Doctor other : matches){
            if(!other.getId().equals(doctorId)){
                return true;
            }
        }
        return false;
    }

}
