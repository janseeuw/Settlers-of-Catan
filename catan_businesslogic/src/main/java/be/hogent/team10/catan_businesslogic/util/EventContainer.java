package be.hogent.team10.catan_businesslogic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Every event, going from a player recieving resouces, to a player cancelling a
 * traderequest is stored in the database. Because of this extensive writing to
 * the database, it is possible that a table would be queried multiple times. As
 * a solution we did this: Data comes in: <br/>
 * event "game_dice" 21 // dice rolled <br/>
 * event "player_resource" 182 // got some resources<br/>
 * event "player_resource" 183 // got some resources<br/>
 * event "player_resource" 184 // got some resources<br/>
 * event "game_resource" 21 // bank lost some resources<br/>
 * event "component_game" 18921 node built <br/>
 * event "component_game" 18951 street built <br/>
 * event "component_game" 18954 street built <br/>
 * event "player_trade" 184 // new<br/>
 * event "player_trade" 184 // cancelled<br/>
 * 
 * these are mapped to a more compact version<br/>
 *
 * event "game_dice" 21 // dice rolled <br/>
 * event "player_resource" [182, 183, 184] // some players got some resources<br/>
 * event "game_resource" 21 // bank lost some resources<br/>
 * event "component_game" [18921, 18951, 18954] some components on board changed. <br/>
 * event "player_trade" [184,184] // something happened with a trade.<br/>
 * 
 * When the tiles have been updated, a full sync is required by default.
 * @author HP
 */
public class EventContainer {

    private List<Event> cleanEvents;
    private int gameStamp = 0;
    private boolean requiresFullSync = false;
    /**
     * constructor
     * @param events these are mapped to a more compact version
     */
    public EventContainer(List<Event> events) {
        this.mapEvents(events);
    }
    
    public EventContainer(){
        
    }

    public List<Event> getEvents() {
        return cleanEvents;
    }

    public List<Event> getAffectedTables() {
        return cleanEvents;
    }
    /**
     * returns latest gamestamp
     */
    public int getGameStamp() {
        return gameStamp;
    }
    /**
     * 
     * @param gameStamp like a versionnumber. Greater values imply more recent versions.
     */
    public void setGameStamp(int gameStamp) {
        this.gameStamp = gameStamp;
    }

    public boolean isRequiresFullSync() {
        return requiresFullSync;
    }
    
    public Map<Integer, List<String>> analyse(List<Event> events){
        Map<Integer, List<String>> filtered = new HashMap<Integer, List<String>>();
        if(events != null)
        for(Event e : events){
            if(!filtered.containsKey(e.getTurn())){
                filtered.put(e.getTurn(), new ArrayList<String>());
            }
            System.out.println("--------------111)--------------");
            System.out.println(e.getTurn());
            System.out.println(e.getEventDescription());
            if(! filtered.get(e.getTurn()).contains(e.getEventDescription())){
                filtered.get(e.getTurn()).add(e.getEventDescription());
            }
        }
        return filtered;
    }

    public void setRequiresFullSync(boolean requiresFullSync) {
        this.requiresFullSync = requiresFullSync;
    }
    
    /**
     * Maps events to a more compact version.
     * @param events 
     */
    private void mapEvents(List<Event> events) {
        if (cleanEvents == null) {
            cleanEvents = new ArrayList<Event>();
        }
        cleanEvents.clear();
        if(events!=null)
        for (Event e : events) {
            if (cleanEvents.contains(e)) {
                int index = cleanEvents.indexOf(e);
                cleanEvents.get(index).addAffectedIds(e.getAffectedIds());
            } else {
                cleanEvents.add(e);
            }
            if (e.getEventNumber() > gameStamp) {
                gameStamp = e.getEventNumber();
            }
            if (e.getEventDescription().equalsIgnoreCase("TILE_GAME")) {
                requiresFullSync = true;
            }
        }
    }
}