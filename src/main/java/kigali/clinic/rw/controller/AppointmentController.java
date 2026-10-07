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

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.service.AppointmentService;


@RestController
@RequestMapping (value = {"/api/appointment", "/api/appointments"})
public class AppointmentController {


    @Autowired
    private AppointmentService appointmentServ;

    @PostMapping(value="/save")
    public ResponseEntity<?> saveAppointment(@RequestBody Appointment appointment,
            @RequestParam UUID patientId, @RequestParam UUID doctorId){

        String returnedMessage = appointmentServ.saveAppointment(appointment, patientId, doctorId);
        return toResponse(returnedMessage);
    }

    @GetMapping(value="/all")
    public ResponseEntity<?> getAllAppointments(){
        return new ResponseEntity<>(appointmentServ.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id){

        Appointment appointment = appointmentServ.getAppointmentById(id);
        if(appointment == null){
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @PutMapping(value="/update/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment,
            @RequestParam(required = false) UUID patientId, @RequestParam(required = false) UUID doctorId){

        String returnedMessage = appointmentServ.updateAppointment(id, appointment, patientId, doctorId);
        return toResponse(returnedMessage);
    }

    @DeleteMapping(value="/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id){

        String returnedMessage = appointmentServ.deleteAppointment(id);
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
