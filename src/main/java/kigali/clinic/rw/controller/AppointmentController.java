package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
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

    // e.g. /api/appointments/by-status?status=SCHEDULED
    @GetMapping(value="/by-status")
    public ResponseEntity<?> getAppointmentsByStatus(@RequestParam AppointmentStatus status){
        return new ResponseEntity<>(appointmentServ.getAppointmentsByStatus(status), HttpStatus.OK);
    }

    // e.g. /api/appointments/between?start=2026-10-01&end=2026-10-31
    @GetMapping(value="/between")
    public ResponseEntity<?> getAppointmentsBetween(@RequestParam String start, @RequestParam String end){

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(appointmentServ.getAppointmentsBetween(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping(value="/stats/by-status")
    public ResponseEntity<?> countAppointmentsByStatus(){
        return new ResponseEntity<>(appointmentServ.countAppointmentsByStatus(), HttpStatus.OK);
    }

    // e.g. /api/appointments/cancel-day?doctorId=...&date=2026-10-20
    @PatchMapping(value="/cancel-day")
    public ResponseEntity<?> cancelDoctorDay(@RequestParam UUID doctorId, @RequestParam String date){

        String returnedMessage = appointmentServ.cancelDoctorDay(doctorId, LocalDate.parse(date));
        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // e.g. /api/appointments/page?page=0&size=5&sort=appointmentDate,desc
    @GetMapping(value="/page")
    public ResponseEntity<?> getAppointmentsPage(@RequestParam int page, @RequestParam int size,
            @RequestParam String sort){

        // sort comes as "field,direction"
        String[] sortParts = sort.split(",");
        Sort.Direction direction = Sort.Direction.ASC;
        if(sortParts.length > 1){
            direction = Sort.Direction.fromString(sortParts[1]);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        return new ResponseEntity<>(appointmentServ.getAppointmentsPage(pageable), HttpStatus.OK);
    }

    // e.g. /api/appointments/cancelled-before?date=2026-10-15
    @DeleteMapping(value="/cancelled-before")
    public ResponseEntity<?> deleteCancelledBefore(@RequestParam String date){

        String returnedMessage = appointmentServ.deleteCancelledBefore(LocalDate.parse(date));
        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
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
