import model.Person;
import service.PersonService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Liyajie
 */
public class SoapPublisherClient {

    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("http://localhost:10086/ws/person?wsdl");
        QName qName = new QName("http://service/", "PersonServiceImplService");
        Service service = Service.create(url, qName);
        PersonService personService = service.getPort(PersonService.class);
        System.out.println("Add Person: " + personService.addPerson(new Person(0, "Test1", 20)));
        System.out.println("Add Person: " + personService.addPerson(new Person(1, "Test2", 20)));
        System.out.println(personService.getPerson(0));
    }
}
