package be.uantwerpen.sc.models.sim;

/**
 * Created by Gebruiker on 11/05/2017.
 */
// Class for front-end fill in form
public class SimForm {
    private String name;
    private int startPoint;
    private String type;
    private long speed;
    private String property;
    private String value;

    public SimForm() {
        name = null;
        startPoint = 0;
        type = null;
        speed = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
