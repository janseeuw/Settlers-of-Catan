package be.hogent.team10.catan.client.facade;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.TradeRequest;
import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.TileBehaviour;
import be.hogent.team10.catan_businesslogic.util.GameStateDeserializer;
import be.hogent.team10.catan_businesslogic.util.TileBehaviourDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class ServiceFacade implements ServiceFacadeInterface {

    private HttpJsonAdaptor adaptor;

    public ServiceFacade() {
        this.adaptor = new HttpJsonAdaptor();
    }

    @Override
    public void rollDice(int gameId, int playerId) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();

            data.put("gameId", gameId + "");
            data.put("playerId", playerId + "");
            System.out.println(adaptor.post("/game/" + gameId + "/rollDice", data));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildSettlement(int gameId, int playerId, Coordinate coordinate) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("gameId", gameId + "");
            data.put("playerId", playerId + "");
            data.put("X", coordinate.getX() + "");
            data.put("Y", coordinate.getY() + "");
            System.out.println(adaptor.post("/game/" + gameId + "/buildSettlement", data));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildCity(int gameId, int playerId, Coordinate coordinate) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("gameId", gameId + "");
            data.put("playerId", playerId + "");
            data.put("X", coordinate.getX() + "");
            data.put("Y", coordinate.getY() + "");
            System.out.println(adaptor.post("/game/" + gameId + "/buildCity", data));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildStreet(int gameId, int playerId, Coordinate coordinate) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("gameId", gameId + "");
            data.put("playerId", playerId + "");
            data.put("X", coordinate.getX() + "");
            data.put("Y", coordinate.getY() + "");
            System.out.println(adaptor.post("/game/" + gameId + "/buildStreet", data));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveRobber(int gameId, int playerId, Coordinate coordinate) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("gameId", gameId + "");
            data.put("playerId", playerId + "");
            data.put("X", coordinate.getX() + "");
            data.put("Y", coordinate.getY() + "");
            System.out.println(adaptor.post("/game/" + gameId + "/moveRobber", data));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stealResource(int gameId, int robberId, int victimId) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("gameId", gameId + "");
            data.put("robberId", robberId + "");
            data.put("victimId", victimId + "");
            System.out.println(adaptor.post("/game/" + gameId + "/steal", data));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trade(int gameId, TradeRequest tradeRequest) throws Exception {
        try {
            String data = new Gson().toJson(tradeRequest);
            System.out.println(adaptor.post("/game/" + gameId + "/trade", data));
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void replyTrade(int gameId, int playerId, int tradeRequestId, boolean accepted) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("accepted", accepted + "");
            data.put("playerId", playerId + "");
            data.put("tradeRequestId", tradeRequestId+ "");
            data.put("gameId",gameId+ "");
            System.out.println(adaptor.post("/game/" + gameId + "/replyTrade", data));
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Game poll(int gameId, int gameStamp, int PlayerId) {
        try {
            String json = (adaptor.get("/game/" + gameId + "/poll/" + PlayerId + "/" + gameStamp + ""));
            Gson gson = new GsonBuilder().registerTypeAdapter(GameState.class,
                    new GameStateDeserializer()).registerTypeAdapter(TileBehaviour.class, new TileBehaviourDeserializer()).create();
            Game game = gson.fromJson(json, Game.class);
            return game;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void endTurn(int gameId, int my_id) {
        try {
            this.adaptor.get("/game/" + gameId + "/endturn/" + my_id);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void startGame(int gameId, int my_id) {
        try {
            this.adaptor.get("/game/" + gameId + "/start/" + my_id);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void buyDevelopmentCard(int gameId, int playerId) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("gameId", gameId + "");
            data.put("playerId", playerId + "");
            System.out.println(adaptor.post("/game/" + gameId + "/buyDevelopmentCard", data));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playDevelopmentCard(int gameId, int playerId, int type) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("gameId", gameId + "");
            data.put("playerId", playerId + "");
            data.put("type", type + "");
            System.out.println(adaptor.post("/game/" + gameId +"/playDevelopmentCard/" + type, data));
        }catch(MalformedURLException e){
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
