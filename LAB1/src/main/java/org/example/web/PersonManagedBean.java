package org.example.web;

import org.example.model.Person;
import org.example.service.PersonService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Named("personMB")
@SessionScoped
public class PersonManagedBean implements Serializable {

    @EJB
    private PersonService personService;

    private List<Person> persons;
    private Person selected;
    private Person newPerson;

    private String filterColumn;
    private String filterValue;

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        persons = personService.findAll(0, 100, null, true, null, null);
    }

    public void filter() {
        persons = personService.findAll(0, 100, null, true, filterColumn, filterValue);
    }

    public void addNew() {
        newPerson = new Person();
    }

    public void saveNew() {
        personService.create(newPerson);
        refresh();
    }

    public void edit(Person p) {
        selected = p;
    }

    public void saveEdit() {
        personService.update(selected);
        refresh();
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public void setSelected(Person selected) {
        this.selected = selected;
    }

    public void setNewPerson(Person newPerson) {
        this.newPerson = newPerson;
    }

    public void delete(Person p) {
        personService.delete(p.getId());
        refresh();
    }

    // Статистика
    public long countByHairColor(String color) {
        return personService.countByHairColor(color);
    }

    public long countByEyeColor(String color) {
        return personService.countByEyeColor(color);
    }

    public long countHeightLessThan(float value) {
        return personService.countHeightLessThan(value);
    }

    public long countByBirthday(ZonedDateTime date) {
        return personService.countByBirthday(date);
    }


    public List<Person> getPersons() { return persons; }
    public Person getSelected() { return selected; }
    public Person getNewPerson() { return newPerson; }
    public String getFilterColumn() { return filterColumn; }
    public void setFilterColumn(String f) { this.filterColumn = f; }
    public String getFilterValue() { return filterValue; }
    public void setFilterValue(String v) { this.filterValue = v; }
}
