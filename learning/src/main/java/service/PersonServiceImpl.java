package service;

import model.Person;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liyajie
 */
@WebService(endpointInterface = "service.PersonService")
public class PersonServiceImpl implements PersonService {

    private static final Map<Integer, Person> PERSONS = new HashMap<>();

    @Override
    public boolean addPerson(Person p) {
        if (PERSONS.get(p.getId()) != null) {
            return false;
        }
        PERSONS.put(p.getId(), p);
        return true;
    }

    @Override
    public boolean deletePerson(int id) {
        if (PERSONS.get(id) == null) {
            return false;
        }
        PERSONS.remove(id);
        return true;
    }

    @Override
    public Person getPerson(int id) {
        return PERSONS.get(id);
    }

    @Override
    public Person[] getAllPersons() {
        return PERSONS.values().toArray(new Person[0]);
    }
}
