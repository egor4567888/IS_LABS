package org.example.repository;

import org.example.model.Person;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Stateless
public class PersonRepository {

    @PersistenceContext(unitName = "PersonsPU")
    private EntityManager em;

    public Person create(Person p) {
        em.persist(p);
        em.flush();
        return p;
    }

    public Person find(Long id) {
        return em.find(Person.class, id);
    }

    public Person update(Person p) {
        return em.merge(p);
    }

    public void delete(Long id) {
        Person p = em.find(Person.class, id);
        if (p != null) {
            em.remove(p);
        }
    }

    public List<Person> findAll(int offset, int limit, String sortBy, boolean asc, String filterColumn, String filterValue) {
        StringBuilder sb = new StringBuilder("SELECT p FROM Person p");
        if (filterColumn != null && filterValue != null) {
            sb.append(" WHERE p.").append(filterColumn).append(" = :filterValue");
        }
        if (sortBy != null) {
            sb.append(" ORDER BY p.").append(sortBy).append(asc ? " ASC" : " DESC");
        }
        TypedQuery<Person> q = em.createQuery(sb.toString(), Person.class);
        if (filterColumn != null && filterValue != null) {
            q.setParameter("filterValue", filterValue);
        }
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(p) FROM Person p", Long.class).getSingleResult();
    }

    public List<Person> findByBirthday(ZonedDateTime birthday) {
        return em.createQuery("SELECT p FROM Person p WHERE p.birthday = :bd", Person.class)
                .setParameter("bd", birthday)
                .getResultList();
    }

    public long countByHairColor(String color) {
        return em.createQuery("SELECT COUNT(p) FROM Person p WHERE p.hairColor = :clr", Long.class)
                .setParameter("clr", Enum.valueOf(org.example.model.Color.class, color))
                .getSingleResult();
    }

    public long countByEyeColor(String color) {
        return em.createQuery("SELECT COUNT(p) FROM Person p WHERE p.eyeColor = :clr", Long.class)
                .setParameter("clr", Enum.valueOf(org.example.model.Color.class, color))
                .getSingleResult();
    }

    public long countByBirthday(ZonedDateTime date) {
        return em.createQuery("SELECT COUNT(p) FROM Person p WHERE p.birthday = :bd", Long.class)
                .setParameter("bd", date)
                .getSingleResult();
    }

    public long countHeightLessThan(float value) {
        return em.createQuery("SELECT COUNT(p) FROM Person p WHERE p.height < :h", Long.class)
                .setParameter("h", value)
                .getSingleResult();
    }
}
