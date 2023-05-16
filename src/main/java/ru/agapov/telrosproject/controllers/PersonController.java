package ru.agapov.telrosproject.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.agapov.telrosproject.dto.PersonDTO;
import ru.agapov.telrosproject.models.Person;
import ru.agapov.telrosproject.services.PersonService;
import ru.agapov.telrosproject.util.PersonErrorResponse;
import ru.agapov.telrosproject.util.PersonNotCreatedException;
import ru.agapov.telrosproject.util.PersonNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PersonController {
    private final PersonService personService;
    private final ModelMapper modelMapper;
    @Autowired
    public PersonController(PersonService personService, ModelMapper modelMapper) {
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PersonDTO> findAll() {
        return personService.findAll().stream().map(this::convertToPersonDTO).collect(Collectors.toList());
    }


    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addPerson(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = checkBindingResult(bindingResult);
            throw new PersonNotCreatedException(errorMsg.toString());
        }
        personService.save(convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Person findPerson(@PathVariable("id") int id) {
        return personService.findById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updatePerson(@PathVariable("id") int id,
                               @RequestBody @Valid PersonDTO personDTO,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = checkBindingResult(bindingResult);
            throw new PersonNotCreatedException(errorMsg.toString());
        }
        if (!personService.findAll().stream().anyMatch(p->p.getId() == id))
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        else {
            personService.update(convertToPerson(personDTO), id);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePerson(@PathVariable("id") int id) {
        if (!personService.findAll().stream().anyMatch(p->p.getId() == id))
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        else {
            personService.deleteById(id);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Person with this id wasn't found",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Person wasn't created;" + e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }

    private StringBuilder checkBindingResult(BindingResult bindingResult) {
        StringBuilder errorMsg = new StringBuilder();

        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            errorMsg.append(error.getField())
                    .append(" - ").append(error.getDefaultMessage())
                    .append(";");
        }

        return errorMsg;

    }
}
