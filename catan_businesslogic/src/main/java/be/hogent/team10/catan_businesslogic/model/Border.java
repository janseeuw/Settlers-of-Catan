package be.hogent.team10.catan_businesslogic.model;
/* mergedate : OK
 * 
 */

import be.hogent.team10.catan_businesslogic.util.Observable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * represent streets.
 *
 * @author jonasanseeuw
 */
public class Border extends Observable {

    private Player owner = null;
    private Coordinate coordinate;

    public Border() {
    }

    /**
     * @param coordinate the coordinate of the border
     */
    public Border(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * @return the coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * @param coordinate the coordinate to set
     */
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.coordinate != null ? this.coordinate.hashCode() : 0);
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
        final Border other = (Border) obj;
        if (this.coordinate != other.coordinate && (this.coordinate == null || !this.coordinate.equals(other.coordinate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "border " + coordinate.toString();//+owner==null?"":owner.getColor().name();
    }

    public Map<String, String> getDetails() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("COMPONENTTYPE", "Straat");

        if (this.owner != null) {           
            data.put("OWNER", "Player " + owner.getName()+ " [ " + owner.getPlayerColor().name().toLowerCase() +" ]");
        }
        data.put("DESCRIPTION", "Straten dienen om een handelsweg te creëren langs dewelke nieuwe nederzettingen kunnen gesticht worden.");

        return data;
    }

    public void synchronize(Border updatedBorder) {
        if (updatedBorder.getOwner() != null) {
            if (!updatedBorder.getOwner().equals(this.getOwner())) {
                this.setOwner(updatedBorder.getOwner());
                setChanged();
                notifyObservers();
            }
        }
    }
}
