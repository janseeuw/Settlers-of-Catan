package be.hogent.team10.catan_businesslogic.model;

import be.hogent.team10.catan_businesslogic.util.Observable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jonasanseeuw
 */
public class Node extends Observable {

    private Coordinate coordinate;
    private BuildingType buildingtype;
    private Player owner;

    public Node(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.buildingtype = BuildingType.NOBUILDING;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        setChanged();
        notifyObservers();
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

    /**
     * @return the buildingtype
     */
    public BuildingType getBuildingtype() {
        return buildingtype;
    }

    /**
     * @param buildingtype the buildingtype to set
     */
    public void setBuildingtype(BuildingType buildingtype) {
        this.buildingtype = buildingtype;
        setChanged();
        notifyObservers();
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
        final Node other = (Node) obj;
        if (this.coordinate == null || !this.coordinate.equals(other.coordinate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Node " + coordinate.toString();
    }

    public Map<String, String> getDetails() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("DESCRIPTION", "Op de hoekpunten tussen tegels kunnen steden gesticht worden. Na het gooien van de dobbelstenen ontvangen spelers die een nederzetting hebben rond een tegel met dat nummer een grondstofkaart.");

        if (this.buildingtype == BuildingType.NOBUILDING) {
            data.put("COMPONENTTYPE", "Bouwplaats");
        }
        if (this.buildingtype == BuildingType.CITY) {
            data.put("COMPONENTTYPE", "Stad");
            data.put("DESCRIPTION", data.get("DESCRIPTION") + " Omdat hier een stad gebouw is krijgt de speler telkens een extra grondstofkaart.");
        }
        if (this.buildingtype == BuildingType.SETTLEMENT) {
            data.put("COMPONENTTYPE", "Nederzetting");
        }
        if (this.owner != null) {
            data.put("OWNER", "Player " + owner.getName() + " [ " + owner.getPlayerColor().name().toLowerCase() + " ]");
        }
        return data;
    }

    public void synchronize(Node updatedNode) {
        if (updatedNode.getOwner() != null) {
            this.setOwner(updatedNode.getOwner());
        }

        if (this.getBuildingtype() != updatedNode.getBuildingtype()) {
            this.setBuildingtype(updatedNode.getBuildingtype());
        }
    }
}
