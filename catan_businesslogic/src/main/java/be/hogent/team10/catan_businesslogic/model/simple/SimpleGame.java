/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_businesslogic.model.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author HP
 */
public class SimpleGame {
    private List<String> players;
    private Calendar timeCreated;
    private String status;
    private int gameId;
    private int max;
    
    public SimpleGame(){
        
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
    
    public void setPlayers(String playerCsv){
        if(playerCsv != null)
            this.players = new ArrayList<String>(Arrays.asList(playerCsv.split(";")));
    }

    public Calendar getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Calendar timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
    
}
