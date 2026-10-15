package kigali.clinic.rw.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.AppointmentService;
import kigali.clinic.rw.service.PrescriptionService;


@RestController
@RequestMapping (value = {"/api/prescription", "/api/prescriptions"})
public class PrescriptionController {


    @Autowired
    private PrescriptionService prescriptionServ;

    @Autowired
    private AppointmentService appointmentServ;

    // e.g. /api/prescriptions/save?appointmentId=...
    @PostMapping(value="/save")
    public ResponseEntity<?> savePrescription(@RequestBody Prescription prescription,
            @RequestParam UUID appointmentId){

        String returnedMessage = prescriptionServ.savePrescription(prescription, appointmentId);
        if(returnedMessage.equals("Prescription saved successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        }
        return toResponse(returnedMessage);
    }

    @GetMapping(value="/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionsOfAppointment(@PathVariable UUID appointmentId){

        Appointment appointment = appointmentServ.getAppointmentById(appointmentId);
        if(appointment == null){
            return new ResponseEntity<>("The appointment with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(prescriptionServ.getPrescriptionsOfAppointment(appointment), HttpStatus.OK);
    }

    private ResponseEntity<?> toResponse(String message){

        if(message.endsWith("successfully")){
            return new ResponseEntity<>(message, HttpStatus.OK);
        }else if(message.endsWith("does not exist")){
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }
}
