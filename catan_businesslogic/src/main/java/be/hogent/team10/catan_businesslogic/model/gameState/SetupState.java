package be.hogent.team10.catan_businesslogic.model.gameState;

import be.hogent.team10.catan_businesslogic.model.Border;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Node;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.Tile;
import be.hogent.team10.catan_businesslogic.model.TileType;
import be.hogent.team10.catan_businesslogic.model.exception.CannotBuildException;
import be.hogent.team10.catan_businesslogic.model.exception.GameStateException;
import be.hogent.team10.catan_businesslogic.util.PlayerTask;
import be.hogent.team10.catan_businesslogic.util.Task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Represents the setup state in which the numbers are placed upon all the
 * resource tiles. Besides that, the players will be able to build 2 houses and
 * 2 streets.
 *
 * @author HP
 */
public class SetupState extends GameState {

    public SetupState(Game gc) {
        super(gc);
        nextState = new PlayGameState(gc);
        setCurrentState("SETUP_STATE");
    }

    @Override
    public void setTile(Coordinate c, String s) throws GameStateException {
        throw new GameStateException("You cannot move tiles in this stage of the game.");
    }

    @Override
    public void setFirstNumber(Coordinate c) throws GameStateException {
        for (Tile t : game.getTiles()) {
            if (t.getNumber() != 0) {
                throw new GameStateException("You have already assigned the numbers.");
            }
        }
        if (validate()) {
            nextPhase();
        }
    }

    @Override
    public void buildStreet(Coordinate c, Player player) throws GameStateException, Exception {
        /*
         * if you have built 2 streets, you cannot build another one.
         */
        if (player.getCapturedBorders().size() == 2) {
            throw new GameStateException("Je kan momenteel geen straten bijleggen.");

        }
        /*
         * if you have built 1 settlement annd 1 street, you must first build a settlement
         */
        if (player.getCapturedBorders().size() == player.getCapturedNodes().size() && player.getCapturedBorders().size() == 1) {
            throw new GameStateException("Je moet eerst de volgende nederzetting bouwen.");
        }

        boolean connected = false, lasthouse = true;
        Node connectedNode = null;
        for (Node n : game.getBoard().getNeighbourNodesFromBorder(c)) {
            if (n.getOwner() != null && n.getOwner().equals(player)) {
                connected = true;
                connectedNode = n;
            }
        }
        if (connected) {
            for (Border b : game.getBoard().getNeighbourBorders(connectedNode.getCoordinate())) {
                if (b.getOwner() == player) {
                    lasthouse = false;
                }
            }
        } else {
            lasthouse = false;
        }

        if (!lasthouse) {
            throw new GameStateException("Jouw straat moet grenzen aan jouw laatst gebouwde huis");
        }
        if (validate()) {
            nextPhase();
        }
    }
    
    @Override
    public void streetBuilt(int playerId) {
        List<Player> nextplayers = new ArrayList<Player>();
        // only if it is time for the next player.
        //reverse.
        if (game.getCurrentPlayerObject().getCapturedBorders().size() == 2) {
            System.out.println("2 streets built.");
            // if player has 2 houses and 2 streets:
            // no other players with 1 house, 1 street next player = current player;
            // players with 1 house, 1 street -> next player = current player;
            for (Player p : game.getPlayers()) {
                if (p.getCapturedNodes().size() == 1) {
                    nextplayers.add(p);
                }
            }

            // if there are players with 0 cities, we may not do this.
            for (Player p : game.getPlayers()) {
                if (p.getCapturedNodes().isEmpty()) {
                    nextplayers.clear();
                }
            }
            // if there are players with only 1 house
            if (!nextplayers.isEmpty()) {
                Collections.sort(nextplayers, new Comparator<Player>() {
                    @Override
                    public int compare(Player t, Player t1) {
                        System.out.println(t.getTimeJoined() == null);
                        System.out.println(t1.getTimeJoined() == null);
                        if(t.getTimeJoined() == null)
                            return -1;
                        if(t1.getTimeJoined() == null)
                            return 1;
                        if (t.getTimeJoined().after(t1.getTimeJoined())) {
                            return 1;
                        }
                        return -1;
                    }
                });
                 //^-- sorts reverse-order.
                game.setCurrentPlayer(nextplayers.get(nextplayers.size() - 1).getId());
                game.setTurnId(-1);
            }
        }
        // next player 
        else if (game.getCurrentPlayerObject().getCapturedBorders().size() == 1 && game.getCurrentPlayerObject().getCapturedNodes().size() == 1){
            System.out.println("1 streets built.");
            List<Player> players = game.getPlayers();
            for(Player p:players){
                if(p.getCapturedNodes().isEmpty())
                    nextplayers.add(p);
            }
            Collections.sort(nextplayers, new Comparator<Player>() {
                    @Override
                    public int compare(Player t, Player t1) {
                        System.out.println((t.getTimeJoined() == null) +"de boel is null");
                        System.out.println((t1.getTimeJoined() == null) +"de boel is null");
                        if(t.getTimeJoined() == null)
                            return -1;
                        if(t1.getTimeJoined() == null)
                            return 1;

                        if (t.getTimeJoined().after(t1.getTimeJoined())) {
                            return 1;
                        }
                        return -1;
                    }
                });
            System.out.println(players);
            int index = players.indexOf(game.getCurrentPlayerObject());
            // all players have 1 settlement, 1 street.
            if(nextplayers.isEmpty()){
                game.setCurrentPlayer(game.getCurrentPlayer());
                game.setTurnId(-1);
            }
            else
                game.setCurrentPlayer(nextplayers.get(0).getId());
        
        }
    }

