/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.facade;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.TradeRequest;
import be.hogent.team10.catan_businesslogic.model.exception.GameStateException;
import be.hogent.team10.catan_businesslogic.model.gameState.SetupState;
import be.hogent.team10.catan_businesslogic.util.PlayerColor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class DummyServiceFacade implements ServiceFacadeInterface{
    
    private Game g;
    
    public DummyServiceFacade() throws Exception{
        g = new Game();
        List<Player> players= new ArrayList<Player>();
        g.initialize();
        players.add(new Player("OfflinePlayer1", 1, "noreply", "nopassword"));
        players.get(0).setPlayerColor(PlayerColor.BLUE);
        players.add(new Player("OfflinePlayer2", 2, "noreply", "nopassword"));
        players.get(1).setPlayerColor(PlayerColor.GREEN);
        players.add(new Player("OfflinePlayer3", 3, "noreply", "nopassword"));
        players.get(2).setPlayerColor(PlayerColor.RED);
        g.setPlayers(players);
    }
    
    @Override
    public void rollDice(int gameId, int playerId) throws Exception {
        try {
            g.rollDice();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildSettlement(int gameId, int playerId, Coordinate coordinate) throws Exception {
        try {
            g.buildSettlement(coordinate, g.getPlayer(playerId));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildCity(int gameId, int playerId, Coordinate coordinate) throws Exception {
        try {
            g.buildCity(coordinate, g.getPlayer(playerId));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildStreet(int gameId, int playerId, Coordinate coordinate) throws Exception {
        try {
            g.buildStreet(coordinate, g.getPlayer(playerId));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveRobber(int gameId, int playerId, Coordinate coordinate) throws Exception {
        try {
            if(g.getCurrentPlayer() == playerId)
                g.moveRobber(coordinate,g.getDice().getValue());
            else throw new GameStateException("you cannot move the robber");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void stealResource(int gameId, int robberId, int victimID) throws Exception {
     throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void trade(int gameId, TradeRequest tradeRequest) throws Exception{
        throw new UnsupportedOperationException("can't trade in singleplayer yet");
    }
    
    @Override
    public Game poll(int gameId, int gameStamp, int PlayerId){
        try {
            return g;
        } catch( Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void endTurn(int gameId, int my_id) {
        try {
            g.endTurn(my_id);
        }catch(Exception e){
            
        }
    }

    @Override
    public void startGame(int gameId, int my_id) {
        try {
            g.generateTiles();
            g.setCurrentPlayer(1);
            g.setGameState(new SetupState(g));
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DummyServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buyDevelopmentCard(int gameId, int playerId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void playDevelopmentCard(int gameId, int playerId, int type) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void replyTrade(int gameId, int tradeRequestId, int playerId, boolean accepted) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void divideResources(int gameId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
