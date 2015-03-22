package be.hogent.team10.catan_businesslogic.model;

import be.hogent.team10.catan_businesslogic.model.exception.GameStateException;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import be.hogent.team10.catan_businesslogic.model.exception.ResourceException;
import be.hogent.team10.catan_businesslogic.model.exception.TradeException;
import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.gameState.InitializationState;
import be.hogent.team10.catan_businesslogic.model.gameState.RobberState;
import be.hogent.team10.catan_businesslogic.util.Dice;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Task;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 *
 * @author HP
 */
public class Game extends Observable {

    @Expose
    private List<Player> players;
    @Expose
    private String state;
    @Expose
    private int gameId;
    private int turnId;
    private int creator;
    private int my_id;
    private int currentPlayer;
    private int gameStamp;
    private int tradeId=99999;
    private Dice dice;
    private ResourceSet resources;
    private Map<String, ResourceSet> prices;
    private Calendar timeStarted;
    private Calendar timeCreated;
    private List<Task> tasks;
    private List<TradeRequest> tradeRequests;
    private GameState gameState;
    private Board board;
    private List<DevelopmentCard> developmentCards;
    private TradeRequest trade = null;

    /**
     *
     * @param creatorId
     */
    public Game(int creatorId) {
        this.creator = creatorId;
    }

    /**
     * Constructor
     */
    public Game() {
        this.board = new Board();
        this.resources = new ResourceSet();
        this.dice = new Dice();
    }

    /**
     * Sets the game's GameState to InGameState
     */
    public void setGameInGameState() {
        if (this.gameState != null) {
            this.gameState.setGameInGameState(this);
        }
    }

    /**
     *
     * @param id player id
     * @return player with specified id
     * @throws ObjectNotFoundException
     */
    public Player getPlayer(int id) throws ObjectNotFoundException {
        System.out.println(players.isEmpty()+"empty");
        for (Player p : players) {
            System.out.print(p.getId());
            if (p.getId() == id) {
                return p;
            }
        }
        throw new ObjectNotFoundException("That player was not found. PlayerId incorrect.");
    }

    /**
     *
     * @return You
     * @throws ObjectNotFoundException
     */
    public Player getMyPlayerObject() throws ObjectNotFoundException {
        return getPlayer(my_id);
    }

    /**
     *
     * @return the id from the game
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Rolls the dice
     *
     * @throws GameStateException
     * @throws Exception
     */
    public void rollDice() throws GameStateException, Exception {
        this.gameState.rollDice();
        if (dice == null) {
            dice = new Dice();
        }
        this.dice.rollDice();
        if (dice.getValue() != 7) {
            this.distributeResources(dice.getValue());
        }
        this.gameState.diceRolled();
    }

    /**
     * @param numberOfEyes
     * @throws Exception
     */
    private void distributeResources(int numberOfEyes) throws Exception {
        List<Tile> theTiles = board.getTilesByNumber(numberOfEyes);
        ResourceSet needed = new ResourceSet();
        for (Tile tile : theTiles) {
            /*
             * if the robber is not there.
             */
            if (!tile.getHasRobber() && !(board.getRobber().equals(tile.getCoordinate()))) {
                /*
                 * Since only resourceTiles have a number between 2 and 12, we don't have to check the tiletype
                 */
                Resource resource = tile.getResource();
                List<Node> theNodes = board.getSurroundingNodes(tile.getCoordinate());
                /*
                 * check how many resources are required for this tile
                 */
                for (Node node : theNodes) {
                    switch (node.getBuildingtype()) {
                        case SETTLEMENT:
                            needed.add(resource, 1);
                            break;
                        case CITY:
                            needed.add(resource, 2);
                            break;
                        default:
                            break;
                    }
                }
            }
            /*
             * @throws exception.
             */
        }
        removeResources(needed);

        /*
         * We got here, that means the game had enough cards. Now we can give resources to players.
         */
        for (Tile tile : theTiles) {
            //skip all nodes of this tile if the robber is there.
            if (!(tile.getHasRobber()) && !(board.getRobber().equals(tile.getCoordinate()))) {
                Resource resource = tile.getResource();
                List<Node> theNodes = board.getSurroundingNodes(tile.getCoordinate());
                for (Node n : theNodes) {
                    Player owner = n.getOwner();
                    /*
                     * if captured
                     */
                    if (owner != null) {
                        owner.addResource(resource, (n.getBuildingtype() == BuildingType.CITY ? 2 : 1));
                    }
                }
            }
        }

    }

