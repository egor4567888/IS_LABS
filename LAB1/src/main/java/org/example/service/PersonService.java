package org.example.service;

import org.example.model.Person;
import org.example.repository.PersonRepository;
import org.example.ws.Broadcaster;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import javax.persistence.PersistenceException;

@Stateless
public class PersonService {

    @Inject
    private PersonRepository repo;

    @Inject
    private Broadcaster broadcaster;

    @Inject
    private Validator validator;

    public Person create(Person p) {
        validateEntity(p);
        Person created = repo.create(p);
        broadcaster.broadcastCreate(created);
        return created;
    }

    public Person find(Long id) {
        return repo.find(id);
    }

    public List<Person> findAll(int offset, int limit, String sortBy, boolean asc, String filterColumn, String filterValue) {
        return repo.findAll(offset, limit, sortBy, asc, filterColumn, filterValue);
    }

    public long count() {
        return repo.count();
    }

    public Person update(Person p) {
        validateEntity(p);
        Person updated = repo.update(p);
        broadcaster.broadcastUpdate(updated);
        return updated;
    }

    public void delete(Long id) {
        try {
            repo.delete(id);
            broadcaster.broadcastDelete(id);
        } catch (PersistenceException ex) {
            throw new IllegalStateException("Нельзя удалить объект — есть связанные сущности.", ex);
        }
    }

    public long countByBirthday(ZonedDateTime birthday) {
        return repo.countByBirthday(birthday);
    }

    public long deleteByBirthday(ZonedDateTime birthday) {
        List<Person> list = repo.findByBirthday(birthday);
        for (Person p : list) {
            delete(p.getId());
        }
        return list.size();
    }

    public long countHeightLessThan(float height) {
        return repo.countHeightLessThan(height);
    }

    public long countByHairColor(String color) {
        return repo.countByHairColor(color);
    }

    public long countByEyeColor(String color) {
        return repo.countByEyeColor(color);
    }

    private void validateEntity(Person p) {
        Set<ConstraintViolation<Person>> violations = validator.validate(p);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<?> v : violations) {
                sb.append(v.getPropertyPath()).append(" ").append(v.getMessage()).append("; ");
            }
            throw new ConstraintViolationException("Validation failed: " + sb.toString(), violations);
        }
    }
}
