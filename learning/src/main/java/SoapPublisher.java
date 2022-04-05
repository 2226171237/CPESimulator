import service.PersonServiceImpl;

import javax.xml.ws.Endpoint;

/**
 * @author Liyajie
 */
public class SoapPublisher {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:10086/ws/person", new PersonServiceImpl());
    }
}