    private void removeResources(ResourceSet needed) throws Exception {
        if (resources.checkEnoughResources(needed)) {
            resources.remove(needed);
        } else {
            throw new ResourceException("Niet genoeg beschikbare grondstoffen.");
        }
    }

    /**
     * Sets the prices for components
     *
     * @param item the component you want to put a price on
     * @param price the price
     */
    public void setPrice(String item, ResourceSet price) {
        if (prices == null) {
            this.prices = new HashMap<String, ResourceSet>();
        }
        this.prices.put(item, price);
    }

    /**
     * Get the price for a component
     *
     * @param component
     * @return the price
     */
    public ResourceSet getPrice(String component) {
        return prices.get(component);
    }

    /**
     * Sets the players
     *
     * @param players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Sets the borders
     *
     * @param allBorders
     */
    public void setBorders(List<Border> allBorders) {
        Map<Coordinate, Border> borderMap = new HashMap<Coordinate, Border>();
        for (Border b : allBorders) {
            System.out.println("putting border into borders.");
            System.out.println(b.getCoordinate());
            borderMap.put(b.getCoordinate(), b);
        }
        System.out.println(board.getBorders().size());
        board.setBorders(borderMap);
    }

    /**
     * Sets the nodes
     *
     * @param allNodes
     */
    public void setNodes(List<Node> allNodes) {
        Map<Coordinate, Node> nodeMap = new HashMap<Coordinate, Node>();
        for (Node n : allNodes) {
            nodeMap.put(n.getCoordinate(), n);
        }
        board.setNodes(nodeMap);
    }

    /**
     * Sets the tiles
     *
     * @param allTiles
     */
    public void setTiles(List<Tile> allTiles) {
        Map<Coordinate, Tile> tileMap = new HashMap<Coordinate, Tile>();
        for (Tile t : allTiles) {
            tileMap.put(t.getCoordinate(), t);
        }
        board.setTiles(tileMap);
    }

    /**
     * Sets the developmentcards
     *
     * @param allDevelopmentCards
     */
    public void setDevelopmentCards(List<DevelopmentCard> allDevelopmentCards) {
        this.developmentCards = allDevelopmentCards;
    }

    /**
     * Set the creator of the game
     *
     * @param p the creator of the game
     */
    public void setCreator(int p) {
        this.creator = p;
    }

    /**
     * Sets the resources from the bank
     *
     * @param resources
     */
    public void setResources(ResourceSet resources) {
        this.resources = resources;
    }

    /**
     * Sets the gameId
     *
     * @param gameId
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setTurnId(int turnId) {
        this.turnId = turnId;
    }

    public int getGameStamp() {
        return gameStamp;
    }

    /**
     * Set GameStamp
     *
     * @param gameStamp
     */
    public void setGameStamp(int gameStamp) {
        this.gameStamp = gameStamp;
    }

    /**
     * Get TimeStarted
     *
     * @return
     */
    public Calendar getTimeStarted() {
        return timeStarted;
    }

    /**
     * Set TimeStarted
     *
     * @param timeStarted
     */
    public void setTimeStarted(Calendar timeStarted) {
        this.timeStarted = timeStarted;
    }

    /**
     *
     * @return
     */
    public Calendar getTimeCreated() {
        return timeCreated;
    }

