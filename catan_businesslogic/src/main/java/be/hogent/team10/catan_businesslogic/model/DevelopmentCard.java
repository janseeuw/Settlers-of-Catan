package be.hogent.team10.catan_businesslogic.model;

import java.util.Observable;

public class DevelopmentCard extends Observable {

    private Player owner;
    private DevelopmentCardType developmentCardType;
    private boolean played;
    private int id;

    public DevelopmentCard() {
    }

    public DevelopmentCard(DevelopmentCardType developmentCardType) {
        this.developmentCardType = developmentCardType;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player p) {
        owner = p;
        setChanged();
        notifyObservers();
    }

    /**
     * @return the buildingtype
     */
    public DevelopmentCardType getDevelopmentCardType() {
        return developmentCardType;
    }

    /**
     * @param buildingtype the buildingtype to set
     */
    public void setDevelopmentCardType(DevelopmentCardType developmentCardType) {
        this.developmentCardType = developmentCardType;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return "Ontwikkelingskaart " + this.getDevelopmentCardType() + " is in het bezit van " + this.getOwner().getName();
    }

    public int getId() {
        return id;
    }

    public boolean getPlayed() {
        return played;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayed(boolean played) {
        this.played = played;
        setChanged();
        notifyObservers();
    }

    public void synchronize(DevelopmentCard updatedDevelopmentCard) {
        if(updatedDevelopmentCard.getId() != 0){
            this.setId(updatedDevelopmentCard.getId());
        }
        
        if (updatedDevelopmentCard.getOwner() != null) {
            this.setOwner(updatedDevelopmentCard.getOwner());
        }

        if (this.getDevelopmentCardType() != updatedDevelopmentCard.getDevelopmentCardType()) {
            this.setDevelopmentCardType(updatedDevelopmentCard.getDevelopmentCardType());
        }

        if (this.getPlayed() != updatedDevelopmentCard.getPlayed()) {
            this.setPlayed(updatedDevelopmentCard.getPlayed());
        }
    }
}
