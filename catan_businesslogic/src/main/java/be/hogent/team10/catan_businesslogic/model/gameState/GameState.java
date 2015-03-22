package be.hogent.team10.catan_businesslogic.model.gameState;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.exception.GameStateException;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.PlayerTask;
import be.hogent.team10.catan_businesslogic.util.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HP The idea of this state pattern is to represent the states of the
 * game. Gamestate class declares an interface for all classes that represent
 * the operational states. Subclasses implement behaviour.
 *
 * Why use state pattern? The game's behaviour depends on its state, and changes
 * must be made at runtime
 *
 * Multiple operations would have conditional statements, depending on the
 * game's state.
 *
 * The state pattern puts all state-specific behaviour into one class. This is
 * to prevent using a whole lot of case or other conditional statements, making
 * the code more structured and better understandable.
 *
 * Moreover, no states can be skipped because each state has a succeeding state
 * specified.
 *
 *
 */
public abstract class GameState extends Observable {

    protected Game game;
    protected GameState nextState;
    protected List<Task> whatYouMayDo;
    private String currentState;
    /*
     * board initialization methods setter methods
     */

    public GameState(Game g) {
        this.game = g;
    }

    /**
     * we provide this method because when json tries to serialize this object,
     * it also serializes its attributes. Ofcourse game has a gamestate, which
     * on his turn has a game. result : outOfMemoryException aka infinite loop.
     * Therefore we don't set the game in the gamestate upon creation in the
     * datalayer, but we set it when we need it or plan to use it in the near
     * future.
     */
    public void setGameInGameState(Game g) {
        this.game = g;
        initialize();
    }

    public abstract void setTile(Coordinate c, String s) throws GameStateException;

    public abstract void setFirstNumber(Coordinate c) throws GameStateException;

    /*
     * gameplay and game initialization methods. setter methods
     */
    public abstract void buildStreet(Coordinate c, Player player) throws Exception;

    public abstract void buildSettlement(Coordinate c, Player player) throws Exception;

    public abstract void buildCity(Coordinate c, Player player) throws Exception;

    public abstract void buyDevelopmentCard(Player player) throws Exception;

    public abstract void moveRobber(Coordinate c) throws GameStateException;

    public boolean validate() {
        return false;
    }
    
    public abstract void updateWhatYouMayDo(Map<Integer, List<String>> events, int currentTurn);
    
    /*
     * getter methods
     */
    public abstract void removeResources(ResourceSet resources);

    public abstract void rollDice() throws GameStateException;

    public abstract void diceRolled() throws GameStateException;

    public abstract void getRobberLocation();

    public abstract void getPlayerResources();

    public abstract void getPlayerKnightForce();

    // Set default prices off all the board elements.
    protected void initialize() {
        game.setPrice("SETTLEMENT", new ResourceSet(1, 1, 1, 1, 0));
        game.setPrice("CITY", new ResourceSet(0, 0, 0, 2, 3));
        game.setPrice("STREET", new ResourceSet(1, 1, 0, 0, 0));
        game.setPrice("DEVELOPMENTCARD", new ResourceSet(0, 0, 1, 1, 1));
    }

    public abstract void nextPhase() throws GameStateException;

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.currentState != null ? this.currentState.hashCode() : 0);
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
        final GameState other = (GameState) obj;
        if ((this.currentState == null) ? (other.currentState != null) : !this.currentState.equals(other.currentState)) {
            return false;
        }
        return true;
    }

    public void triggerUpdates(GameState newGamestate) {
        setChanged();
        notifyObservers(newGamestate);
    }

    public void synchronize(GameState gameState) {
        if (!gameState.getCurrentState().equalsIgnoreCase(currentState)) {
            game.setGameState(gameState);
            game.setGameInGameState();
            // copy observers to the newgamestate;
            gameState.copyObservers(this);
        }
        this.whatYouMayDo = gameState.whatYouMayDo;
    }

    void nextPlayer() {
        
    }

    public abstract void streetBuilt(int playerId);

    public void setInactive() {
        whatYouMayDo = new ArrayList<Task>();
        whatYouMayDo.add(new Task(PlayerTask.WAIT, false));
        whatYouMayDo.add(new Task(PlayerTask.ACCEPTTRADE, false));
    }
}