    /**
     *
     * @param timeCreated
     */
    public void setTimeCreated(Calendar timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getTurnId() {
        return this.turnId;
    }

    /**
     *
     * @param task
     */
    public void addTask(Task task) {
        if (tasks == null) {
            this.tasks = new ArrayList<Task>();
        }
        tasks.add(task);
    }

    /**
     *
     * @return
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     *
     * @return
     */
    public int getCreator() {
        return creator;
    }

    /**
     *
     * @return
     */
    public ResourceSet getResources() {
        return resources;
    }

    /**
     *
     * @return
     */
    public List<Border> getBorders() {
        return new LinkedList<Border>(board.getBorders().values());
    }

    /**
     *
     * @return
     */
    public List<Node> getNodes() {
        return new LinkedList<Node>(board.getNodes().values());
    }

    /**
     *
     * @return
     */
    public List<Tile> getTiles() {
        return new LinkedList<Tile>(board.getTiles().values());
    }

    /**
     *
     * @param tradeRequests
     */
    public void setTradeRequests(List<TradeRequest> tradeRequests) {
        this.tradeRequests = tradeRequests;
    }

    /**
     *
     * @param request
     */
    public void addTradeRequest(TradeRequest request) {
        if (this.tradeRequests == null) {
            tradeRequests = new ArrayList<TradeRequest>();
        }
        this.tradeRequests.add(request);
    }

    /**
     *
     * @param request
     */
    public void removeTradeRequest(TradeRequest request) {
        this.tradeRequests.remove(request);
    }

    /**
     *
     * @return
     */
    public List<TradeRequest> getTradeRequests() {
        return tradeRequests;
    }

    /**
     *
     * @param currentPlayer
     */
    public void setCurrentPlayer(int currentPlayer) {
        System.out.println(this.currentPlayer);
        System.out.println(currentPlayer);
        this.currentPlayer = currentPlayer;
        setChanged();
        notifyObservers("NEXT_PLAYER");
    }

    /**
     *
     * @return
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     *
     * @return
     */
    public Dice getDice() {
        return dice;
    }

    /**
     *
     * @param dice
     */
    public void setDice(Dice dice) {
        this.dice = dice;
    }

    /**
     *
     * @return
     */
    public int getMy_id() {
        return my_id;
    }

    /**
     *
     * @param my_id
     */
    public void setMy_id(int my_id) {
        this.my_id = my_id;
    }

    /**
     *
     * @return
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     *
     * @param tasks
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     *
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     */
    public Map<String, ResourceSet> getPrices() {
        return prices;
    }

    /**
     *
     * @param prices
     */
    public void setPrices(Map<String, ResourceSet> prices) {
        this.prices = prices;
    }

    /**
     *
     * @return
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     *
     * @param gameState
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     *
     * @return
     */
    public Board getBoard() {
        return board;
    }

    /**
     *
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     *
     * @param trade
     */
    public void setTrade(TradeRequest trade) {
        this.trade = trade;
    }

    /**
     *
     * @return
     */
    public TradeRequest getTrade() {
        return trade;
    }

    /**
     * Initializes the game.
     *
     * @throws Exception
     */
    public void initialize() throws Exception {
        this.board.init();
        if (players == null) {
            players = new ArrayList<Player>();
        }
        if (dice == null) {
            dice = new Dice();
        }
    }

    /**
     * Method that generates the tiles randomly at the screen.
     *
     * @throws Exception
     */
    public void generateTiles() throws Exception {
        Random random = new Random();
        Iterator<Tile> it = board.getTiles().values().iterator();
        while (it.hasNext()) {
            Tile t = it.next();
            boolean exception = true;
            while (t.getTileType() == null) {
                int resNumber = random.nextInt(board.getAvailableTiles().size());
                try {
                    String resource = (String) board.getAvailableTiles().keySet().toArray()[resNumber];
                    if (resource.equals("BRICK")) {
                        board.placeTile(t.getCoordinate(), Resource.BRICK);
                    } else if (resource.equals("LUMBER")) {
                        board.placeTile(t.getCoordinate(), Resource.LUMBER);
                    } else if (resource.equals("GRAIN")) {
                        board.placeTile(t.getCoordinate(), Resource.GRAIN);
                    } else if (resource.equals("ORE")) {
                        board.placeTile(t.getCoordinate(), Resource.ORE);
                    } else if (resource.equals("WOOL")) {
                        board.placeTile(t.getCoordinate(), Resource.WOOL);
                    } else if (resource.equals("DESERT")) {
                        board.placeTile(t.getCoordinate(), TileType.DESERT);
                    }
                    exception = false;
                } catch (Exception ex) {
                    exception = true;
                }
            }
            exception = true;
        }
        board.setNumbers(new Coordinate(3, 7), true);
    }

    /**
     *
     * @return
     */
    public List<DevelopmentCard> getDevelopmentCards() {
        return this.developmentCards;
    }

    /**
     *
     * @param updatedGame
     * @throws Exception
     */
    public void synchronize(Game updatedGame) throws Exception {
        /*
         * check if the curent player has changed.
         */
        if (this.getCurrentPlayer() != updatedGame.getCurrentPlayer()) {
            this.setCurrentPlayer(updatedGame.getCurrentPlayer());
        }
        if (updatedGame.getDice() != null) {
            this.dice.synchonize(updatedGame.getDice());
        }
        this.resources.synchronize(updatedGame.getResources());
        this.creator = updatedGame.getCreator();
        /*
         * If a player joined, the list of players in updatedgame is not null.
         */
        if (players == null) {
            players = new ArrayList<Player>();
        }
        if (updatedGame.getPlayers() != null) {
            for (Player p : updatedGame.getPlayers()) {
                if (!this.getPlayers().contains(p)) {
                    this.players.add(p);
                    setChanged();
                    System.out.println("notifying --- " + ((Observable) this).countObservers() + " players");
                    notifyObservers("ADD_PLAYER");
                }
            }
        }
        /*
         * Updated borders and nodes are fully loaded, except for the player. This is because json would cause an
         * infinite loop, and doing this, we reduce database-access. Therefore we add a dummy-object in the repository.
         * It is now time to set a full playerobject.
         *
         * Remark : For serverside validation, this list only contains 1 player. This is because we often only need
         * information about the player that invoked this action. Other players are then identied using the id (only
         * field that is set in the dummy-player object that is now replaced).
         *
         */
        if (updatedGame.getNodes() != null) {
            for (Node node : updatedGame.getNodes()) {
                for (Player p : players)// these have all properties set.
                {
                    if (p.equals(node.getOwner())) { // equals methode checks user_id.
                        node.setOwner(p);
                        Node actualnode = board.getNode(node.getCoordinate());
                        actualnode.setOwner(p);
                        actualnode.synchronize(node);
                        p.addNode(actualnode);
                    }
                }
            }
        }
        if (updatedGame.getBorders() != null) {
            for (Border border : updatedGame.getBorders()) {
                for (Player p : players)// these have all properties set.
                {
                    if (p.equals(border.getOwner())) { // equals methode checks user_id.       
                        border.setOwner(p);
                        Border actualborder = board.getBorder(border.getCoordinate());
                        actualborder.synchronize(border);
                        p.addBorder(actualborder);
                        //System.out.println(p.getCapturedBorders().size() +" size of the border of the player");
                    }
                }
            }
        }

        if (updatedGame.getDevelopmentCards() != null) {
            System.out.println((updatedGame.getDevelopmentCards().size()) + "kaarten worden upgedate");
            for (DevelopmentCard developmentCard : updatedGame.getDevelopmentCards()) {
                for (Player p : players) {
                    if (p.equals(developmentCard.getOwner())) {
                        developmentCard.setOwner(p);
                        p.addDevelopmentCard(developmentCard);
                    }
                }
            }

            this.determineKnightForceHolder();
        }

        /*
         * Check for traderequests
         */
        if (updatedGame.getTradeRequests() != null) {
            // Aan juiste speler het ruilvoorstel doen
            Player p = this.getMyPlayerObject();
            for (TradeRequest tr : updatedGame.getTradeRequests()) {
                if (p.getId() == tr.getPlayerReceiving()) {
                    System.out.println(" You have a traderequest ! ");
                    if (this.trade == null && tradeId!=tr.getTradeRequestId()) {
                        trade = tr;
                        tradeId = tr.getTradeRequestId();
                        setChanged();
                        notifyObservers("TRADE");
                    }
                } 
            }
        }
        /*
         * sync stuff like resourcesets,available building things, points.
         */
        if (updatedGame.getPlayers() != null) {
            for (Player p : updatedGame.getPlayers()) {
                if (!this.getPlayers().contains(p)) {
                    this.getPlayers().add(p);
                } else {
                    this.getPlayers().get(this.getPlayers().indexOf(p)).synchronize(p);
                }
            }
            //this.determineKnightForceHolder();
        }

        /*
         * sync elements on the board.
         */
        if (updatedGame.getBoard() != null) {
            this.getBoard().synchronize(updatedGame.getBoard());
        }
        gameState.synchronize(updatedGame.getGameState());
        if (gameState instanceof InitializationState) {
            this.gameStamp = 0;
        } else {
            this.gameStamp = updatedGame.getGameStamp();
        }
    }

    /**
     *
     * @param coordinate
     * @param player
     * @throws Exception
     */
    public void buildSettlement(Coordinate coordinate, Player player) throws Exception {
        /*
         * checks if player can build settlement setupstate : if 1 settlement built -> street built? no more than 2
         * settlements built? playstate : does the player have enough resources?
         */
        this.gameState.buildSettlement(coordinate, player);
        /*
         * builds the settlement (if distance-rule is ok) or if gamestate = setupstate
         */
        this.board.buildSettlement(coordinate, player, gameState);
        /*
         * What goes around, comes around. You may pay for your investment now.
         */
        this.resources.add(prices.get("SETTLEMENT"));
        player.removeResources(prices.get("SETTLEMENT"));
        /*
         * 1 settlement built -> settlements --;
         */
        player.setAvailableSettlements(player.getAvailableSettlements() - 1);
    }

    /**
     *
     * @param coordinate
     * @param player
     * @throws Exception
     */
    public void buildCity(Coordinate coordinate, Player player) throws Exception {
        /*
         * checks if player can build city playstate : does the player have enough resources?
         */
        this.gameState.buildCity(coordinate, player);
        /*
         * builds the city (if the player owns that node and nothing but a settlement was built there)
         */
        this.board.buildCity(coordinate, player);
        /*
         * What goes around, comes around. You may pay for your investment now.
         */
        this.resources.add(prices.get("CITY"));
        player.removeResources(prices.get("CITY"));
        /*
         * One City built -> cities --; -> settlements ++;
         */
        player.setAvailableCities(player.getAvailableCities() - 1);
        player.setAvailableSettlements(player.getAvailableSettlements() + 1);
    }

    /**
     *
     * @param coordinate
     * @param player
     * @throws Exception
     */
    public void buildStreet(Coordinate coordinate, Player player) throws Exception {
        /*
         * checks if the payer may build a street there.
         */
        this.gameState.buildStreet(coordinate, player);
        /*
         * builds the street (if street not capured, is next to a node of the same player(setupstate) or next to another
         * street)
         */
        this.board.buildStreet(coordinate, player, gameState);
        /*
         * paytime.
         */
        this.resources.add(prices.get("STREET"));
        player.removeResources(prices.get("STREET"));
        System.out.println("------");
        this.gameState.streetBuilt(player.getId());
        this.gameState.nextPhase();
        System.out.println("------");
        /*
         * One street built -> streets --;
         */
        player.setAvailableStreets(player.getAvailableStreets() - 1);
    }

    /**
     *
     * @param player
     * @return
     * @throws Exception
     */
    public DevelopmentCard buyDevelopmentCard(Player player) throws Exception {
        /**
         * Checks if the player can buy a developmentCard based on his rss and the gamestate.
         */
        this.gameState.buyDevelopmentCard(player);

        /**
         * Buy a random developmentcard
         */
        DevelopmentCard cardToBuy = null;

        int totalNumberOfCards = this.getDevelopmentCards().size();

        Collections.shuffle(getDevelopmentCards());
        for (int i = 0; i < getDevelopmentCards().size() && cardToBuy == null; i++) {
            if (getDevelopmentCards().get(i).getOwner() == null) {
                cardToBuy = getDevelopmentCards().get(i);
            }
        }

        // Found a card, now player will add this to his cards
        if (cardToBuy == null) {
            throw new Exception("Er kunnen geen kaarten meer gekocht worden");
        }

        player.addDevelopmentCard(cardToBuy);

        // Pay the card
        this.resources.add(prices.get("DEVELOPMENTCARD"));
        player.removeResources(prices.get("DEVELOPMENTCARD"));
        return cardToBuy;
    }

    /**
     *
     * @param developmentCard
     * @return
     * @throws Exception
     */
    public DevelopmentCard getDevelopmentCard(DevelopmentCard developmentCard) throws Exception {
        boolean found = false;
        int counter = 0;
        DevelopmentCard d = null;

        while (!found && counter < this.getDevelopmentCards().size()) {
            if (this.getDevelopmentCards().get(counter).getId() == developmentCard.getId()) {
                d = this.getDevelopmentCards().get(counter);
                found = true;
            }
        }

        if (!found) {
            throw new Exception("Ongeldige kaart");
        }
        return d;
    }

    /**
     * Method used to Play a particular developmentcard. Based on the type of developmentcard actions will be launched
     * here.
     *
     * @param developmentCard
     * @param player The player who wants to play a developmantcard
     * @return
     * @throws Exception
     */
    public DevelopmentCard useDevelopmentCard(DevelopmentCard developmentCard, Player player) throws Exception {
        if (developmentCard == null) {
            throw new Exception("Alle kaarten zijn reeds uitgespeeld!");
        }

        //KNIGHT
        if (developmentCard.getDevelopmentCardType() == DevelopmentCardType.KNIGHT) {
            System.out.println("De rover kaart wordt uitspeeld");
            //Activate robberstate, now we will be obligated to move the robber!
            this.setGameState(new RobberState(this));
            player.setKnightforce(player.getKnightforce() + 1);
            determineKnightForceHolder();
        }// UNIVERSITY
        else if (developmentCard.getDevelopmentCardType() == DevelopmentCardType.UNIVERSITY) {
            System.out.println("Er werd een overwinningspunten kaart uitgespeeld");
            player.setPoints(player.getPoints() + 1);
        } else {
            throw new Exception("Ongeldig kaarttype!");
        }

        developmentCard.setPlayed(true);
        return developmentCard;
    }

    /**
     *
     * @return
     */
    public int getAvailableDevelopmentCards() {
        int number = 0;
        for (DevelopmentCard d : this.getDevelopmentCards()) {
            if (d.getOwner() != null) {
                number++;
            }
        }
        return number;
    }

    /**
     *
     * @param coordinate
     * @param diceValue
     * @throws GameStateException
     * @throws Exception
     */
    public void moveRobber(Coordinate coordinate, int diceValue) throws GameStateException, Exception {
        this.gameState.moveRobber(coordinate);
        boolean moved = this.board.moveRobber(coordinate);
        if (moved) {
            this.gameState.nextPhase();
            // Based on the diceValue, we see if the resources need to be divided, if the value of the dice is 7
            // We can be sure, we gotta move the robber cuz of the dice, and not cuz of the knightcard.
            // So now we gotta divide all the players their resources.
            if (diceValue == 7) {
                this.divideResources();
            }
        } else {
            throw new GameStateException("Je moet de rover verplaatsen!");
        }
    }

    /**
     * Accept's a traderequest. It will be rejected if an error occurs.
     *
     * @param request
     * @throws TradeException
     * @throws Exception
     */
    public void acceptTrade(TradeRequest request) throws TradeException, Exception {
        Player sender = null;
        Player receiver = null;
        for (Player p : players) {
            if (p.getId() == request.getPlayerSending()) {
                sender = p;
            } else if (p.getId() == request.getPlayerReceiving()) {
                receiver = p;
            }
        }
        if (sender != null && receiver != null) {
            if (!sender.getResources().checkEnoughResources(request.getOffer())) {
                request.setStatus(TradeRequestStatus.R);
                throw new TradeException("Je hebt niet genoeg grondstoffen om te ruilen.");
            }
            if (!receiver.getResources().checkEnoughResources(request.getReward())) {
                request.setStatus(TradeRequestStatus.R);
                throw new TradeException("Je hebt niet genoeg grondstoffen om te ruilen.");
            }
            receiver.getResources().add(request.getOffer());
            receiver.getResources().remove(request.getReward());
            sender.getResources().add(request.getReward());
            sender.getResources().remove(request.getOffer());
            request.setStatus(TradeRequestStatus.A);
        }
    }

    /**
     * Method used to divide the resources of all the players who have more than 7 resources In this method a submethod
     * is called to make a ResourceSet that will subtracted from all the players.
     */
    public void divideResources() {
        for (Player p : this.getPlayers()) {
            if (p.getResources().getTotalAmount() >= 7) {
                ResourceSet sub = p.makeResourceDivideSet();
                this.resources.add(sub);
                p.divideResources(sub);
            }
        }
    }

    /**
     * Used to trade with the bank.
     *
     * @param request
     * @throws TradeException
     * @throws Exception
     */
    public void tradeWithBank(TradeRequest request) throws TradeException, Exception {
        /*
         * Example : I give 4, I want 1.
         */
        /*
         * If the bank doesn't have enough resources -> can't trade.
         */
        if (!this.resources.checkEnoughResources(request.getReward())) {
            throw new TradeException("De bank heeft niet genoeg grondstoffen om te ruilen.");
        }
        for (Player p : players) {
            if (p.getId() == request.getPlayerSending()) {
                if (!p.getResources().checkEnoughResources(request.getOffer())) {
                    throw new TradeException("Je hebt niet genoeg grondstoffon om te ruilen met de bank.");
                } else {
                    /*
                     * bank has enough resources, player has enough resources.
                     */
                    /*
                     * //todo : while(canTrade()) 4 afdoen, en 1 bijgeven. daj bij een offer van 5: 1 en ratio is 4:1
                     * die ene offer ook niet kwijt bent.
                     */
                    this.resources.remove(request.getReward());
                    this.resources.add(request.getOffer());
                    p.getResources().remove(request.getOffer());
                    p.getResources().add(request.getReward());
                }
            }
        }
    }
    // you can only trade 1 resource at a time for now.

    /**
     * Checks if a player can trade with the Bank.
     *
     * @param request
     * @throws TradeException
     */
    public void checkTradeRatio(TradeRequest request) throws TradeException {
        /*
         * //todo : let player calculate his own ratio. -- with harbours etc.
         */
        ResourceSet ratio = null;
        for (Player player : players) {
            if (player.getId() == request.getGameId()) {
                ratio = player.getTradeRatio();
            }
        }
        if (ratio == null) {
            throw new TradeException("Het trade-ratio kon niet berekend worden.");
        }
        Entry<String, Integer> offer = null;
        Entry<String, Integer> reward = null;
        for (Entry<String, Integer> entry : request.getReward().getResources().entrySet()) {
            if (entry.getValue() != 0) // you cant to recieve this.
            {
                reward = entry;
            }
        }
        for (Entry<String, Integer> entry : request.getOffer().getResources().entrySet()) {
            if (entry.getValue() != 0) // you cant to give this.
            {
                offer = entry;
            }
        }
        if (offer == null || reward == null) {
            throw new TradeException("Je moet grondstoffen kiezen alvorens te ruilen");
        }
        if ((double) offer.getValue() / (double) reward.getValue() != ratio.getAmount(reward.getKey())) {
            throw new TradeException("Je moet meer grondstoffen aanbieden om te kunnen ruilen.");
        }
    }

    /**
     * Trade with another player.
     *
     * @param request
     * @throws TradeException
     * @throws Exception
     */
    public void tradeWithPlayer(TradeRequest request) throws TradeException, Exception {
        for (Player p : players) {
            if (p.getId() == request.getPlayerSending()) {
                if (!p.getResources().checkEnoughResources(request.getOffer())) {
                    throw new TradeException("Je hebt niet genoeg grondstoffon om te ruilen.");
                } else {
                    // Player has enough resources
                    request.setStatus(TradeRequestStatus.P);
                }
            }
        }
    }

    /**
     * Method used in the robber state to steal 1 random resource from another player.
     *
     * @param robber
     * @param victim
     */
    public void stealResource(Player robber, Player victim) {
        Random generator = new Random();
        Resource resource = null;
        try {
            if (!robber.equals(victim)) {
                if (victim.getResources().getTotalAmount() >= 1) {
                    while (resource == null) {
                        int rand = generator.nextInt(5);
                        switch (rand) {
                            case 0:
                                resource = Resource.BRICK;
                                break;
                            case 1:
                                resource = Resource.GRAIN;
                                break;
                            case 2:
                                resource = Resource.LUMBER;
                                break;
                            case 3:
                                resource = Resource.ORE;
                                break;
                            case 4:
                                resource = Resource.WOOL;
                                break;
                        }
                        if (victim.getResources().getAmount(resource) != 0) {
                            robber.addResource(resource, 1);
                            victim.addResource(resource, -1);
                        } else {
                            resource = null;
                        }
                    }
                } else {
                    throw new ResourceException("De gekozen speler heeft geen grondsstoffen");
                }
            } else {
                throw new IllegalArgumentException("Je kan geen grondstof van jezelf stelen.");
            }
        } catch (Exception ex) {
        }
    }

    /**
     *
     * @return
     */
    public Player getCurrentPlayerObject() {
        if (currentPlayer != 0) // still fetching data from server.
        {
            for (Player p : players) {
                if (p.getId() == currentPlayer) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param player_id
     * @param type
     * @return
     */
    public int getAmountCards(int player_id, int type) {
        return this.players.get(player_id).getCards(type);
    }

    /**
     * Determinate who is the holder of the knightforce card that gives 2 extra points.
     *
     */
    public void determineKnightForceHolder() {
        if (!this.getPlayers().isEmpty()) {
            // First we set our first player as biggest knightforce
            Player max = this.getPlayers().get(0);
            if (max != null) {
                max.setBiggestKnightForce(true);

                // now we check every other player, if another player has a bigger knightforce we make sure we set the properties for both player right
                for (Player p : this.getPlayers()) {
                    if (p.getKnightforce() > max.getKnightforce()) {
                        p.setBiggestKnightForce(true);
                        max.setBiggestKnightForce(false);
                        max = p;
                    }
                }
            }

            // In the end we check if this knightforce is bigger or equal to 3, if not, we dont have a holder of this card
            if (max != null && max.getKnightforce() < 3) {
                // No one deserves 2 extra points, their is no one who has the biggest knight force card!
                max.setBiggestKnightForce(false);
            } else {
                // Since this player has the biggest knight force now, we recalculate to make sure he gets his extra points!
                max.recalculatePoints();
            }
        }
    }

    /**
     *
     */
    public void endTurn() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param my_id
     */
    public void endTurn(int my_id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    public void generateDevelopmentCards() {
        this.developmentCards = new ArrayList<DevelopmentCard>();
        for (int i = 0; i < 15; i++) {
            developmentCards.add(new DevelopmentCard(DevelopmentCardType.KNIGHT));
        }
        for (int i = 0; i < 5; i++) {
            developmentCards.add(new DevelopmentCard(DevelopmentCardType.UNIVERSITY));
        }

    }
}
