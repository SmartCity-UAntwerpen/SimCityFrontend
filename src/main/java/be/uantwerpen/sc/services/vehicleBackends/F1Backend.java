package be.uantwerpen.sc.services.vehicleBackends;

import be.uantwerpen.sc.models.sim.messages.F1WayPoint;
import be.uantwerpen.sc.models.sim.messages.F1WayPointMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class F1Backend {

    private String backendURL;
    private RestTemplate restTemplate;
    private ObjectMapper mapper = new ObjectMapper();

    public F1Backend(@Value("${backend.f1.url") String backendURL) {
        this.backendURL = backendURL;
        restTemplate = new RestTemplate();
    }


    public Map<Long,F1WayPoint> getWayPoints() throws IOException {
        String endpoint = backendURL + "getwaypoints";

        F1WayPointMap data = restTemplate.getForObject(endpoint,F1WayPointMap.class);

        return null;
    }

}
