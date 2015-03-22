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
 * Represents the first state of the game. In this state, the tiles are put in
 * place. The game won't go to the next phase until all the tiles are placed
 * correctly
 *
 * @author HP
 */
public class InitializationState extends GameState {

    public InitializationState(Game gc) {
        super(gc);
        this.nextState = new SetupState(gc);
        setCurrentState("INITIALIZATION_STATE");
    }

    public void setTile(Coordinate c, String s) throws GameStateException {
        //let it pass.
    }

    public void trySetTile(Coordinate c, String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFirstNumber(Coordinate c) throws GameStateException {
        throw new GameStateException("You must place all tiles before you can place the numbers.");
    }

    public void buildStreet(Coordinate c, Player player) throws GameStateException {
        throw new GameStateException("You cannot capture streets at this stage.");
    }

    public void buildSettlement(Coordinate c, Player player) throws GameStateException {
        throw new GameStateException("Er kunnen momenteel geen nederzettingen gebouwd worden.");
    }

    public void buildCity(Coordinate c, Player player) throws GameStateException {
        throw new GameStateException("Er kunnen momenteel geen steden gebouwd worden.");
    }

    public void moveRobber(Coordinate coordinate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getTiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getNodes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getBorders() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getTileDetails() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getNodeDetails() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getBorderDetails() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeResources(ResourceSet resources) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void rollDice() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void diceRolled() throws GameStateException {
        throw new GameStateException("This cannot be done yet.");
    }

    public void getRobberLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getPlayerResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getPlayerKnightForce() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void nextPhase() throws GameStateException {
        if (!game.getBoard().getAllTilesPlaced()) {
            throw new GameStateException("You cannot continue because you have not placed all tiles.");
        }
        //    game.setGameState(nextState);
        synchronize(nextState);
    }

    @Override
    public boolean validate() {
        return game.getBoard().getAllTilesPlaced();
    }

    @Override
    public String toString() {
        return "Initialisatie ronde";
    }

    @Override
    public void buyDevelopmentCard(Player player) throws Exception {
            throw new GameStateException("Er kan nu geen ontwikkelingskaart gekocht worden");
    }

    @Override
    public void streetBuilt(int playerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateWhatYouMayDo(Map<Integer, List<String>> events, int currentTurn) {
        this.whatYouMayDo = new ArrayList<Task>();
        whatYouMayDo.add(new Task(PlayerTask.WAIT, true));
    }
}