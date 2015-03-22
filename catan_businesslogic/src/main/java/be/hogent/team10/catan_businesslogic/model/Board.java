package be.hogent.team10.catan_businesslogic.model;

import static be.hogent.team10.catan_businesslogic.model.TileType.DESERT;
import static be.hogent.team10.catan_businesslogic.model.TileType.RESOURCE;
import static be.hogent.team10.catan_businesslogic.model.TileType.SEA;
import be.hogent.team10.catan_businesslogic.model.exception.CannotBuildException;
import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.gameState.PlayGameState;
import be.hogent.team10.catan_businesslogic.model.gameState.RobberState;
import be.hogent.team10.catan_businesslogic.model.gameState.SetupState;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.SeaTileBehaviour;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import be.hogent.team10.catan_businesslogic.util.Observable;
import java.util.*;

/**
 *
 * @author HP
 */
public class Board extends Observable {

    private Map<Coordinate, Node> nodes;
    private Map<Coordinate, Border> borders;
    private Map<Coordinate, Tile> tiles;
    // A list with available nodes to build settlements. (Distance rule)
    private Map<Coordinate, Node> availableBuildingSpots;
    private Map<Coordinate, Border> availableBorders;
    // Available Tiles for each Type & Resource
    private Map<String, Integer> availableTiles;
    // Numbers used on the board
    private List<Integer> numbers;
    // Coordinate where the robber is
    private Coordinate robber;
    // List of development cards;
    private List<DevelopmentCard> developmentCards;
    /*
     * Constructor
     */

    public Board() {
        this.borders = new HashMap<Coordinate, Border>();
        this.nodes = new HashMap<Coordinate, Node>();
        this.tiles = new HashMap<Coordinate, Tile>();
        this.availableBuildingSpots = new HashMap<Coordinate, Node>();
        this.availableBorders = new HashMap<Coordinate, Border>();
        this.availableTiles = new HashMap<String, Integer>();
        this.developmentCards = new ArrayList<DevelopmentCard>();
    }

    public Map<Coordinate, Border> getAvailableBorders() {
        return this.availableBorders;
    }

    /**
     *
     * @return how many tiles available to place for each type
     */
    public Map<String, Integer> getTilesToPlace() {
        return this.availableTiles;
    }

    /**
     * @return the nodes
     */
    public Map<Coordinate, Node> getNodes() {
        return nodes;
    }

    /**
     * @return the borders
     */
    public Map<Coordinate, Border> getBorders() {
        return borders;
    }

    public boolean getAllTilesPlaced() {
        return this.availableTiles.isEmpty();
    }

    /**
     * @param tileCoordinate
     * @return the Borders around the tile.
     */
    public List<Border> getSurroundingBorders(Coordinate tileCoordinate) throws Exception {
        if (!tiles.containsKey(tileCoordinate)) {
            throw new Exception("non-Land tile chosen");
        }
        List<Border> results = new ArrayList<Border>();

        int[][] addendum = new int[][]{{-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, 1}, {1, 0}};
        for (int row = 0; row < addendum.length; row++) {
            results.add(borders.get(new Coordinate(tileCoordinate.getX() + addendum[row][0], tileCoordinate.getY() + addendum[row][1])));

        }
        return results;
    }

    /**
     * @param tileCoordinate
     * @return the Borders around the tile.
     */
    public List<Tile> getSurroundingTiles(Coordinate tileCoordinate) throws Exception {
        if (!tiles.containsKey(tileCoordinate)) {
            throw new Exception("Tile not found");
        }
        List<Tile> results = new ArrayList<Tile>();

        int[][] addendum = new int[][]{{-2, -2}, {-2, 0}, {0, 2}, {2, 2}, {2, 0}, {0, -2}};
        for (int row = 0; row < addendum.length; row++) {
            Tile t = tiles.get(new Coordinate(tileCoordinate.getX() + addendum[row][0], tileCoordinate.getY() + addendum[row][1]));
            if (t != null) {
                results.add(t);
            }
        }
        return results;
    }

