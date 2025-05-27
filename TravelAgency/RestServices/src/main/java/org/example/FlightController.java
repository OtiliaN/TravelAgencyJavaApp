package org.example;

import org.example.domain.Flight;
import org.example.persistance.interfaces.IFlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("org/flights")
public class FlightController {
    @Autowired
    private IFlightRepository flightRepository;

    @RequestMapping(method = RequestMethod.POST)
    public Flight create(@RequestBody Flight flight){
        flightRepository.save(flight);
        return flight;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Flight update(@PathVariable Long id, @RequestBody Flight flight) {
        System.out.println("Updating flight...");
        flight.setId(id);
        flightRepository.update(flight);
        return flight;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String id){
        System.out.println("Deleting flight ..." + id);
        try{
            flightRepository.delete(Long.parseLong(id));
            return new ResponseEntity<Flight>(HttpStatus.OK);
        }catch (Exception ex){
            System.out.println("Ctrl Delete flight exception");
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Flight> getAll(){
        System.out.println("Get all flights ...");
        return flightRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable String id){
        System.out.println("Get by id " + id);
        var flight = flightRepository.findOne(Long.parseLong(id));
        if (flight.isEmpty()){
            return new ResponseEntity<String>("Flight not found", HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<Flight>(flight.get(), HttpStatus.OK);
        }
    }
}
