package be.uantwerpen.sc;

import be.uantwerpen.sc.services.vehicleBackends.F1Backend;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class F1BackendTest {

    private F1Backend backend;

    public F1BackendTest() {
        backend = new F1Backend("http://smartcity.ddns.net:8081/carmanager/");
    }

    @Test
    public void TestGetData() throws Exception{
        //backend.getWayPoints();
        backend.parseJSON("{\"wayPoints\":{\"48\":{\"id\":48,\"x\":-5.91,\"y\":-1.03,\"z\":0.52,\"w\":0.85},\"49\":{\"id\":49,\"x\":6.09,\"y\":0.21,\"z\":-0.04,\"w\":1.0},\"46\":{\"id\":46,\"x\":-6.1,\"y\":-28.78,\"z\":0.73,\"w\":0.69},\"47\":{\"id\":47,\"x\":-6.47,\"y\":-21.69,\"z\":0.66,\"w\":0.75}}}");
    }
}
