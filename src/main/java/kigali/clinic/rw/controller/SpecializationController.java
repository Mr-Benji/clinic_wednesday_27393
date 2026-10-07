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
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;


@RestController
@RequestMapping (value = {"/api/specialization", "/api/specializations"})
public class SpecializationController {


    @Autowired
    private SpecializationService specializationServ;

    @PostMapping(value="/save")
    public ResponseEntity<?> saveSpecialization(@RequestBody Specialization specialization){

        String returnedMessage = specializationServ.saveSpecialization(specialization);
        return toResponse(returnedMessage);
    }

    @GetMapping(value="/all")
    public ResponseEntity<?> getAllSpecializations(){
        return new ResponseEntity<>(specializationServ.getAllSpecializations(), HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getSpecializationById(@PathVariable UUID id){

        Specialization specialization = specializationServ.getSpecializationById(id);
        if(specialization == null){
            return new ResponseEntity<>("Specialization not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(specialization, HttpStatus.OK);
    }

    @PutMapping(value="/update/{id}")
    public ResponseEntity<?> updateSpecialization(@PathVariable UUID id, @RequestBody Specialization specialization){

        String returnedMessage = specializationServ.updateSpecialization(id, specialization);
        return toResponse(returnedMessage);
    }

    @DeleteMapping(value="/delete/{id}")
    public ResponseEntity<?> deleteSpecialization(@PathVariable UUID id){

        String returnedMessage = specializationServ.deleteSpecialization(id);
        return toResponse(returnedMessage);
    }

    // e.g. /api/specializations/{specializationId}/add-doctor/{doctorId}
    @PostMapping(value="/{specializationId}/add-doctor/{doctorId}")
    public ResponseEntity<?> addDoctor(@PathVariable UUID specializationId, @PathVariable UUID doctorId){

        String returnedMessage = specializationServ.addDoctor(specializationId, doctorId);
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