    /**
     * @param tileCoordinate
     * @return the Borders around the tile.
     */
    public List<Node> getSurroundingNodes(Coordinate tileCoordinate) throws Exception {

        if (!tiles.containsKey(tileCoordinate)) {
            throw new Exception("non-Land tile chosen");
        }
        // we werken met random-access -> arraylist.
        List<Node> results = new ArrayList<Node>();
        int[][] addendum = new int[][]{{0, 1}, {1, 2}, {-1, 0}};
        int indexX = 0;
        int indexY = 1;
        for (int i = 0; i < 2; i++) {
            for (int row = 0; row < addendum.length; row++) {
                results.add(nodes.get(new Coordinate(tileCoordinate.getX() + addendum[row][indexX], tileCoordinate.getY() + addendum[row][indexY])));
            }
            indexX = 1;
            indexY = 0;
        }
        return results;
    }

    /**
     * Important method for the distance rule.
     *
     * @param Coordinate nodeCoordinate
     * @return the Neighbournodes around the node.
     */
    public List<Node> getNeighbourNodes(Coordinate nodeCoordinate) throws Exception {
        if (!this.nodes.containsKey(nodeCoordinate)) {
            throw new Exception("The chosen component is not a node");
        }
        List<Node> results = new ArrayList<Node>();
        // [Even, Odd] or [Odd, Even]
        int[][] addendum;
        if (nodeCoordinate.getX() % 2 == 0) {
            addendum = new int[][]{{-1, -1}, {1, 1}, {1, -1}};
        } else {
            addendum = new int[][]{{-1, -1}, {-1, 1}, {1, 1}};
        }

        for (int row = 0; row < addendum.length; row++) {
            results.add(nodes.get(new Coordinate(nodeCoordinate.getX() + addendum[row][0], nodeCoordinate.getY() + addendum[row][1])));
        }
        return results;
    }

    /**
     *
     * @param nodeCoordinate
     * @return The borders around a particular node
     * @throws Exception
     */
    public List<Border> getNeighbourBorders(Coordinate nodeCoordinate) throws Exception {
        if (!this.nodes.containsKey(nodeCoordinate)) {
            throw new Exception("The chosen component is not a node");
        }
        List<Border> results = new ArrayList<Border>();
        int[][] addendum;
        if (nodeCoordinate.getX() % 2 == 0) {
            addendum = new int[][]{{-1, -1}, {0, 0}, {0, -1}};
        } else {
            addendum = new int[][]{{-1, -1}, {-1, 0}, {0, 0}};
        }

        for (int row = 0; row < addendum.length; row++) {
            results.add(borders.get(new Coordinate(nodeCoordinate.getX() + addendum[row][0], nodeCoordinate.getY() + addendum[row][1])));
        }

        return results;
    }

    /**
     *
     * @param borderCoordinate
     * @return List of nodes around a particular border
     */
    public List<Node> getNeighbourNodesFromBorder(Coordinate borderCoordinate) throws Exception {
        if (!this.borders.containsKey(borderCoordinate)) {
            throw new Exception("The chosen component is not a border");
        }
        List<Node> results = new ArrayList<Node>();
        int[][] addendum;
        if (borderCoordinate.getX() % 2 == 0) {
            if (borderCoordinate.getY() % 2 == 0) {
                addendum = new int[][]{{0, 1}, {1, 0}};
            } else {
                addendum = new int[][]{{0, 0}, {1, 1}};
            }
        } else {
            addendum = new int[][]{{0, 0}, {1, 1}};
        }
        for (int row = 0; row < addendum.length; row++) {
            Node n = nodes.get(new Coordinate(borderCoordinate.getX() + addendum[row][0], borderCoordinate.getY() + addendum[row][1]));
            if (n != null) {
                results.add(n);
            }
        }

        return results;
    }

    public List<Border> getNeighbourBorderFromBorder(Coordinate borderCoordinate) throws Exception {
        if (!this.borders.containsKey(borderCoordinate)) {
            throw new Exception("The chosen component is not a border");
        }
        List<Border> results = new ArrayList<Border>();
        int[][] addendum;
        if (borderCoordinate.getX() % 2 == 0) {
            if (borderCoordinate.getY() % 2 == 0) {
                addendum = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
            } else {
                addendum = new int[][]{{-1, -1}, {0, 1}, {1, 1}, {0, -1}};
            }
        } else {
            addendum = new int[][]{{-1, -1}, {-1, 0}, {1, 1}, {1, 0}};
        }
        for (int row = 0; row < addendum.length; row++) {
            results.add(borders.get(new Coordinate(borderCoordinate.getX() + addendum[row][0], borderCoordinate.getY() + addendum[row][1])));
        }

        return results;
    }

