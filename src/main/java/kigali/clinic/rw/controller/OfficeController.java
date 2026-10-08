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

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;


@RestController
@RequestMapping (value = {"/api/office", "/api/offices"})
public class OfficeController {


    @Autowired
    private OfficeService officeServ;

    @PostMapping(value="/save")
    public ResponseEntity<?> saveOffice(@RequestBody Office office){

       String returnedMessage =  officeServ.saveOffice(office);
       return toResponse(returnedMessage);
    }

    @GetMapping(value="/all")
    public ResponseEntity<?> getAllOffices(){
        return new ResponseEntity<>(officeServ.getAllOffices(), HttpStatus.OK);
    }

    // e.g. /api/office/search?name=card
    @GetMapping(value="/search")
    public ResponseEntity<?> searchOfficesByName(@RequestParam String name){
        return new ResponseEntity<>(officeServ.searchOfficesByName(name), HttpStatus.OK);
    }

    @GetMapping(value="/busiest")
    public ResponseEntity<?> getBusiestOffice(){

        Object[] busiest = officeServ.getBusiestOffice().orElse(null);
        if(busiest == null){
            return new ResponseEntity<>("No appointments yet", HttpStatus.OK);
        }
        return new ResponseEntity<>(busiest, HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id){

        Office office = officeServ.getOfficeById(id).orElse(null);
        if(office == null){
            return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(office, HttpStatus.OK);
    }

    @PutMapping(value="/update/{id}")
    public ResponseEntity<?> updateOffice(@PathVariable UUID id, @RequestBody Office office){

        String returnedMessage = officeServ.updateOffice(id, office);
        return toResponse(returnedMessage);
    }

    @DeleteMapping(value="/delete/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable UUID id){

        String returnedMessage = officeServ.deleteOffice(id);
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
