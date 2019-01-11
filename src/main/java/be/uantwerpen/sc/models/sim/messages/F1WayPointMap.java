package be.uantwerpen.sc.models.sim.messages;

import java.util.HashMap;

public class F1WayPointMap {

    private HashMap<Long,F1WayPoint> wayPoints;

    public F1WayPointMap() {
        wayPoints = new HashMap<>();
    }

    public HashMap<Long, F1WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(HashMap<Long, F1WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }
}
