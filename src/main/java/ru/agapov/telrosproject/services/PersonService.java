package ru.agapov.telrosproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.agapov.telrosproject.models.Person;
import ru.agapov.telrosproject.repositories.PersonRepository;
import ru.agapov.telrosproject.util.PersonNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void save(Person person) {
        personRepository.save(person);
    }

    public Person findById(int id) {
        Optional<Person> foundPerson = personRepository.findById(id);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public void update(Person person, int id) {
        Person personToUpdate = findById(id);

        personToUpdate.setName(person.getName());
        personToUpdate.setSurname(person.getSurname());
        personToUpdate.setPatronymic(person.getPatronymic());
        personToUpdate.setDateOfBirth(person.getDateOfBirth());
        personToUpdate.setEmail(person.getEmail());
        personToUpdate.setTel(person.getTel());

    }

    public void deleteById(int id) {
        personRepository.deleteById(id);
    }
}
