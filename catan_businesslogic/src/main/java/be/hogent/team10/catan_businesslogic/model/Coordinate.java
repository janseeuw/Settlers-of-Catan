package be.hogent.team10.catan_businesslogic.model;
/*
 * mergedate : OK
 *
 */

/**
 *
 * @author jonasanseeuw
 */
public class Coordinate {

    private int x;
    private int y;

    /**
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        if (x < 0 && x > 15)
            throw new IllegalArgumentException("Ongeldige coordinaatwaarde");
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        if (y < 0 && y > 15) 
            throw new IllegalArgumentException("Ongeldige coordinaatwaarde");
        this.y = y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.x;
        hash = 53 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coordinate other = (Coordinate) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";

    }
}
