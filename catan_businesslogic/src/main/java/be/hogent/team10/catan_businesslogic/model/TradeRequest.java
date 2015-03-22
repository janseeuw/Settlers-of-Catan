package be.hogent.team10.catan_businesslogic.model;
/* mergedate : OK
 * 
 */
import java.util.Observable;

/**
 *
 * @author HP
 */

public class TradeRequest extends Observable{

    private int tradeRequestId;
    private int gameId;
    private int playerSending;
    private int playerReceiving;
    private ResourceSet offer;
    private ResourceSet reward;
    private long timeTillAccepted;
    private TradeRequestStatus status;

    public TradeRequest(int gameId, int playerSending,int playerReceiving, ResourceSet offer, ResourceSet reward) {
        this.gameId = gameId;
        this.playerSending = playerSending;
        this.playerReceiving = playerReceiving;
        this.offer = offer;
        this.reward = reward;
    }

    public TradeRequest() {
    }

    
    
    public int getTradeRequestId() {
        return tradeRequestId;
    }

    public void setTradeRequestId(int tradeRequestId) {
        this.tradeRequestId = tradeRequestId;
    }

    public int getPlayerSending() {
        return playerSending;
    }

    public void setPlayerSending(int playerSending) {
        this.playerSending = playerSending;
    }

    public int getPlayerReceiving() {
        return playerReceiving;
    }

    public void setPlayerReceiving(int playerReceiving) {
        this.playerReceiving = playerReceiving;
    }

    public ResourceSet getOffer() {
        return offer;
    }

    public void setOffer(ResourceSet offer) {
        this.offer = offer;
    }

    public ResourceSet getReward() {
        return reward;
    }

    public void setReward(ResourceSet reward) {
        this.reward = reward;
    }

    public long getTimeTillAccepted() {
        return timeTillAccepted;
    }

    public void setTimeTillAccepted(long timeTillAccepted) {
        this.timeTillAccepted = timeTillAccepted;
    }

    public void setStatus(TradeRequestStatus status) {
        this.status = status;
    }
    
    public TradeRequestStatus getStatus() {
        return status;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    
}
