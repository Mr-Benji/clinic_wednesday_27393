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

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;


@RestController
@RequestMapping (value = {"/api/doctor", "/api/doctors"})
public class DoctorController {


    @Autowired
    private DoctorService doctorServ;

    @PostMapping(value="/save")
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor,
            @RequestParam(required = false) UUID officeId){

        String returnedMessage = doctorServ.saveDoctor(doctor, officeId);
        return toResponse(returnedMessage);
    }

    @GetMapping(value="/all")
    public ResponseEntity<?> getAllDoctors(){
        return new ResponseEntity<>(doctorServ.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id){

        Doctor doctor = doctorServ.getDoctorById(id);
        if(doctor == null){
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }

    @PutMapping(value="/update/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor,
            @RequestParam(required = false) UUID officeId){

        String returnedMessage = doctorServ.updateDoctor(id, doctor, officeId);
        return toResponse(returnedMessage);
    }

    @DeleteMapping(value="/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id){

        String returnedMessage = doctorServ.deleteDoctor(id);
        return toResponse(returnedMessage);
    }

    private ResponseEntity<?> toResponse(String message){

        if(message.endsWith("successfully")){
            return new ResponseEntity<>(message, HttpStatus.OK);
        }else if(message.endsWith("not found")){
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }else if(message.endsWith("required")){
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }
}
