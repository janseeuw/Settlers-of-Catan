package be.hogent.team10.catan.client.view;

/*
 * merge : merging.
 */
import be.hogent.team10.catan.client.controller.GameController;
import be.hogent.team10.catan.client.util.AudioStream;
import be.hogent.team10.catan.client.view.viewComponents.*;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.Tile;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.gameState.InitializationState;
import be.hogent.team10.catan_businesslogic.model.gameState.PlayGameState;
import be.hogent.team10.catan_businesslogic.model.gameState.RobberState;
import be.hogent.team10.catan_businesslogic.model.gameState.SetupState;
import be.hogent.team10.catan_businesslogic.util.Dice;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Brenden Deze klasse is een facade van de GUI. Alle aanroepen passeren hier, hierdoor verloopt alle
 * communicatie naar het domein via deze controller. Ook enkele gui specifiele attributen en methoden kunnen vanaf hier
 * gemakkelijk aangeroepen worden. De meeste methoden spreken voor zich.
 */
public class GuiController implements Observer {

    private GameController gameController;
    private int WIDTH = 1280;
    private int HEIGHT = 820;
    private SidePanel sidePanel;
    private PlayerResourcePanel resourcePanel;
    private BoardPanel board;
    public String selectedTileType = "NONE";
    private boolean buildSettlement = false;
    private boolean buildStreet = false;
    private CatanFrame catan = null;
    private boolean erasor = false;
    //singleton
    private static GuiController instance;
    private String setupElement = "NONE";
    private ComponentDetailPanel lastDetailPanel;
    private ErrorPanel errorPanel = ErrorPanel.getInstance();
    private BuildPanel buildPanel;
    private ResourceBundle resolution;
    private String[] options={"AUtomatisch",">1280x720","1280x720","800x600"};

    private GuiController() {
        System.out.println("CREATING WINDOW");
        int option=JOptionPane.showOptionDialog(null, "Kies een resolutie", "Kolonisten van Catan",
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
            null, options, options[0]);
        switch(option){
            case 0: try {
                     determineResolution();
                    } catch (Exception e) {
                        }
                    break;
            case 1: setResolution(1920);break;
            case 2: setResolution(1280);break;
            case 3: setResolution(800);break;
        }
        
        WIDTH = getSize("GCw");
        HEIGHT = getSize("GCh");
        sidePanel = new SidePanel(this);
        System.out.println("panel OK");
        resourcePanel = new PlayerResourcePanel(this);
        System.out.println("resourcepanel ok OK");
        //Aantal ringen van het bord is standaard 4
        //Set layout from JFrame
    }

    public void determineResolution() {
        Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
        if (res.getWidth() < 1200) {
            setResolution(800);
        } else if (res.getWidth() > 1200 && res.getHeight() < 799) {
            setResolution(1280);
        } else {
            setResolution(1920);
        }
    }

    public void setGameController(GameController gc) {
        this.gameController = gc;
        System.out.println(getClientNumber());
        if (getClientNumber() != 0) {
            getClient().addObserver(resourcePanel);
            addObserver(resourcePanel);
        }

        System.out.println("ok, na nodes on top");
        gameController.addObserver(this);
        gameController.getGame().getGameState().addObserver(this);

        System.out.println("ok, na nodes on top3");
        int numberOfRings = 4;
        int streetWidth = 8;
        int lengthOfSideHex = getSize("hex");
        board = new BoardPanel(numberOfRings, streetWidth, lengthOfSideHex, this);

        System.out.println("ok, na nodes on top99");
        this.setNodesOnTop();
    }

    public void setResolution(int width) {
        String w = null;
        String h = null;
        switch (width) {
            case 800:
                w = "800";
                h = "600";
                break;
            case 1280:
                w = "1280";
                h = "720";
                break;
            case 1920:
                w = "1920";
                h = "1080";
                break;
        }
        Locale currentLocale;

        currentLocale = new Locale(w, h);
        resolution = ResourceBundle.getBundle("SizesBundle", currentLocale);

    }
    //deze methode zal de property file aanspreken voor de juiste size te achterhalen

    public int getSize(String key) {
        try {

            return Integer.parseInt(resolution.getString(key));
        } catch (Exception e) {
        }
        return 0;
    }

    public void setFrame(CatanFrame c) {
        catan = c;
    }

    public static GuiController getInstance() {
        if (GuiController.instance == null) {
            GuiController.instance = new GuiController();
        }
        return instance;
    }