    /**
     * @param tilenumber number, assigned by player during initialization of the
     * game
     * @return tile
     * @exception when no tile can be found that matches the number, an
     * exception is thrown. Either the initialization of the game has not yet
     * ended, or an internal error occured.
     */
    public List<Tile> getTilesByNumber(int tileNumber) throws Exception {
        List<Tile> result = new ArrayList<Tile>();
        for (Map.Entry<Coordinate, Tile> entry : tiles.entrySet()) {
            Tile tile = entry.getValue();

            if (tile.getNumber() == tileNumber) {
                result.add(tile);
            }
        }
        if (!result.isEmpty()) {
            return result;
        } else {
            throw new Exception("Tile not found");
        }
    }

    /**
     * @param coordinate
     * @return
     * @throws Exception
     */
    public Tile getTile(Coordinate coordinate) throws Exception {
        Tile tile = tiles.get(coordinate);
        if (tile != null) {
            return tile;
        } else {
            throw new Exception("Tile not found");
        }
    }

    /**
     * @param coordinate
     * @return
     * @throws Exception
     */
    public Border getBorder(Coordinate coordinate) throws Exception {
        Border border = borders.get(coordinate);
        if (border != null) {
            return border;
        } else {
            throw new Exception("Border not found");
        }
    }

    /**
     * @return the robber
     */
    public Coordinate getRobber() {
        return robber;
    }

    public Node getNode(Coordinate coordinate) {
        return nodes.get(coordinate);
    }

    /**
     * @return the tiles
     */
    public Map<Coordinate, Tile> getTiles() {
        return tiles;
    }

