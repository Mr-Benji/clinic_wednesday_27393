package kigali.clinic.rw.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.DoctorService;
import kigali.clinic.rw.service.PatientService;


@RestController
@RequestMapping (value = {"/api/patient", "/api/patients"})
public class PatientController {


    @Autowired
    private PatientService patientServ;

    @Autowired
    private DoctorService doctorServ;

    @PostMapping(value="/save")
    public ResponseEntity<?> savePatient(@RequestBody Patient patient){

        String returnedMessage = patientServ.savePatient(patient);
        return toResponse(returnedMessage);
    }

    @GetMapping(value="/all")
    public ResponseEntity<?> getAllPatients(){
        return new ResponseEntity<>(patientServ.getAllPatients(), HttpStatus.OK);
    }

    // e.g. /api/patients/by-last-name?lastName=uwase
    @GetMapping(value="/by-last-name")
    public ResponseEntity<?> getPatientsByLastName(@RequestParam String lastName){
        return new ResponseEntity<>(patientServ.getPatientsByLastName(lastName), HttpStatus.OK);
    }

    @GetMapping(value="/of-doctor/{doctorId}")
    public ResponseEntity<?> getPatientsOfDoctor(@PathVariable UUID doctorId){

        if(doctorServ.getDoctorById(doctorId) == null){
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patientServ.getPatientsOfDoctor(doctorId), HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable UUID id){

        Patient patient = patientServ.getPatientById(id);
        if(patient == null){
            return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    @PutMapping(value="/update/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable UUID id, @RequestBody Patient patient){

        String returnedMessage = patientServ.updatePatient(id, patient);
        return toResponse(returnedMessage);
    }

    @DeleteMapping(value="/delete/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable UUID id){

        String returnedMessage = patientServ.deletePatient(id);
        return toResponse(returnedMessage);
    }

    private ResponseEntity<?> toResponse(String message){

        if(message.endsWith("successfully")){
            return new ResponseEntity<>(message, HttpStatus.OK);
        }else if(message.endsWith("not found")){
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }
}
