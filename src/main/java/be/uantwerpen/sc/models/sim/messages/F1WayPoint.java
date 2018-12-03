package be.uantwerpen.sc.models.sim.messages;


import java.util.Map;

/**
 * Neemt de velden over van WayPoint uit de SmartRaceCar/common
 */
public class F1WayPoint {
    private float x = 0;
    private float y = 0;
    private float z = 0;
    private float w = 0;
    private long id = 0;

    public F1WayPoint(float x, float y, float z, float w, long id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.id = id;
    }

    public F1WayPoint() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
        id = 0;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