    /**
     * @return the availableTiles
     */
    public Map<String, Integer> getAvailableTiles() {
        return availableTiles;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(Map<Coordinate, Node> nodes) {
        this.nodes = nodes;
        setChanged();
        notifyObservers();
    }

    /**
     * @param tiles the tiles to set
     */
    public void setTiles(Map<Coordinate, Tile> tiles) {
        this.tiles = tiles;
        setChanged();
        notifyObservers();
    }

    /**
     * @param borders the borders to set
     */
    public void setBorders(Map<Coordinate, Border> borders) {
        this.borders = borders;
        setChanged();
        notifyObservers();
    }

    public void setAvailableTiles(Map<String, Integer> tiles) {
        this.availableTiles = tiles;
    }

    public void setTilesToPlace(Map<String, Integer> availableTiles) {
        this.availableTiles = availableTiles;
    }

    public void setRobber(Coordinate c) {
        this.robber = c;
    }

    /**
     * Initializes the 4-player standard board (Tiles aren't placed yet)
     */
    public void init() throws Exception {
        //flush all data.
        this.borders = new HashMap<Coordinate, Border>();
        this.nodes = new HashMap<Coordinate, Node>();
        this.tiles = new HashMap<Coordinate, Tile>();
        init(7);
        this.availableTiles.put("SEA", 18);
        this.availableTiles.put("DESERT", 1);
        this.availableTiles.put("BRICK", 3);
        this.availableTiles.put("LUMBER", 4);
        this.availableTiles.put("WOOL", 4);
        this.availableTiles.put("GRAIN", 4);
        this.availableTiles.put("ORE", 3);

        numbers = new ArrayList<Integer>(Arrays.asList(5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11));
        this.surroundWithSeaTiles();
    }

    private void surroundWithSeaTiles() {
        Map<Coordinate, Tile> tiles = this.getTiles();
        /*
         * Override the outer tiles as Sea tile
         */

        for (Map.Entry<Coordinate, Tile> entry : tiles.entrySet()) {
            Tile t = entry.getValue();
            Coordinate c = t.getCoordinate();
            Tile checkMin = tiles.get(new Coordinate(c.getX(), c.getY() - 2));
            Tile checkMax = tiles.get(new Coordinate(c.getX(), c.getY() + 2));
            boolean extremeX = false;
            if (checkMin == null || checkMax == null) {
                extremeX = true;
            }
            checkMin = tiles.get(new Coordinate(c.getX() - 2, c.getY()));
            checkMax = tiles.get(new Coordinate(c.getX() + 2, c.getY()));
            if ((checkMin == null || checkMax == null) || extremeX) {
                try {
                    this.placeTile(t.getCoordinate(), TileType.SEA);
                    entry.setValue(t);
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * Initializes a board with chosen width
     *
     * @param numberOfTilesOnMiddleRow the width of the middle row
     */
    public void init(int numberOfTilesOnMiddleRow) {
        List<Tile> anonymousTiles = new ArrayList<Tile>();
        // middle row
        int x = 1;
        int y = 1;
        for (int i = 0; i < numberOfTilesOnMiddleRow; i++) {
            Tile t = new Tile(new Coordinate(x + i * 2, y + i * 2));
            anonymousTiles.add(t);
        }
        // other rows;
        for (int i = numberOfTilesOnMiddleRow - 1; i > (numberOfTilesOnMiddleRow - 1) / 2; i--) { //6 5 4
            for (int j = i; j > 0; j--) {// 6-5-4-3-2-1
                Tile tUp = new Tile(new Coordinate((((numberOfTilesOnMiddleRow * 2) - 1) - (((numberOfTilesOnMiddleRow) - j)) * 2), x + (j * 2) + (numberOfTilesOnMiddleRow - 1 - i) * 2));
                Tile tDown = new Tile(new Coordinate(y + (j * 2) + (numberOfTilesOnMiddleRow - 1 - i) * 2, (((numberOfTilesOnMiddleRow * 2) - 1) - (((numberOfTilesOnMiddleRow) - j)) * 2)));
                anonymousTiles.add(tUp);
                anonymousTiles.add(tDown);
            }
        }
        //put in map
        for (Tile t : anonymousTiles) {
            tiles.put(t.getCoordinate(), t);
        }
        //generate node list
        for (Tile t : anonymousTiles) {
            Coordinate tilecoord = t.getCoordinate();
            int[][] addendum = new int[][]{{0, 1}, {1, 2}, {-1, 0}, {1, 0}, {2, 1}, {0, -1}};

            for (int i = 0; i < 2; i++) {
                for (int row = 0; row < addendum.length; row++) {
                    Coordinate c = new Coordinate(tilecoord.getX() + addendum[row][0], tilecoord.getY() + addendum[row][y]);
                    Node n = new Node(c);
                    nodes.put(c, n);
                }
            }
        }
        //generate borders
        for (Tile t : anonymousTiles) {
            Coordinate tileCoordinate = t.getCoordinate();
            int[][] addendum = new int[][]{{-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, 1}, {1, 0}};

            for (int row = 0; row < addendum.length; row++) {
                Coordinate c = new Coordinate(tileCoordinate.getX() + addendum[row][0], tileCoordinate.getY() + addendum[row][1]);
                borders.put(c, new Border(c));
            }
        }

        for (int i = 1; i <= 15; i++) {
            DevelopmentCard d = new DevelopmentCard();
            d.setDevelopmentCardType(DevelopmentCardType.KNIGHT);
            this.developmentCards.add(d);
        }

        for (int i = 1; i <= 4; i++) {
            DevelopmentCard d = new DevelopmentCard();
            d.setDevelopmentCardType(DevelopmentCardType.UNIVERSITY);
            this.developmentCards.add(d);
        }
    }

    /**
     * Places a tile on the board
     *
     * @param tileCoordinate
     * @param type SEA, DESERT or RESOURCE
     * @throws Exception
     */
    public void placeTile(Coordinate tileCoordinate, TileType type) throws Exception {
        Tile tile = tiles.get(tileCoordinate);
        if (tile != null) {
            // check if tiles available
            if (this.getAvailableTiles().get(type.name()) > 0) {
                // check if there's already a tile
                if (tile.getTileType() != null) {
                    this.removeOldTile(tile.getCoordinate());
                }
                tile.setTileType(type);
                this.getAvailableTiles().put(type.name(), this.getAvailableTiles().get(type.name()) - 1);
            } else {
                throw new Exception("Type not available");
            }
        } else {
            throw new Exception("Tile not found.");
        }
    }

    /**
     * Places a tile on the board
     *
     * @param tileCoordinate
     * @param resource BRICK, LUMBER, WOOL, GRAIN, ORE
     * @throws Exception
     */
    public void placeTile(Coordinate tileCoordinate, Resource resource) throws Exception {
        Tile tile = tiles.get(tileCoordinate);
        if (tile != null) {
            // When assigning a Resource it is implicit a Resource TileType
            // Check if resourceTiles available
            if (this.getAvailableTiles().get(resource.name()) > 0) {
                // Check if tile defined
                if (tile.getTileType() != null) {
                    removeOldTile(tile.getCoordinate());
                }
                tile.setTileType(TileType.RESOURCE);
                tile.setResource(resource);
                this.getAvailableTiles().put(resource.name(), this.getAvailableTiles().get(resource.name()) - 1);
                addSurroundingNodesToAvailableBuildingSpots(tile);

                addSurroundingBordersToAvailableBorders(tile);

            } else {
                throw new Exception("Resource not available");
            }
        } else {
            throw new Exception("Tile not found.");
        }
    }

    /**
     * For a particular land tile, all the nodes are added to the list of
     * available building spots.
     *
     * @param tile
     * @throws Exception
     */
    private void addSurroundingNodesToAvailableBuildingSpots(Tile tile) throws Exception {
        List<Node> surroundingNodes = getSurroundingNodes(tile.getCoordinate());
        for (Node surroundingNode : surroundingNodes) {
            availableBuildingSpots.put(surroundingNode.getCoordinate(), surroundingNode);
        }
    }

    private void addSurroundingBordersToAvailableBorders(Tile tile) throws Exception {
        List<Border> surroundingBorders = this.getSurroundingBorders(tile.getCoordinate());
        for (Border surroundingBorder : surroundingBorders) {
            availableBorders.put(surroundingBorder.getCoordinate(), surroundingBorder);
        }
    }

    /**
     * Build a house on a particular coordinate
     *
     * @param nodeCoodinate the coordinate of thenode where a player wants to
     * build a house/city
     * @param player the player that wants to build a house/city
     */
    public void buildSettlement(Coordinate nodeCoordinate, Player player, GameState gameState) throws CannotBuildException, Exception {
        Node newNode = this.getNode(nodeCoordinate);
        if (newNode.getOwner() != null) {
            throw new CannotBuildException("Er staat al een nederzetting op deze plaats.");
        }
        List<Node> surroundingNodes = getNeighbourNodes(nodeCoordinate);
        /*
         * check if node is too close to other cities/villages
         */
        for (Node node : surroundingNodes) {
            if (node.getBuildingtype() != BuildingType.NOBUILDING || node.getOwner() != null) {
                throw new CannotBuildException("Er moet minimuum 1 onbezet kruispunt tussen de omringende steden liggen.");
            }
        } // Foute foutmelding
        List<Border> surroundingBorders = getNeighbourBorders(nodeCoordinate);
        /*
         * check if either a street is connected to this buildingspot or the game is in the setupstate (which allows
         * users to place settlements freely).
         */
        boolean maybuild = false;
        if (gameState instanceof SetupState) {
            maybuild = true;
        }

        for (Border b : surroundingBorders) {
            if (b.getOwner() != null && b.getOwner().equals(player)) {
                maybuild = true;
            }

        }//
        if (maybuild) {
            newNode.setOwner(player);
            newNode.setBuildingtype(BuildingType.SETTLEMENT);

        } else {
            throw new CannotBuildException("U bezit geen weg naar deze bouwplaats.");
        }
    }

    /**
     *
     * @param nodeCoordinate
     * @param player
     * @throws Exception
     */
    public void buildCity(Coordinate nodeCoordinate, Player player) throws CannotBuildException {
        Node node = this.nodes.get(nodeCoordinate);
        if (node.getOwner() == null) {
            throw new CannotBuildException("Je moet eerst een nederzetting bouwen alvorens deze up te graden.");
        }
        if (node.getOwner().equals(player)) {
            if (node.getBuildingtype() != null && node.getBuildingtype() == BuildingType.CITY) {
                throw new CannotBuildException("Je kan een stad niet opnieuw upgraden.");
            }
            node.setBuildingtype(BuildingType.CITY);
        } else {
            throw new CannotBuildException("Dit is niet jouw stad.");
        }
    }

    /**
     *
     * Sets the numbers in spiral-way starting at given startcoordinate
     *
     * @param start
     * @param clockwise
     */
    @SuppressWarnings("empty-statement")
    public void setNumbers(Coordinate start, boolean clockwise) throws Exception {
        Tile t = tiles.get(start);
        // Check if it is a corner tile
        if (!getCornerTiles().contains(t)) {
            throw new Exception("Not a corner tile");
        }
        for (Map.Entry<Coordinate, Tile> entry : tiles.entrySet()) {
            Tile tile = entry.getValue();
            tile.removeNumber();
        }
        // Determine direction startvector
        Coordinate directionVector;
        // WEST
        if (tiles.get(new Coordinate(t.getCoordinate().getX() - 2, t.getCoordinate().getY() - 2)).getTileType() == TileType.SEA) {
            if (tiles.get(new Coordinate(t.getCoordinate().getX() - 2, t.getCoordinate().getY())).getTileType() == TileType.SEA) {
                if (tiles.get(new Coordinate(t.getCoordinate().getX(), t.getCoordinate().getY() + 2)).getTileType() == TileType.SEA) {
                    directionVector = new Coordinate(0, -2);
                } else {
                    directionVector = new Coordinate(+2, 0);
                }
            } else {
                directionVector = new Coordinate(+2, +2);
            }
        } else {
            //EAST
            if (tiles.get(new Coordinate(t.getCoordinate().getX(), t.getCoordinate().getY() + 2)).getTileType() == TileType.SEA) {
                if (tiles.get(new Coordinate(t.getCoordinate().getX() - 2, t.getCoordinate().getY())).getTileType() == TileType.SEA) {
                    directionVector = new Coordinate(-2, -2);
                } else {
                    directionVector = new Coordinate(-2, 0);
                }
            } else {
                directionVector = new Coordinate(0, +2);
            }
        }

        boolean dessertPassed = false;
        Stack<Integer> nrs = new Stack<Integer>();
        for (int i = numbers.size() - 1; i >= 0; i--) {
            nrs.push(numbers.get(i));
        }

        // 
        try {
            if (t.getTileType() != TileType.DESERT) {
                t.setNumber(nrs.lastElement());
                nrs.pop();
            } else {
                t.setNumber(0);
            }
        } catch (Exception ex) {
            dessertPassed = true;
        }
        while (!nrs.empty()) {
            // Check if next tile is sea tile or tile with already a number
            Tile hulp = tiles.get(new Coordinate(t.getCoordinate().getX() + directionVector.getX(), t.getCoordinate().getY() + directionVector.getY()));
            if (dessertPassed) {
                while (hulp.getTileType() == TileType.SEA || hulp.getNumber() != 0 || hulp.getTileType() == TileType.DESERT) {
                    // Change directionVector
                    if (directionVector.getX() == -2) {
                        if (directionVector.getY() == -2) {
                            directionVector.setX(0);
                            directionVector.setY(-2);
                        } else {
                            directionVector.setX(-2);
                            directionVector.setY(-2);
                        }
                    } else if (directionVector.getX() == 0) {
                        if (directionVector.getY() == -2) {
                            directionVector.setX(2);
                            directionVector.setY(0);
                        } else {
                            directionVector.setX(-2);
                            directionVector.setY(0);
                        }
                    } else {
                        if (directionVector.getY() == 0) {
                            directionVector.setX(2);
                            directionVector.setY(2);
                        } else {
                            directionVector.setX(0);
                            directionVector.setY(2);
                        }
                    }
                    hulp = tiles.get(new Coordinate(t.getCoordinate().getX() + directionVector.getX(), t.getCoordinate().getY() + directionVector.getY()));
                }
            } else {
                while (hulp.getTileType() == TileType.SEA || hulp.getNumber() != 0) {
                    // Change directionVector
                    if (directionVector.getX() == -2) {
                        if (directionVector.getY() == -2) {
                            directionVector.setX(0);
                            directionVector.setY(-2);
                        } else {
                            directionVector.setX(-2);
                            directionVector.setY(-2);
                        }
                    } else if (directionVector.getX() == 0) {
                        if (directionVector.getY() == -2) {
                            directionVector.setX(2);
                            directionVector.setY(0);
                        } else {
                            directionVector.setX(-2);
                            directionVector.setY(0);
                        }
                    } else {
                        if (directionVector.getY() == 0) {
                            directionVector.setX(2);
                            directionVector.setY(2);
                        } else {
                            directionVector.setX(0);
                            directionVector.setY(2);
                        }
                    }
                    hulp = tiles.get(new Coordinate(t.getCoordinate().getX() + directionVector.getX(), t.getCoordinate().getY() + directionVector.getY()));
                }
            }
            t = tiles.get(new Coordinate(t.getCoordinate().getX() + directionVector.getX(), t.getCoordinate().getY() + directionVector.getY()));
            try {
                if (t.getTileType() != TileType.DESERT) {
                    t.setNumber(nrs.lastElement());
                    nrs.pop();
                } else {
                    t.setNumber(0);
                }
            } catch (Exception ex) {
                dessertPassed = true;
            }
        }
    }

    /*
     * Returns the 6 land-tiles in the corner of the board @throws Exception @return Lists of tiles that are next to the
     * sea
     */
    public List<Tile> getCornerTiles() throws Exception {
        List<Tile> result = new ArrayList<Tile>();
        for (Map.Entry<Coordinate, Tile> entry : tiles.entrySet()) {
            Tile t = entry.getValue();
            // Only check land-tiles, check for land-tiles with 3 surrounding sea-tiles
            if (t.getTileType() != TileType.SEA) {
                List<Tile> neighbours = getSurroundingTiles(t.getCoordinate());
                int i = 0;
                for (Tile n : neighbours) {
                    if (n.getTileType() == TileType.SEA) {
                        i++;
                    }
                }
                if (i == 3) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    /**
     * Moves the robber to a particular coordinate
     *
     * @param coordinate
     * @throws Exception
     */
    public boolean moveRobber(Coordinate coordinate) throws Exception {
        Tile oldRobber = tiles.get(getRobber());
        //Check if robber already set on other tile
        if (getRobber() != null) {
            if (coordinate == oldRobber.getCoordinate()) {
                return false;
            }
            
            if (oldRobber != null) {
                oldRobber.setHasRobber(false);
            }
        }
        Tile newRobber = tiles.get(coordinate);
        if (newRobber != null) {
            newRobber.setHasRobber(true);
            // update boards robber coordinate
            setRobber(coordinate);
            return true;
        } else {
            throw new Exception("Tegel niet gevonden" + coordinate);
        }
    }

    /**
     * @return the availableBuildingSpots
     */
    public Map<Coordinate, Node> getAvailableBuildingSpots(int playerid) throws Exception {
        availableBuildingSpots.clear();
        for (Border b : borders.values()) {
            if (b.getOwner() != null && b.getOwner().getId() == playerid) {
                List<Node> nodes = getNeighbourNodesFromBorder(b.getCoordinate());
                if (nodes != null) {
                    boolean found = false;
                    for (Node n : nodes) {
                        if (n.getOwner() != null) {
                            found = true;
                        }
                    }
                    if (!found) {
                        for (Node n : nodes) {
                            availableBuildingSpots.put(n.getCoordinate(), n);
                        }
                    }
                }
            }
        }
        return availableBuildingSpots;
    }

    public Map<Coordinate, Border> getAvailableBorders(int playerid) throws Exception {
        availableBorders.clear();
        for (Border b : borders.values()) {
            if (b.getOwner() != null && b.getOwner().getId() == playerid) {
                List<Node> nodes = getNeighbourNodesFromBorder(b.getCoordinate());
                if (nodes != null) {
                    boolean found = false;
                    for (Node n : nodes) {
                        if (n.getOwner() != null && n.getOwner().getId() == playerid) {
                            availableBorders.put(b.getCoordinate(), b);
                        }
                    }
                    if (!found) {
                        for (Border b2 : getNeighbourBorderFromBorder(b.getCoordinate())) {
                            if (b2.getOwner() != null && b2.getOwner().getId() == playerid) {
                                availableBorders.put(b2.getCoordinate(), b2);
                            }
                        }
                    }
                }
            }
        }
        return availableBorders;
    }

    /**
     * Removes the tile from the board when setting a new tile on top of it.
     *
     * @param coordinate
     * @throws Exception
     */
    public void removeOldTile(Coordinate coordinate) throws Exception {
        Tile tile = tiles.get(coordinate);
        if (tile != null) {
            switch (tile.getTileType()) {
                case DESERT:
                    this.availableTiles.put(TileType.DESERT.name(), this.availableTiles.get(TileType.DESERT.name()) + 1);
                    break;
                case SEA:
                    this.availableTiles.put(TileType.SEA.name(), this.availableTiles.get(TileType.SEA.name()) + 1);
                    break;
                case RESOURCE:
                    this.availableTiles.put(tile.getResource().name(), this.availableTiles.get(tile.getResource().name()) + 1);
                    break;
                default:
                    throw new Exception("Error");
            }
        } else {
            throw new Exception("Tile not found");
        }
        //tile.clear();
    }

    /**
     * Build a street following the rules.
     *
     * @param borderCoordinate The coordinate of the border where a player wants
     * to build a street
     * @param player The player that tries to build a street
     */
    public void buildStreet(Coordinate borderCoordinate, Player player, GameState gameState) throws Exception {
        /*
         * check if the street already has an owner.
         */
        Border border = getBorder(borderCoordinate);
        if (border.getOwner() != null) {
            throw new CannotBuildException("Je kan geen straat bouwen op een bestaande straat");
        }
        
        
        /*
         * check if the street is next to one of the nodes the player built. -- setupstate
         */
        boolean maybuild = false;
        Node theOwnedNode = null;
        List<Node> surroundingNodes = getNeighbourNodesFromBorder(borderCoordinate);
        for (Node n : surroundingNodes) {
            if (n.getOwner() != null && n.getOwner().equals(player)) {
                maybuild = true;
                theOwnedNode = n;
            }
        }
        /*
         * check if either a street is connected to this nod or the game is in the setupstate (which allows users to
         * place streets freely[if next to his city]).
         */
        /*
         * initialize to prevent nullpointer later on.
         */
        List<Border> surroundingBorders = new ArrayList<Border>();
        if (gameState instanceof SetupState) {
            if (!maybuild) {
                throw new CannotBuildException("De straat moet grenzen aan uw laatst gebouwde nederzetting.");
            }
            for (Border b : getNeighbourBorders(theOwnedNode.getCoordinate())) {
                /*
                 * there may be no other roads leading to the city.
                 */
                if (b.getOwner() != null && b.getOwner().equals(player)) {
                    throw new CannotBuildException("De straat moet grenzen aan uw laatst gebouwde nederzetting.");
                }
            }
        } else {
            /*
             * Only if we are not in the setupstate, we must also look at other streets that would be connected to the
             * new street. making it a suitable building spot.
             */
            surroundingBorders = getNeighbourBorderFromBorder(borderCoordinate);
            maybuild = false;
        }
        /*
         * every possibility of game_State has been checked (add to node that already has street owned by player) (place
         * where none of the surrounding nodes is owned by the player)
         */
        /*
         * Now we check whether this street borders to one of the streets owned by the player.
         */
        //maybuild = false;
        for (Border bc : surroundingBorders) {
            if (bc.getOwner() != null && bc.getOwner().equals(player)) {
                maybuild = true;
            }
        }
        if (maybuild) {
            border.setOwner(player);
            player.addBorder(border);
        } else {
            throw new CannotBuildException("U bezit geen weg naar deze bouwplaats.");
        }
    }
    /*
     * Sets Robber on its initial place -> The desert tile.
     */

    public void placeRobber() throws Exception {
        for (Map.Entry<Coordinate, Tile> entry : tiles.entrySet()) {
            if (entry.getValue().getTileType() == TileType.DESERT) {
                entry.getValue().setHasRobber(true);
                entry.getValue().setNumber(0);
                robber = entry.getValue().getCoordinate();
            }
        }
        setChanged();
        notifyObservers();
    }

    public void synchronize(Board updatedBoard) throws Exception {
        /*
         * only changed objects are sent from the server.
         */
        if (updatedBoard.getTiles() != null) {
            for (Tile t : updatedBoard.getTiles().values()) {
                Tile myTile = this.getTile(t.getCoordinate());
                myTile.synchronize(t);
                if (!(myTile.getBehaviour() instanceof SeaTileBehaviour)) {
                    addSurroundingBordersToAvailableBorders(myTile);
                    addSurroundingNodesToAvailableBuildingSpots(myTile);
                }
            }
        }
        /*
         * only if borders have changed.
         */
        if (updatedBoard.getBorders() != null) {
            for (Border b : updatedBoard.getBorders().values()) {
                Border myBorder = this.getBorder(b.getCoordinate());
                myBorder.synchronize(b);
            }
        }
        /*
         * only for changed nodes.
         */
        if (updatedBoard.getNodes() != null) {
            for (Node n : updatedBoard.getNodes().values()) {
                Node myNode = this.getNode(n.getCoordinate());
                myNode.synchronize(n);
            }
        }
        if (updatedBoard.getRobber() != null && !(updatedBoard.getRobber().equals(this.getRobber()))) {
            this.moveRobber(updatedBoard.getRobber());
        }
    }
}
