package be.hogent.team10.catan_businesslogic.model;
/* mergedate : OK
 * 
 */

import be.hogent.team10.catan_businesslogic.model.tileBehaviour.ResourceTileBehaviour;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.DesertTileBehaviour;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.SeaTileBehaviour;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.TileBehaviour;
import be.hogent.team10.catan_businesslogic.util.Observable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jonasanseeuw
 */
public class Tile extends Observable {

    private Coordinate coordinate;
    private int number;
    private boolean robber;
    private TileBehaviour behaviour;
    private Resource resource;

    public Tile() {
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    public Resource getResource() {
        return resource;
    }

    /**
     * @return the behaviour
     */
    public TileBehaviour getBehaviour() {
        return behaviour;
    }

    /**
     * @param behaviour the behaviour to set
     */
    public void setBehaviour(TileBehaviour behaviour) throws Exception {
        if (this.getTileType() == TileType.SEA) {
            throw new Exception("You cannot override a seatile");
        }
        this.number = 0;
        this.robber = false;
        this.resource = null;
        this.behaviour = behaviour;
        setChanged();
        notifyObservers();
    }

    public void setResource(Resource resource) throws Exception {
        getBehaviour().validateSetResource();
        this.resource = resource;
        setChanged();
        notifyObservers();
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) throws Exception {
        getBehaviour().validateSetNumber(number);
        if (number >= 2 && number <= 12 && number != 7) {
            this.number = number;
        } else if (!(behaviour instanceof ResourceTileBehaviour)) {
            this.number = number;
        } else {
            throw new IllegalArgumentException("Invalid number");
        }
        setChanged();
        notifyObservers();
    }

    /*
     * Removes the number of a Tile
     */
    public void removeNumber() {
        this.number = 0;
        setChanged();
        notifyObservers();
    }

    /**
     * @return the robber
     */
    public boolean getHasRobber() {
        return robber;
    }

    /**
     * @param robber the robber to set
     */
    public void setHasRobber(boolean robber) throws Exception {
        getBehaviour().validateSetRobber();
        this.robber = robber;
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

    public Tile(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public TileType getTileType() {
        if (this.behaviour != null) {
            return this.getBehaviour().getTileType();
        } else {
            return null;
        }
    }

    public void setTileType(TileType tileType) throws Exception {
        switch (tileType) {
            case DESERT:
                this.setBehaviour(new DesertTileBehaviour());
                setChanged();
                notifyObservers();
                break;
            case SEA:
                this.setBehaviour(new SeaTileBehaviour());
                break;
            case RESOURCE:
                this.setBehaviour(new ResourceTileBehaviour());
                break;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Removes the tile's type, resource and number
     */
    public void clear() {
        this.number = 0;
        this.robber = false;
        this.resource = null;
        this.behaviour = null;
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tile other = (Tile) obj;
        if (this.coordinate != other.coordinate && (this.coordinate == null || !this.coordinate.equals(other.coordinate))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.coordinate != null ? this.coordinate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "tile " + coordinate.toString();
    }

    public Map<String, String> getDetails() {
        Map<String, String> data = new HashMap<String, String>();
        if (this.getTileType() == null) {
            return null;
        }
        if (this.getTileType() == TileType.DESERT) {
            data.put("COMPONENTTYPE", "Woestijn");
            data.put("DESCRIPTION", "Woestijntegels zijn onvruchtbare gebieden die geen tegelnummer hebben. Spelers met een nederzetting rond deze tegel zullen nooit grondstoffen krijgen van deze tegel.");
        }
        if (this.getTileType() == TileType.SEA) {
            data.put("COMPONENTTYPE", "Zee");
            data.put("DESCRIPTION", "Zeetegels omringen het spelbord. Op tussen zeetegels onderling kunnen geen straten gebouwd worden. Indien geen van de hoekpunten toebehoort aan een landtegel zal ook daar geen nederzetting gesticht kunnen worden");
        }
        if (this.getTileType() == TileType.RESOURCE) {
            data.put("COMPONENTTYPE", resource.name().toLowerCase());
            data.put("DESCRIPTION", "Een nederzetting stichten op een hoekpunt van een tegel zorgt ervoor dat je grondstoffen van dit type kan krijgen. Indien het nummer van deze tegel gegooid word, krijg je 1 of zelfs 2 grondstofkaart(en) van het type \"" + resource.name().toLowerCase() + "\".");
            if (this.getHasRobber()) {
                data.put("ROBBER", "Struikrovers teisteren deze tegelI Als gevolg daarvan krijgt geen van de spelers rond deze tegel grondstoffen voor deze tegel zolang de rover hier staat");
            }
        }
        return data;
    }

    public void synchronize(Tile updatedTile) throws Exception {
        if ( this.getTileType() != (updatedTile.getTileType())) {
            this.setTileType(updatedTile.getTileType());
            if (updatedTile.getTileType() == TileType.RESOURCE) {
                this.setResource(updatedTile.getResource());
            }
        }
        
        if (this.number != updatedTile.getNumber()) {
            this.setNumber(updatedTile.getNumber());
        }
    }
}