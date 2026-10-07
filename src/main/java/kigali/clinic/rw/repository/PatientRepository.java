package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    // A1: patients with the given last name (case insensitive), sorted by first name
    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);
}
