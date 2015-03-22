/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.facade;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.TradeRequest;

/**
 *
 * @author HP
 */
public interface ServiceFacadeInterface {

    void buildCity(int gameId, int playerId, Coordinate coordinate) throws Exception;

    void buildSettlement(int gameId, int playerId, Coordinate coordinate) throws Exception;

    void buildStreet(int gameId, int playerId, Coordinate coordinate) throws Exception;
    
    void buyDevelopmentCard(int gameId, int playerId) throws Exception;

    void playDevelopmentCard(int gameId, int playerId, int type) throws Exception;
    
    void endTurn(int gameId, int my_id);

    void moveRobber(int gameId, int playerId, Coordinate coordinate) throws Exception;

    void stealResource(int gameId, int robberId, int victimID) throws Exception;
        
    Game poll(int gameId, int gameStamp, int PlayerId);

    void rollDice(int gameId, int playerId) throws Exception;

    void startGame(int gameId, int my_id);

    void trade(int gameId, TradeRequest tradeRequest) throws Exception;
    
    void replyTrade(int gameId, int tradeRequestId, int playerId, boolean accepted) throws Exception;
    
}
