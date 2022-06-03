package collectionData;

import java.io.Serializable;

/**
 * Class for location (int x, Double y, String name).
 */
public class Location implements Serializable {
    private int x;
    private Double y; //Поле не может быть null
    private String name; //Поле не может быть null

    public Location(){}

    public Location(int x, Double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "x = " + x + "; y = " + y.toString() + "; name: " + name;
    }

    /**
     * Checks whether the location meets the requirements
     * @return The result
     */
    public boolean check() {
        return y != null && name != null;
    }
}
