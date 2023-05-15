package ru.agapov.telrosproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.agapov.telrosproject.models.Person;

public interface PersonRepository extends JpaRepository<Person,Integer> {
}
