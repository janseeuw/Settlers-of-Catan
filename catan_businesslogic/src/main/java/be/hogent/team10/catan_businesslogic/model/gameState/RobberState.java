package be.hogent.team10.catan_businesslogic.model.gameState;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.exception.GameStateException;
import be.hogent.team10.catan_businesslogic.util.PlayerTask;
import be.hogent.team10.catan_businesslogic.util.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HP
 */
public class RobberState extends GameState {

    private Coordinate oldLocation;

    public RobberState(Game game) {
        super(game);
        setCurrentState("ROBBER_STATE");
    }

    @Override
    public void setTile(Coordinate c, String s) throws GameStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFirstNumber(Coordinate c) throws GameStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buildStreet(Coordinate c, Player player) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buildSettlement(Coordinate c, Player player) throws Exception {
        throw new GameStateException("Je kan nu geen nederzetting bouwen. Verplaats eerst de rover.");
    }

    @Override
    public void buildCity(Coordinate c, Player player) throws Exception {
        throw new GameStateException("Je kan nu geen stad bouwen. Verplaats eerst de rover.");
    }

    @Override
    public void buyDevelopmentCard(Player player) throws Exception {
        throw new GameStateException("Er kan nu geen ontwikkelingskaart gekocht worden");
        /*
        if (!player.getResources().checkEnoughResources(game.getPrice("DEVELOPMENTCARD"))) {
            throw new GameStateException("Je hebt niet genoeg grondstoffen om een ontwikkelingskaart te kopen");
        }*/
    }

    @Override
    public void moveRobber(Coordinate coordinate) throws GameStateException {
        this.oldLocation = game.getBoard().getRobber();
    }

    @Override
    public void removeResources(ResourceSet resources) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rollDice() throws GameStateException {
        throw new GameStateException("Je moet eerst de rover verplaatsen alvorens je opnieuw kan gooien");
    }

    @Override
    public void diceRolled() throws GameStateException {
        throw new GameStateException("This cannot be done.");
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
        if (!game.getBoard().getRobber().equals(oldLocation)) {
            game.setGameState(new PlayGameState(game));
        } else {
            throw new GameStateException("De rover is niet verplaatst");
        }
    }

    @Override
    public String toString() {
        return "Struikrover ronde";
    }

    @Override
    public void streetBuilt(int playerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateWhatYouMayDo(Map<Integer, List<String>> events, int currentTurn) {
        this.whatYouMayDo = new ArrayList<Task>();
        if(!events.containsKey(currentTurn) || !events.get(currentTurn).contains("ROBBER_MOVED")){
            whatYouMayDo.add(new Task(PlayerTask.MOVEROBBER, true));
        }
        whatYouMayDo.add(new Task(PlayerTask.STEAL, true));
        whatYouMayDo.add(new Task(PlayerTask.ENDTURN, true));
    }
}
