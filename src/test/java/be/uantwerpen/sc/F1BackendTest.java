package be.uantwerpen.sc;

import be.uantwerpen.sc.models.sim.messages.F1WayPoint;
import be.uantwerpen.sc.services.vehicleBackends.F1Backend;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@SpringApplicationConfiguration(classes = F1Backend.class)
@WebIntegrationTest
public class F1BackendTest {

    @Autowired
    private F1Backend backend;
    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    private String testData = "{\"wayPoints\":{\"48\":{\"id\":48,\"x\":-5.91,\"y\":-1.03,\"z\":0.52,\"w\":0.85},\"49\":{\"id\":49,\"x\":6.09,\"y\":0.21,\"z\":-0.04,\"w\":1.0},\"46\":{\"id\":46,\"x\":-6.1,\"y\":-28.78,\"z\":0.73,\"w\":0.69},\"47\":{\"id\":47,\"x\":-6.47,\"y\":-21.69,\"z\":0.66,\"w\":0.75}}}";

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        // set fake url
        backend.setBackendURL("http://localhost/");
    }

    @Test
    public void TestGetData() throws Exception{
        mockServer.expect(requestTo("http://localhost/getwaypoints")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(testData, MediaType.APPLICATION_JSON));

        Map<Long, F1WayPoint> result = backend.getWayPoints();
        System.out.println("Parsed data: "+result);

        // test some properties
        assert(result.get(48L).getId() == 48);
        assert(result.get(48L).getX() == -5.91F);
    }

}
