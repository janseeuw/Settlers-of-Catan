package be.hogent.team10.catan_businesslogic.util;

import java.util.List;

/**
 * Represents an event that was stored in the database, or a more compact version.
 * @see EventContainer
 * 
 * @param eventDescription component, player or data that has changed.
 * @param affectedIds again : @see Eventcontainer
 * List because of compact version. 
 * @author HP
 */
public class Event {

    private int gameId;
    private int eventNumber;
    private String eventDescription;
    private List<Integer> affectedIds;
    private int turn;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getEventNumber() {
        return eventNumber;
    }

    public void setEventNumber(int eventNumber) {
        this.eventNumber = eventNumber;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public List<Integer> getAffectedIds() {
        return affectedIds;
    }

    public void setAffectedIds(List<Integer> affectedIds) {
        this.affectedIds = affectedIds;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.gameId;
        hash = 53 * hash + (this.eventDescription != null ? this.eventDescription.hashCode() : 0);
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
        final Event other = (Event) obj;
        if (this.gameId != other.gameId) {
            return false;
        }
        if ((this.eventDescription == null) ? (other.eventDescription != null) : !this.eventDescription.equals(other.eventDescription)) {
            return false;
        }
        return true;
    }

    public void addAffectedIds(List<Integer> affectedIds) {
        if (affectedIds != null) {
            for (int id : affectedIds) {
                this.affectedIds.add(id);
            }
        }
    }

    public int getTurn() {
        return turn;
    }
    public void setTurn(int turn) {
        this.turn =  turn;
    }
}