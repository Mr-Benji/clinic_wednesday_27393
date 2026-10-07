package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepo;

    public String saveOffice(Office office) {
        Office existing = officeRepo.findByOfficeNumber(office.getOfficeNumber());
        if (existing != null) {
            return "Office is already exist";
        }
        officeRepo.save(office);
        return "Office saved successfully";
    }

    public List<Office> getAllOffices() {
        return officeRepo.findAll();
    }

    public List<Office> searchOfficesByName(String name) {
        return officeRepo.findByName(name);
    }

    public Optional<Office> getOfficeById(UUID id) {
        return officeRepo.findById(id);
    }

    public String updateOffice(UUID id, Office newData) {
        Optional<Office> found = officeRepo.findById(id);
        if (found.isEmpty()) {
            return "Office not found";
        }

        Office other = officeRepo.findByOfficeNumber(newData.getOfficeNumber());
        if (other != null && !other.getId().equals(id)) {
            return "Office is already exist";
        }

        Office office = found.get();
        office.setName(newData.getName());
        office.setOfficeNumber(newData.getOfficeNumber());
        officeRepo.save(office);
        return "Office updated successfully";
    }

    public String deleteOffice(UUID id) {
        Optional<Office> found = officeRepo.findById(id);
        if (found.isEmpty()) {
            return "Office not found";
        }
        if (found.get().getDoctor() != null) {
            return "Office is assigned to a doctor";
        }
        officeRepo.deleteById(id);
        return "Office deleted successfully";
    }
}