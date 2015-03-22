package be.hogent.team10.catan_businesslogic.model.gameState;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.DevelopmentCard;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.exception.CannotBuildException;
import be.hogent.team10.catan_businesslogic.model.exception.GameStateException;
import be.hogent.team10.catan_businesslogic.util.PlayerTask;
import be.hogent.team10.catan_businesslogic.util.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represent the stage in which players will play the game untill a player reaches 10 points.
 *
 * @author HP
 */
public class PlayGameState extends GameState {

    public PlayGameState(Game gc) {
        super(gc);
        setCurrentState("PLAY_GAMESTATE");
    }

    @Override
    public void setTile(Coordinate c, String s) throws GameStateException {
        throw new GameStateException("You cannot move tiles in this stage of the game.");
    }

    @Override
    public void setFirstNumber(Coordinate c) throws GameStateException {
        throw new GameStateException("You cannot change the tilenumbers in this stage of the game.");
    }

    @Override
    public void buildStreet(Coordinate c, Player player) throws CannotBuildException {
        if (!player.getResources().checkEnoughResources(game.getPrice("STREET"))) {
            throw new CannotBuildException("Je hebt niet genoeg grondstoffen om een straat te bouwen.");
        }
        if(player.getAvailableStreets() <= 0){
            throw new CannotBuildException("Je hebt geen straten meer om te bouwen.");
        }
    }

    @Override
    public void buildSettlement(Coordinate c, Player player) throws CannotBuildException {
        if (!player.getResources().checkEnoughResources(game.getPrice("SETTLEMENT"))) {
            throw new CannotBuildException("Je hebt niet genoeg grondstoffen om een nederzetting te bouwen.");
        }
        if (player.getAvailableSettlements() <= 0) {
            throw new CannotBuildException("Je hebt geen nederzettingen meer om te bouwen.");
        }
    }

    @Override
    public void buildCity(Coordinate c, Player player) throws Exception {
        if (!player.getResources().checkEnoughResources(game.getPrice("CITY"))) {
            throw new CannotBuildException("Je hebt niet genoeg grondstoffen om een stad te bouwen.");
        }
        if (player.getAvailableCities() <= 0) {
            throw new CannotBuildException("Je hebt geen steden meer om te bouwen.");
        }
    }

    @Override
    public void buyDevelopmentCard(Player player) throws Exception {
        if(!player.getResources().checkEnoughResources(game.getPrice("DEVELOPMENTCARD"))){
            throw new GameStateException("Je hebt niet genoeg grondstoffen om een ontwikkelingskaart te kopen");
        }
    }

    @Override
    public void moveRobber(Coordinate coordinate) throws GameStateException {
        if (game.getDice().getValue() != 7) {
            throw new GameStateException("Je kan de rover niet verplaatsen als de som van de ogen geen 7 is.");
        }
    }

    @Override
    public void removeResources(ResourceSet resources) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rollDice() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void diceRolled() throws GameStateException {
        if (game.getDice().getValue() == 7) {
            this.game.setGameState(new RobberState(game));
            game.getGameState().initialize();
        }
    }

    @Override
    public void getRobberLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getPlayerResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getPlayerKnightForce() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void nextPhase() throws GameStateException {
    }

    @Override
    public String toString() {
        return "Speel ronde";
    }

    @Override
    public void streetBuilt(int playerId) {
        
    }

    @Override
    public void updateWhatYouMayDo(Map<Integer, List<String>> events, int currentTurn) {
        if(events.containsKey(currentTurn)){
            for(String s : events.get(currentTurn)){
                System.out.println(s);
            }
        }
        this.whatYouMayDo = new ArrayList<Task>();
        
            if(events == null || events.get(currentTurn) == null || !events.get(currentTurn).contains("DICE_ROLLED")){
                whatYouMayDo.add(new Task(PlayerTask.ROLLDICE, true));
            }
            if(events == null || events.get(currentTurn) == null || !events.get(currentTurn).contains("BOUGHT_CARD")){
                whatYouMayDo.add(new Task(PlayerTask.BUYCARD, false));
            }
            if(events == null || events.get(currentTurn) == null || !events.get(currentTurn).contains("PLAYED_CARD")){
                whatYouMayDo.add(new Task(PlayerTask.PLAYCARD, false));
            }
            whatYouMayDo.add(new Task(PlayerTask.BUILDCITY, false));
            whatYouMayDo.add(new Task(PlayerTask.BUILDHOUSE, false));
            whatYouMayDo.add(new Task(PlayerTask.BUILDSTREET, false));
            whatYouMayDo.add(new Task(PlayerTask.SENDTRADE, false));
            whatYouMayDo.add(new Task(PlayerTask.ENDTURN, true));
    }
        
}