    @Override
    public void buildSettlement(Coordinate c, Player player) throws GameStateException, CannotBuildException {
        if (player.getCapturedNodes().size() == 2) {
            System.out.println(player.getCapturedNodes().size());
            throw new GameStateException("Je kan nu geen nederzettingen meer bouwen.");
        }
        if (player.getCapturedBorders().size() != player.getCapturedNodes().size()) {
            throw new CannotBuildException("Bouw eerst een straat naast je laatste nederzetting.");
        }
    }

    @Override
    public void buildCity(Coordinate c, Player player) throws GameStateException {
        throw new GameStateException("Je kan nu nog geen stad bouwen.");
    }

    @Override
    public void buyDevelopmentCard(Player player) throws Exception {
        throw new GameStateException("Je kan nu nog geen ontwikkelingskaart kopen");
    }

    @Override
    public void moveRobber(Coordinate coordinate) throws GameStateException {
        throw new GameStateException("You cannot move the robber now.");
    }

    @Override
    public void removeResources(ResourceSet resources) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Staat tijdelijk uit!!
    @Override
    public void nextPhase() throws GameStateException {
        boolean ok = true;
        for (Player p : game.getPlayers()) {
            if (p.getCapturedBorders().size() != 2) {
                ok = false;
            }
            if (p.getCapturedNodes().size() != 2) {
                ok = false;
            }
        }
        if (ok) {
            synchronize(nextState);
        }
    }

    /**
     * This method overrides the prices of settlements and streets.
     */
    @Override
    protected void initialize() {
        game.setPrice("SETTLEMENT", new ResourceSet(0, 0, 0, 0, 0));
        game.setPrice("STREET", new ResourceSet(0, 0, 0, 0, 0));
        game.setPrice("CITY", new ResourceSet(999, 999, 999, 999, 999));
    }

    @Override
    public void rollDice() throws GameStateException {
        throw new GameStateException("Je kan nu niet met de dobbelsteen gooien.");
    }

    @Override
    public void diceRolled() throws GameStateException {
        throw new GameStateException("This cannot be done yet.");
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
    public String toString() {
        return "Opzettingsronde";
    }

    @Override
    public boolean validate() {
        for (Player p : game.getPlayers()) {
            if (p.getCapturedBorders().size() != 2) {
                return false;
            }
            if (p.getCapturedNodes().size() != 2) {
                return false;
            }
            for (Tile tile : game.getTiles()) {
                if (tile.getTileType() == TileType.RESOURCE && tile.getNumber() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void updateWhatYouMayDo(Map<Integer, List<String>> events, int currentTurn) {
        this.whatYouMayDo = new ArrayList<Task>();
        if(events.get(currentTurn) == null){
            whatYouMayDo.add(new Task(PlayerTask.BUILDHOUSE, true));
        }
        else{
        if(!events.get(currentTurn).contains("COMPONENT_GAME")){
            whatYouMayDo.add(new Task(PlayerTask.BUILDHOUSE, true));
        }else
            whatYouMayDo.add(new Task(PlayerTask.BUILDSTREET, true));
        }
}
}
