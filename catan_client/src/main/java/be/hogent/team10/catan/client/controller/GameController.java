package be.hogent.team10.catan.client.controller;

import be.hogent.team10.catan.client.facade.DummyServiceFacade;
import be.hogent.team10.catan.client.facade.ServiceFacade;
import be.hogent.team10.catan.client.facade.ServiceFacadeInterface;
import be.hogent.team10.catan.client.util.PollDaemon;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Board;
import be.hogent.team10.catan_businesslogic.model.Border;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Node;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.Tile;
import be.hogent.team10.catan_businesslogic.model.TradeRequest;
import be.hogent.team10.catan_businesslogic.model.TradeRequestStatus;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.gameState.InitializationState;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import be.hogent.team10.catan_businesslogic.util.PlayerColor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jonasanseeuw Better sollution needed for this observer for the state.
 */
public class GameController implements Observer {

    private Game game;
    private ServiceFacadeInterface serviceFacade;
    private PollDaemon deamon;

    public GameController(int gameId, int playerId) {
        try {
            game = new Game();
            game.setGameId(gameId);
            game.setMy_id(playerId);
            this.game.initialize();
            game.setGameState(new InitializationState(game));
            //Toegevoegd Joachim
            game.addObserver((Observer) this);
            //
            this.serviceFacade = new ServiceFacade();
            this.deamon = new PollDaemon(game);
            deamon.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public GameController(PollDaemon deamon, int gameId, int playerId) {
        try {
            this.game = new Game();
            game.setCreator(playerId);
            game.setGameId(gameId);
            game.setMy_id(playerId);
            game.setCurrentPlayer(1);
            //Toegevoegd Joachim
            game.addObserver((Observer) this);
            //
            List<Player> players = new ArrayList<Player>();
            game.initialize();
            players.add(new Player("OfflinePlayer1", 1, "noreply", "nopassword"));
            players.get(0).setPlayerColor(PlayerColor.BLUE);
            game.setPlayers(players);
            game.setGameId(1000);
            game.setMy_id(1);
            this.game.generateTiles();
            this.game.setGameState(new InitializationState(game));
            this.serviceFacade = new DummyServiceFacade();
            this.deamon = new PollDaemon(game, serviceFacade);
            this.deamon.start();
        } catch (Exception ex) {
        }
    }

    /**
     * @return the board
     */
    public Board getBoard() {
        return game.getBoard();
    }

    /**
     * @param board the board to set
     */
    public void setBoard(Board board) {
        this.game.setBoard(board);
    }

    /**
     * @return the players
     */
    public List<Player> getPlayers() {
        return game.getPlayers();
    }

    /**
     * @param players the players to set
     */
    public void setPlayers(List<Player> players) {
        this.game.setPlayers(players);
    }

    public ResourceSet getPlayerResourceSet(int playerId) throws ObjectNotFoundException {
        return this.game.getPlayer(playerId).getResources();
    }

    public int getAmountCards(int type) throws ObjectNotFoundException {
        return this.game.getAmountCards(this.getClient().getId(), type);
    }

    /**
     *
     * @return the ID of the player who holds the knightforce card, if 0, no one
     * has it.
     */
    public int getKnightForce() {
        if (this.getPlayers() != null) {
            for (Player p : this.getPlayers()) {
                if (p.getBiggestKnightForce()) {
                    return p.getId();
                }
            }
        }
        return 0;
    }

    @Deprecated
    private Player getOwner(Node node) {
        for (Player p : game.getPlayers()) {
            for (Node n : p.getCapturedNodes()) {
                if (n.equals(node)) {
                    return p;
                }
            }
        }

        return null;
    }

    // Player stuff
    public Player getCurrentPlayer() {
        return game.getCurrentPlayerObject();
    }

    public int getCurrentPlayerNumber() {
        return game.getCurrentPlayer();
    }

    public Player getClient() throws ObjectNotFoundException {
        return game.getMyPlayerObject();
    }

    public void endTurn() {
        //deamon.delay();
        serviceFacade.endTurn(game.getGameId(), game.getMy_id());
    }

    public Map<Coordinate, Tile> getTiles() {
        return game.getBoard().getTiles();
    }

    public PlayerColor getPlayerColor(int playerId) throws ObjectNotFoundException {
        return game.getPlayer(playerId).getPlayerColor();
    }

    public int getAvailableCities(Player player) {
        return player.getAvailableCities();
    }

    public int getAvailableSettlements(Player player) {
        return player.getAvailableSettlements();
    }

    public int getAvailableStreets(Player player) {
        return player.getAvailableStreets();
    }

    public Map<Coordinate, Border> getAvailableBorderSpots() throws Exception {
        return game.getBoard().getAvailableBorders(game.getMy_id());
    }

    public Map<String, String> getTileDetails(Coordinate c) throws Exception {
        return game.getBoard().getTile(c).getDetails();
    }

    public Map<String, String> getNodeDetails(Coordinate c) {
        return game.getBoard().getNode(c).getDetails();
    }

    public Map<String, String> getBorderDetails(Coordinate c) throws Exception {
        return game.getBoard().getBorder(c).getDetails();
    }

    // Board stuff
    public Map<Coordinate, Node> getNodes() {
        return game.getBoard().getNodes();
    }

    public Map<Coordinate, Border> getBorders() {
        return game.getBoard().getBorders();
    }

    public Map<Coordinate, Tile> getTile() {
        return game.getBoard().getTiles();
    }

    public String getTileType(Coordinate tileCoordinate) throws Exception {
        Tile theTile = game.getBoard().getTile(tileCoordinate);
        return theTile.getTileType().name();
    }

    // Dices
    /**
     * Rolls the dice
     *
     * @return the number of the dice
     */
    public void rollDice() throws Exception {
        // clientside validation

        // send request to server
        serviceFacade.rollDice(game.getGameId(), game.getMy_id());
    }

    public int getDiceValue() {
        return game.getDice().getValue();
    }

    /**
     * GUI tells the controller who wants to build a settlement and where he/she
     * wants to build it
     *
     * @param coordinate Coordinate of the node where a settlement will be
     * build.
     * @param player The player who wants to build a home
     * @throws Exception
     */
    public void buildSettlement(Coordinate coordinate) throws Exception {
        // clientside validation

        // send to service.
        //deamon.delay();
        serviceFacade.buildSettlement(game.getGameId(), game.getMyPlayerObject().getId(), coordinate);
    }

    /**
     *
     * @param coordinate
     * @param player
     * @throws Exception
     */
    public void buildCity(Coordinate coordinate) throws Exception {
        // clientside validation

        // send to service
        //deamon.delay();
        serviceFacade.buildCity(game.getGameId(), game.getMyPlayerObject().getId(), coordinate);
    }

    public void buyDevelopmentCard() throws Exception {
        serviceFacade.buyDevelopmentCard(game.getGameId(), game.getMyPlayerObject().getId());
    }

    public void playDevelopmentCard(int type) throws Exception {
        serviceFacade.playDevelopmentCard(game.getGameId(), game.getMyPlayerObject().getId(), type);
    }

    /**
     * GUI tells the controller who wants to build a street and on which
     * coordinate he/she wants to build it
     *
     * @param coordinate
     * @param player
     * @throws Exception
     */
    public void buildStreet(Coordinate coordinate) throws Exception {
        // clientside validation

        // send to service
        //deamon.delay();
        serviceFacade.buildStreet(game.getGameId(), game.getMyPlayerObject().getId(), coordinate);
    }

    /**
     * Places the robber on the tile with given coordinate
     *
     * @param coordinate where the robber will be placed
     * @param player who moves the robber
     */
    public void moveRobber(Coordinate coordinate) throws ObjectNotFoundException, Exception {
        // clientside validation

        // send to service
        serviceFacade.moveRobber(game.getGameId(), game.getMyPlayerObject().getId(), coordinate);
    }

    /**
     * Gets the coordinates of the surrounding nodes of a tile
     *
     * @param tileCoordinate
     * @return List of coordinates
     */
    public List<Coordinate> getSurroundingNodes(Coordinate tileCoordinate) {
        List<Coordinate> coords = new ArrayList<Coordinate>();;
        try {
            List<Node> nodes = game.getBoard().getSurroundingNodes(tileCoordinate);

            for (Node n : nodes) {
                coords.add(n.getCoordinate());
            }
        } catch (Exception ex) {
        }
        return coords;
    }

    /**
     * Retrieves the coordinates of taken nodes arround a specific tile.
     *
     * @param tile
     * @return
     * @throws Exception
     */
    public List<Coordinate> getSurroundingTakenNodes(Coordinate c) throws Exception {
        List<Coordinate> takenNodes = new ArrayList<Coordinate>();
        for (Node n : game.getBoard().getSurroundingNodes(c)) {
            if (n.getOwner() != null && n.getOwner() != this.getCurrentPlayer()) {
                takenNodes.add(n.getCoordinate());
            }
        }
        return takenNodes;
    }

    public ResourceSet getPrice(String component) {
        return game.getPrice(component);
    }

    public Map<String, Integer> getBankResources() {
        return game.getResources().getResources();
    }

    public Map<Coordinate, Node> getAvailableNodes() throws Exception {
        return game.getBoard().getAvailableBuildingSpots(game.getMy_id());
    }

    /**
     *
     * @param player
     * @param send
     * @param quantity
     * @return
     */
    public List<String> getTradeableResources(String send, int quantity) {
        try {
            Player player = game.getMyPlayerObject();
            return player.getTradeableResources(send, quantity);
        } catch (ObjectNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<String>();
    }

    /**
     *
     * @param player
     * @param send
     * @param receive
     * @param quantity
     */
    public void tradeResource(Player player, String send, String receive, int quantity) throws Exception {
        ResourceSet reward = new ResourceSet();
        reward.add(Resource.valueOf(receive), quantity);
        TradeRequest tradeRequest = new TradeRequest(game.getGameId(), game.getMy_id(), 0, new ResourceSet(), reward);
        // clientside validation

        // send to service
        //deamon.delay();
        serviceFacade.trade(game.getGameId(), tradeRequest);
    }

    public boolean checkEnoughResourcesForSettlement() {
        try {
            return game.getMyPlayerObject().getResources().checkEnoughResources(game.getPrice("SETTLEMENT"));
        } catch (ObjectNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean checkEnoughResourcesForCity() {
        try {
            return game.getMyPlayerObject().getResources().checkEnoughResources(game.getPrice("SETTLEMENT"));
        } catch (ObjectNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean checkEnoughResourcesForStreet() {
        try {
            return game.getMyPlayerObject().getResources().checkEnoughResources(game.getPrice("SETTLEMENT"));
        } catch (ObjectNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean checkEnoughResourcesFor(String Component) {
        try {
            return game.getMyPlayerObject().getResources().checkEnoughResources(game.getPrice(Component));
        } catch (ObjectNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

//todo: is this needed?
//todo 2: enkel de sender meegeven. want de reciever is game.getMyPlayerObject();
    /**
     * Takes a random card from another player in robber state.
     *
     * @param receiver
     * @param sender
     */
    public void takeCardFromPlayer(Player receiver, Player sender) throws Exception {
        serviceFacade.stealResource(game.getGameId(), receiver.getId(), sender.getId());
    }

    public void addObserver(Observer o) {
        game.addObserver(o);
    }

    public int getCreator() {
        return game.getCreator();
    }

    public void ready() throws Exception {
        for (Node n : game.getNodes()) {
            n.setOwner(n.getOwner());
        }

        for (Border b : game.getBorders()) {
            b.setOwner(b.getOwner());
        }

        game.getGameState().triggerUpdates(game.getGameState());
        for (Player p : game.getPlayers()) {
            game.setChanged();
            game.notifyObservers("ADD_PLAYER");
        }
    }

    public void startGame() {
        this.serviceFacade.startGame(game.getGameId(), game.getMy_id());
    }

    public boolean myTurn() {
        return game.getMy_id() == game.getCurrentPlayer();
    }

    public Game getGame() {
        return game;
    }

    public GameState getGameState() {
        if (game != null) {
            return this.game.getGameState();
        }
        InitializationState i = new InitializationState(game);
        return i;
    }

    //rsOffer is what the receiving player gets and rsReceive is what the receiving player
    //has to give to the sending player.
    public void trade(Player tradeFromPlayer, Player tradeWithPlayer, ResourceSet rs) throws Exception {
        Map<String, Integer> resources = rs.getResources();
        ResourceSet rsOffer = new ResourceSet();
        ResourceSet rsReceive = new ResourceSet();
        for (Map.Entry<String, Integer> entry : resources.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value != 0) {
                Resource resource = Enum.valueOf(Resource.class, key);
                if (value < 0) {
                    value = Math.abs(value);
                    rsOffer.add(resource, value);
                } else {
                    rsReceive.add(resource, value);
                }
            }
        }

        TradeRequest tr = new TradeRequest(getGame().getGameId(), tradeFromPlayer.getId(), tradeWithPlayer.getId(), rsOffer, rsReceive);

        // send to service
        //deamon.delay();
        serviceFacade.trade(game.getGameId(), tr);
    }

    public void replyTrade(boolean accepted) throws Exception {
        if (accepted) {
            System.out.println("Trade is accepted!");
            TradeRequest trade = game.getTrade();
            int acceptingPlayer = trade.getPlayerReceiving();
            game.setTrade(null);
            serviceFacade.replyTrade(game.getGameId(), acceptingPlayer, trade.getTradeRequestId(), accepted);
        }
    }

    public int getClientNumber() {
        return game.getMy_id();
    }

    public void update(Observable o, Object b) {
        String trade = b + "";
        if (o instanceof Game && trade.equalsIgnoreCase("TRADE")) {
            Game game = (Game) o;
            TradeRequest tradeRequest = game.getTrade();
            if (tradeRequest.getPlayerReceiving() == game.getMy_id()) {
                GuiController.getInstance().askForTradeAccept(tradeRequest.getOffer(), tradeRequest.getReward(), game.getCurrentPlayerObject());
            }
        }
    }
}