    public void notifyTileClicked(Coordinate coordinate, boolean rightMouse, Hexagon source) {
        // getdetails
        if (rightMouse) {
            try {
                if (lastDetailPanel != null) {
                    lastDetailPanel.setVisible(false);
                    lastDetailPanel = null;
                }
                Map<String, String> data = gameController.getTileDetails(coordinate);
                //tile is not yet initialized.
                if (data != null) {
                    lastDetailPanel = new ComponentDetailPanel(true);
                    lastDetailPanel.showData(data);
                    source.showDetails(lastDetailPanel);
                }
            } catch (Exception ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
    }

    public void notifyNodeClicked(Coordinate coordinate, boolean rightMouse, Node source) {        // getdetails
        if (rightMouse) {
            try {
                if (lastDetailPanel != null) {
                    lastDetailPanel.setVisible(false);
                    lastDetailPanel = null;
                }
                Map<String, String> data = gameController.getNodeDetails(coordinate);
                //tile is not yet initialized.
                if (data != null) {
                    lastDetailPanel = new ComponentDetailPanel(true);
                    lastDetailPanel.showData(data);
                    source.showDetails(lastDetailPanel);
                }
            } catch (Exception ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } //if a building is selected -> player wants to build or upgrade a city
        else if (setupElement.equals("SETTLEMENT")) {
            try {
                gameController.buildSettlement(coordinate);
                AudioStream.getInstance().playVillage();
            } catch (Exception ex) {
                ex.printStackTrace();
                //JOptionPane.showMessageDialog(null, ex.getMessage());
                errorPanel.setError(ex.getMessage());
            }
        } else if (setupElement.equals("CITY")) {
            try {
                gameController.buildCity(coordinate);
                AudioStream.getInstance().playCity();
            } catch (Exception ex) {
                ex.printStackTrace();
                //JOptionPane.showMessageDialog(null, ex.getMessage());
                errorPanel.setError(ex.getMessage());
            }
        }
    }

    public void notifyStreetClicked(Coordinate coordinate, boolean rightMouse, Street source) {
        if (rightMouse) {
            try {
                if (lastDetailPanel != null) {
                    lastDetailPanel.setVisible(false);
                    lastDetailPanel = null;
                }
                Map<String, String> data = gameController.getBorderDetails(coordinate);
                //tile is not yet initialized.
                if (data != null) {
                    lastDetailPanel = new ComponentDetailPanel(true);
                    lastDetailPanel.showData(data);
                    source.showDetails(lastDetailPanel);
                }
            } catch (Exception ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        } //if a street is selected -> player wants to build a streetr
        else if (setupElement.equals("STREET")) {
            try {
                gameController.buildStreet(coordinate);
                AudioStream.getInstance().playRoad();
            } catch (Exception ex) {
                ex.printStackTrace();
                //JOptionPane.showMessageDialog(null, ex.getMessage());
                System.out.println(errorPanel.getSize());
                errorPanel.setError(ex.getMessage());
                errorPanel.revalidate();
            }
        }
    }
    //enkele getters en setters --------------------------------------------------------------------------------

    public GameController getGameController() {
        return gameController;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public BoardPanel getBoard() {
        return board;
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public PlayerResourcePanel getResourcePanel() {
        return resourcePanel;
    }

    public Observable getBorder(Coordinate borderCoordinate) {
        try {
            return ((Observable) gameController.getBoard().getBorder(borderCoordinate));
        } catch (Exception ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Observable getNode(Coordinate nodeCoordinate) {
        return gameController.getBoard().getNode(nodeCoordinate);

    }

    public Observable getTile(Coordinate c) {
        return gameController.getTiles().get(c);
    }

    public void setSelectedTileType(String ttype) {
        selectedTileType = ttype;
    }

    public String getSelectedTileType() {
        return selectedTileType;
    }

    public void setErasor(boolean erase) {
        erasor = erase;
    }

    public ResourceSet getPlayerResourceSet(int p) throws Exception {
        // only 1 player available in single player.
        return gameController.getPlayerResourceSet(p);
    }

    public Map<Resource, Integer> getAvailableResourceTiles() {
        return null;
    }

    public String getTileType(Coordinate tileCoordinate) throws Exception {
        return gameController.getTileType(tileCoordinate);
    }

    public Player getCurrentPlayer() {
        return gameController.getCurrentPlayer();
    }

    public int getPoints(Player player) {
        return 0;
    }

    public int getKnightforce(Player player) {
        return 0;
    }

    public int getAvailableCities(Player player) {
        return gameController.getAvailableCities(player);
    }

    public int getAvailableSettlements(Player player) {
        return gameController.getAvailableSettlements(player);
    }

    public int getAvailableStreets(Player player) {
        return gameController.getAvailableStreets(player);
    }

    public int getDiceValue() {
        return gameController.getDiceValue();
    }

    public Map<Coordinate, Tile> getTiles() {
        return gameController.getTiles();
    }

    public GameState getGameState() {
        return gameController.getGameState();
    }

    public void initializeValueChips() {
        board.initializeValueChips();
    }

    public void removeListenersStreets() {
        board.removeMouseListenerForStreets();
    }

    public void removeListenersNodes() {
        board.removeMouseListenerForNodes();
    }

    public void addListenersNodes() {
        board.addMouseListenerForNodes();
    }

    public void addListenersStreets() {
        board.addMouseListenerForStreets();
    }

    public void setNodesOnTop() {
        board.setNodesOnTop();
    }

    public void setStreetsOnTop() {
        board.setStreetsOnTop();
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void buildNode(Coordinate coordinate) {
        try {
            if (setupElement.equals("SETTLEMENT")) {
                //gameController.buildSettlement(coordinate, player, this.setupElement);
                gameController.buildSettlement(coordinate);
            } else {
                gameController.buildCity(coordinate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorPanel.setError(ex.getMessage());
        }
    }

    public void buildStreet(Coordinate coordinate) {
        try {
            if (setupElement.equals("STREET")) {
                gameController.buildStreet(coordinate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorPanel.setError(ex.getMessage());
        }
    }

    public void buyDevelopmentCard() {
        try {
            gameController.buyDevelopmentCard();
        } catch (Exception ex) {
            errorPanel.setError(ex.getMessage());
        }
    }

    public void playDevelopmentCard(int type) {
        try {
            gameController.playDevelopmentCard(type);
        } catch (Exception ex) {
            errorPanel.setError(ex.getMessage());
        }
    }

    public void setSelectedSetupElement(String setupElement) {
        this.setupElement = setupElement;
    }

    public Map<String, Integer> getBankResources() {
        return this.gameController.getBankResources();
    }

    //Observer
    public void update(Observable o, Object o1) {
        /*
         * avoid nullpointer during initialization.
         */
        if (o instanceof InitializationState && catan != null) {
            catan.prepareInitializationState();
        } else if (o instanceof SetupState && getClientNumber() != 0) {
            sidePanel.prepareSetupState(new PlaceComponentsPanel(this));
        } else if ((o instanceof PlayGameState || o instanceof SetupState) && getClientNumber() != 0) {
            if (buildPanel == null) {
                try {
                    Player p = gameController.getClient();
                    buildPanel = new BuildPanel(this);
                    p.addObserver(buildPanel);
                } catch (ObjectNotFoundException ex) {
                    Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            sidePanel.preparePlayState(buildPanel);

        } else if (o instanceof RobberState) {
            if (board != null) {
                if (myTurn()) {
                    enableRobberState();
                    errorPanel.setError("Verplaats de rover!");
                }
            }

        } else if (o instanceof Dice) {
            flicker(((Dice) o).getValue());
        }
    }

    public List<String> getTradeableBankResources(String send, int quantity) {
        return gameController.getTradeableResources(send, quantity);
    }

    public void tradeBank(String send, String receive, int quantity) {
        try {
            gameController.tradeResource(gameController.getClient(), send, receive, quantity);
            AudioStream.getInstance().playTrade();
        } catch (Exception ex) {
            errorPanel.setError(ex.getMessage());
        }
    }

    public void rollDice() {
        try {
            gameController.rollDice();
        } catch (Exception ex) {
            errorPanel.setError(ex.getMessage());
        }
    }

    public boolean checkEnoughResourcesFor(String component) {
        return gameController.checkEnoughResourcesFor(component);
    }
    //makes hexagons clickable to move robber

    public void enableRobberState() {
        for (Hexagon hex : board.getHexagons()) {
            if (hex.getNumber() >= 2) {
                hex.enableRobberState();
            }
        }
    }
    //makes hexagons not clickable

    public void disableRobberState() {

        for (Hexagon hex : board.getHexagons()) {
            hex.disableRobberState();
        }
    }
    //moves the robber

    public void moveRobber(Coordinate c) {
        try {
            gameController.moveRobber(c);
        } catch (ObjectNotFoundException ex) {
            errorPanel.setError(ex.getMessage());
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            errorPanel.setError(ex.getMessage());
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//highlights the resource number that is rolled
    public void flicker(int n) {
        for (Hexagon hex : board.getHexagons()) {
            if (hex.getNumber() == n && !hex.getHasRobber()) {
                hex.flicker();
            }
        }
    }

    //makes nodes clickable to steal when robber is moved
    public void stealWhenRobber(Coordinate c) {
        if (getClient() == getCurrentPlayer()) {
            setNodesOnTop();
            try {
                List<Coordinate> stealableNodes = gameController.getSurroundingTakenNodes(c);
                if (!stealableNodes.isEmpty()) {
                    for (int i = 0; i < stealableNodes.size(); i++) {
                        for (Node n : board.getNodes()) {
                            if (n.getCoordinate().getX() == stealableNodes.get(i).getX() && n.getCoordinate().getY() == stealableNodes.get(i).getY()) {
                                n.setRobberState();
                            }
                        }

                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    //makes nodes unclickable

    public void disableStealWhenRobber() {
        for (Node n : board.getNodes()) {
            n.endRobberState();
        }
    }
    //steals the card from selected owner of node

    public void stealResource(Coordinate c) throws Exception {
        try {
            System.out.println("STEALING A CARD!!");
            Player sender = gameController.getNodes().get(c).getOwner();
            gameController.takeCardFromPlayer(gameController.getClient(), sender);

        } catch (ObjectNotFoundException ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void endTurn() {
        gameController.endTurn();
    }

    public Player getClient() {
        try {
            return gameController.getClient();
        } catch (ObjectNotFoundException ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getClientNumber() {
        return gameController.getClientNumber();
    }

    public void addObserver(Observer o) {
        gameController.addObserver(o);
    }

    public List<Player> getPlayers() {
        return gameController.getPlayers();
    }

    //this method has to be called when player 0 presses the starts game button
    public void enableBuilding() {
        this.getSidePanel().getPlayerCompPanel().enableBuilding();
    }

    public int getCreator() {
        return gameController.getCreator();
    }

    public void startGame() {
        gameController.startGame();
    }

    public boolean myTurn() {
        return gameController.myTurn();
    }

    public Dice getDice() {
        return gameController.getGame().getDice();
    }

    public void trade(Player tradeFromPlayer, Player tradeWithPlayer, ResourceSet rs) {
        try {
            gameController.trade(tradeFromPlayer, tradeWithPlayer, rs);
        } catch (Exception ex) {
            errorPanel.setError(ex.getMessage());
        }
    }

    public void addTradeFrame() {
        TradeFrame frame = TradeFrame.getInstance();
        //Nog variabel maken aan resolutie
        frame.setSize(new Dimension(500, 400));
        frame.toFront();
    }

    public void askForTradeAccept(ResourceSet offer, ResourceSet reward, Player offerPlayer) {
        AcceptTradeFrame af = new AcceptTradeFrame();
        af.setSize(new Dimension(500, 400));
        af.setPlayerNameText(offerPlayer.getName());
        //Wat de andere speler weggeeft (negatief) moet voor deze speler aangeboden worden (positief) vandaar *(-1)
        af.setBrickQuantity(offer.getResources().get("BRICK") - reward.getResources().get("BRICK"));
        af.setLumberQuantity(offer.getResources().get("LUMBER") - reward.getResources().get("LUMBER"));
        af.setWoolQuantity(offer.getResources().get("WOOL") - reward.getResources().get("WOOL"));
        af.setOreQuantity(offer.getResources().get("ORE") - reward.getResources().get("ORE"));
        af.setGrainQuantity(offer.getResources().get("GRAIN") - reward.getResources().get("GRAIN"));
        af.toFront();
    }

    public void replyTrade(boolean accepted) {
        try {
            if (accepted) {
                gameController.replyTrade(accepted);
            }
        } catch (Exception ex) {
            errorPanel.setError(ex.getMessage());
        }
    }

    /**
     * This method is launched when a player presses the knight button in build panel.
     */
    public void playKnight() {
        try {
            gameController.playDevelopmentCard(0);
            AudioStream.getInstance().playKnight();
            this.errorPanel.setError("Er wordt een ridderkaart uitgespeeld!");
        } catch (Exception ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is launched when a player presses the university button in the build panel.
     */
    public void playUniversity() {
        try {
            gameController.playDevelopmentCard(1);
            AudioStream.getInstance().playUni();
            this.errorPanel.setError("Er wordt een overwinningspunt uitgespeeld!");
        } catch (Exception ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
