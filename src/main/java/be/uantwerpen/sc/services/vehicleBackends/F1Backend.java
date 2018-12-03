package be.uantwerpen.sc.services.vehicleBackends;

import be.uantwerpen.sc.models.sim.messages.F1WayPoint;
import be.uantwerpen.sc.models.sim.messages.F1WayPointMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class F1Backend {

    @Value("${backend.f1.url}")
    private String backendURL;

    @Autowired
    private RestTemplate restTemplate;
    private ObjectMapper mapper = new ObjectMapper();

    public Map<Long,F1WayPoint> getWayPoints() {
        String endpoint = backendURL + "getwaypoints";

        F1WayPointMap data = restTemplate.getForObject(endpoint,F1WayPointMap.class);

        return data.getWayPoints();
    }

    public String getBackendURL() {
        return backendURL;
    }

    public void setBackendURL(String backendURL) {
        this.backendURL = backendURL;
    }

    // define REST bean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
