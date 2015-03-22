package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Border;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Tile;

/**
 *
 * @author Joachim,Brenden Dit paneel is een representatie van het spelbord.
 */
public class BoardPanel extends JLayeredPane {

    private int lengthOfSideHex;
    private int numberOfRings;
    private int streetWidth;
    private Dimension dimension;
    private GuiController guiC;
    private int nummer = 0;
    private List<Hexagon> hexagons;
    private List<Point> points;
    private int centerXBoard;
    private int centerYBoard;
    private List<Coordinate> pointsOfNodes;
    private List<Street> streets;
    private List<Node> nodes;

    /**
     * NumberOfRings defines how big the board is made. StreetWidth defines the
     * spacing between the tiles on the board. LengthOfSideHex is the length of
     * a side of the hexagon.
     *
     * @param numberOfRings
     * @param streetWidth
     * @param lengthOfSideHex
     * @param guiC
     */
    public BoardPanel(int numberOfRings, int streetWidth, int lengthOfSideHex, GuiController guiC) {
        this.setLayout(null);
        this.guiC = guiC;
        this.lengthOfSideHex = lengthOfSideHex;
        this.numberOfRings = numberOfRings;
        this.streetWidth = streetWidth;

        points = new ArrayList<Point>();
        nodes = new ArrayList<Node>();

    }

    /**
     * This method returns a dimension object with the size of the boardPanel.
     *
     * @return Dimension object
     */
    public Dimension getDimension() {
        return dimension;
    }

    public List<Hexagon> getHexagons() {
        return hexagons;
    }

    /**
     * This method is used to set the dimension.
     *
     * @param dimension
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * InitializeBoard is responsible for the creating of the gui components of
     * the board. It calls three different methods, one for rendering the tiles,
     * the nodes and the streets.
     *
     * @param dimension
     */
    public void initializeBoard(Dimension dimension) {
        setDimension(dimension);
        centerXBoard = (dimension.width / 2);
        centerYBoard = dimension.height / 2;
        initializeTiles();
        initializeNodes();
        initializeStreets();
    }

    private void initializeTiles() {
        //Midden van het bord bepalen
        List<Point> points;
        hexagons = new ArrayList<Hexagon>();
        int n = 1;
        for (int i = 0; i < numberOfRings; i++) {
            points = defineTopLeftPositionOfHexBounds(i, n);
            for (int j = 0; j < points.size(); j++) {
                //Teken polygon
                Hexagon hexagon;
                Coordinate c = getCoordinate(i, j);
                if (i != numberOfRings - 1) {
                    hexagon = new Hexagon(lengthOfSideHex, points.get(j).x, points.get(j).y, nummer, false, getCoordinate(i, j));
                } else {
                    hexagon = new Hexagon(lengthOfSideHex, points.get(j).x, points.get(j).y, nummer, true, getCoordinate(i, j));
                    Image img = null;
                    try {
                        img = ImageIO.read(getClass().getResource("/SEA.png"));
                        img = img.getScaledInstance(guiC.getSize("HexScaleX"), guiC.getSize("HexScaleY"), 1);
                    } catch (IOException ex) {
                        Logger.getLogger(Hexagon.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ImageIcon icon = new ImageIcon(img);
                    hexagon.setIcon(icon);
                }
                hexagons.add(hexagon);
                this.setComponentZOrder(hexagon, 0);
                this.add(hexagon, 0);
                nummer++;
            }
            if (i == 0) {
                n += 5;
            } else {
                n += 6;
            }
        }
    }

    private void initializeStreets() {
        streets = new ArrayList<Street>();
        double apotheos;
        Set<Coordinate> drawn = new HashSet<Coordinate>();
        for (Hexagon h : hexagons) {
            //nodes can be found using mathematical expression.
            // -1,-1 = border left from centre of the tile.
            int[][] addendum = new int[][]{{1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}, {0, 1}};
            // r of the inner circle of the hexagon
            apotheos = Math.cos(Math.PI / 6) * lengthOfSideHex;
            for (int i = 0; i <= 5; i++) {
                Coordinate borderCoordinate = new Coordinate(h.getCoordinate().getX() + addendum[i][0], h.getCoordinate().getY() + addendum[i][1]);
                if (drawn.contains(borderCoordinate)) {
                    continue;
                } else {
                    drawn.add(borderCoordinate);
                }
                double addX = Math.cos(((double) i) * (Math.PI / 3)) * (apotheos + streetWidth / 2);
                double addY = Math.sin(((double) i) * (Math.PI / 3)) * (apotheos + streetWidth / 2);


                double tileX = h.getMiddle().x - (h.getWidth() / 2);
                double tileY = h.getMiddle().y - (h.getHeight() / 2);
                Street s = new Street(tileX + addX, tileY + addY, (int) lengthOfSideHex, (int) streetWidth, borderCoordinate, (3 * Math.PI / 6) + (i * 2 * (Math.PI / 6)));
                streets.add(s);
                this.setComponentZOrder(s, 2);
                this.add(s, 2);

            }
        }
    }

    private void initializeNodes() {
        int radius = 20;
        definePositionsNodes(streetWidth, radius);
    }

    private Point getLocation(Border n) {
        double rotatedHight = streetWidth / Math.sin(Math.toRadians(60));
        double triangleHeight = Math.cos(Math.toRadians(30)) * lengthOfSideHex;
        double triangleWidth = Math.cos(Math.toRadians(60)) * lengthOfSideHex;

        int fullTilesToRight = 0;
        int halfTilesToRight = 0;
        int tilesToRightForRow;
        int halfTilesToRightForRow = 0;
        int halfTriangleToRightForRow = 0;
        int halfTileDifferenceX;
        int fullTileDifferenceX;
        int halfTriangleHeight = 0;
        int calcX;
        int calcY;
        int backupX, backupY;
        Coordinate c = n.getCoordinate();

        if (c.getX() < c.getY()) {
            backupX = calcX = c.getX();
            backupY = calcY = c.getY();
        } else {
            backupX = calcX = c.getY();
            backupY = calcY = c.getX();
        }

        while (calcX != 0) {
            calcX -= 1;//most left element on the row
            calcY -= 1;//most left element on the row
            halfTilesToRight += 1;
        }
        fullTilesToRight = halfTilesToRight / 2;
        halfTilesToRight = halfTilesToRight % 2;

        tilesToRightForRow = calcY / 4;
        calcY = calcY % 4;
        halfTilesToRightForRow = calcY / 2;
        calcY = calcY % 2;
        halfTriangleToRightForRow = calcY;
        fullTileDifferenceX = halfTilesToRightForRow + tilesToRightForRow * 2;
        halfTileDifferenceX = halfTriangleHeight = halfTriangleToRightForRow;

        int startX = (dimension.width / 2) - 100 - 3 * fullTilesToRight - halfTilesToRightForRow - 3 * streetWidth;
        int startY = dimension.height / 2;
        int apotheos = (int) (Math.cos(Math.PI / 6) * lengthOfSideHex);
        int yPositionHex = startY - lengthOfSideHex;

        //Waarde toe te voegen aan border 00 om andere borders te verkrijgen
        int xCo = startX + (tilesToRightForRow * (2 * apotheos + streetWidth) + halfTilesToRightForRow * (apotheos + streetWidth / 2)
                + halfTilesToRight * (apotheos + streetWidth / 2) + fullTilesToRight * (2 * apotheos + streetWidth) + halfTriangleToRightForRow * (apotheos / 2));
        int yCo = (int) (startY + (fullTileDifferenceX * lengthOfSideHex * 2) + (fullTileDifferenceX * (Math.sin(Math.PI / 6) * lengthOfSideHex))
                + halfTileDifferenceX * lengthOfSideHex + halfTileDifferenceX * (Math.tan(Math.PI / 6) * apotheos / 2));

        Point position = new Point(xCo, yCo);
        return position;
    }

    private List<Point> defineTopLeftPositionOfHexBounds(int ringNumber, int numberOfHex) {
        List<Point> points = new ArrayList<Point>();
        //Zal aangepast moeten worden bij samenvoegen van projecten
        double centerXBoard = (dimension.width / 2);
        double centerYBoard = dimension.height / 2 + 10;
        //Apothema bepalen van de hexagon met dmv zijn zijde
        double apotheos = (Math.cos(Math.PI / 6) * lengthOfSideHex);
        //Eerste coordinaat bepalen voor eerste hexagon
        double xPositionHex = centerXBoard - apotheos;
        double yPositionHex = centerYBoard - lengthOfSideHex;
        Point pointsArray[];
        int aantal;
        if (ringNumber == 0) {
            aantal = 1;
            pointsArray = new Point[ringNumber + 1];
        } else if (ringNumber == 1) {
            aantal = 6;
            pointsArray = new Point[ringNumber];
        } else {
            aantal = 6;
            pointsArray = new Point[ringNumber + 1];
        }
        for (int j = 0; j < pointsArray.length; j++) {
            pointsArray[j] = new Point();
        }
        for (int i = 0; i <= aantal; i++) {
            pointsArray[0].x = (int) (Math.round((xPositionHex + ((apotheos * 2) + streetWidth) * ringNumber * Math.cos(i * 2 * Math.PI / 6))));
            pointsArray[0].y = (int) (Math.round((yPositionHex + ((apotheos * 2) + streetWidth) * ringNumber * Math.sin(i * 2 * Math.PI / 6))));
            if (ringNumber >= 2) {
                if (i == 0) {
                    pointsArray[1] = pointsArray[0];
                } else if (almostEqual(pointsArray[1].getY(), pointsArray[0].getY()) && pointsArray[0].getX() > pointsArray[1].getX()) {
                    for (int j = 2; j < pointsArray.length; j++) {
                        pointsArray[j].x = (int) (Math.round(((pointsArray[0].getX() - pointsArray[1].getX()) / ringNumber) * (j - 1) + pointsArray[1].getX()));
                        pointsArray[j].y = (int) (Math.round(pointsArray[0].getY()));
                        points.add(pointsArray[j]);
                    }
                } else if (almostEqual(pointsArray[1].getY(), pointsArray[0].getY()) && pointsArray[0].getX() < pointsArray[1].getX()) {
                    for (int j = 2; j < pointsArray.length; j++) {
                        pointsArray[j].x = (int) (Math.round(pointsArray[1].getX() - ((pointsArray[1].getX() - pointsArray[0].getX()) / ringNumber) * (j - 1)));
                        pointsArray[j].y = (int) (Math.round(pointsArray[0].getY()));
                        points.add(pointsArray[j]);
                    }
                } else if (pointsArray[1].getX() != pointsArray[0].getX() && pointsArray[1].getY() != pointsArray[0].getY()) {
                    if (pointsArray[0].getX() > pointsArray[1].getX() && pointsArray[0].getY() > pointsArray[1].getY()) {
                        for (int j = 2; j < pointsArray.length; j++) {
                            pointsArray[j].x = (int) (Math.round(((pointsArray[0].getX() - pointsArray[1].getX()) / ringNumber) * (j - 1) + pointsArray[1].getX()));
                            pointsArray[j].y = (int) (Math.round(((pointsArray[0].getY() - pointsArray[1].getY()) / ringNumber) * (j - 1) + pointsArray[1].getY()));
                            points.add(pointsArray[j]);
                        }
                    } else if (pointsArray[0].getX() < pointsArray[1].getX() && pointsArray[0].getY() > pointsArray[1].getY()) {
                        for (int j = 2; j < pointsArray.length; j++) {
                            pointsArray[j].x = (int) (pointsArray[1].getX() - ((pointsArray[1].getX() - pointsArray[0].getX()) / ringNumber) * (j - 1));
                            pointsArray[j].y = (int) (((pointsArray[0].getY() - pointsArray[1].getY()) / ringNumber) * (j - 1) + pointsArray[1].getY());
                            points.add(pointsArray[j]);
                        }
                    } else if (pointsArray[0].getX() > pointsArray[1].getX() && pointsArray[0].getY() < pointsArray[1].getY()) {
                        for (int j = 2; j < pointsArray.length; j++) {
                            pointsArray[j].x = (int) (Math.round(((pointsArray[0].getX() - pointsArray[1].getX()) / ringNumber) * (j - 1) + pointsArray[1].getX()));
                            pointsArray[j].y = (int) (Math.round(pointsArray[1].getY() - ((pointsArray[1].getY() - pointsArray[0].getY()) / ringNumber) * (j - 1)));
                            points.add(pointsArray[j]);
                        }
                    } else if (pointsArray[0].getX() < pointsArray[1].getX() && pointsArray[0].getY() < pointsArray[1].getY()) {
                        for (int j = 2; j < pointsArray.length; j++) {
                            pointsArray[j].x = (int) (Math.round(pointsArray[1].getX() - ((pointsArray[1].getX() - pointsArray[0].getX()) / ringNumber) * (j - 1)));
                            pointsArray[j].y = (int) (Math.round(pointsArray[1].getY() - ((pointsArray[1].getY() - pointsArray[0].getY()) / ringNumber) * (j - 1)));
                            points.add(pointsArray[j]);
                        }
                    }
                }
                if (i != 6) {
                    pointsArray[1] = pointsArray[0];
                }
            }
            if (i != 6 && !(ringNumber == 0 && i == 1)) {
                points.add(pointsArray[0]);
            }
            pointsArray[0] = new Point();
            for (int j = 2; j < pointsArray.length; j++) {
                pointsArray[j] = new Point();
            }
        }
        return points;
    }

    //Check if the coordinates are the same
    private boolean almostEqual(double previous, double next) {
        boolean sameCoordinates;
        double under = previous - 5;
        double above = previous + 5;
        if (next > under && next < above) {
            sameCoordinates = true;
        } else {
            sameCoordinates = false;
        }
        return sameCoordinates;
    }

    private Coordinate getCoordinate(int i, int j) {
        /**
         * We start in the middle of he board. on the middle row all x'es are =
         * y's
         *
         */
        int xy = 7;

        if (i == 0 && j == 0) {
            return new Coordinate(xy, xy);
        } else if (i == 1) {
            switch (j) {
                case 0:
                    return new Coordinate(xy + 2, xy + 2);
                case 1:
                    return new Coordinate(xy + 2, xy);
                case 2:
                    return new Coordinate(xy, xy - 2);
                case 3:
                    return new Coordinate(xy - 2, xy - 2);
                case 4:
                    return new Coordinate(xy - 2, xy);
                case 5:
                    return new Coordinate(xy, xy + 2);
            }
        } else if (i == 2) {
            switch (j) {
                case 0:
                    return new Coordinate(xy + 4, xy + 4);
                case 1:
                    return new Coordinate(xy + 4, xy + 2);
                case 2:
                    return new Coordinate(xy + 4, xy);
                case 3:
                    return new Coordinate(xy + 2, xy - 2);
                case 4:
                    return new Coordinate(xy, xy - 4);
                case 5:
                    return new Coordinate(xy - 2, xy - 4);
                case 6:
                    return new Coordinate(xy - 4, xy - 4);
                case 7:
                    return new Coordinate(xy - 4, xy - 2);
                case 8:
                    return new Coordinate(xy - 4, xy);
                case 9:
                    return new Coordinate(xy - 2, xy + 2);
                case 10:
                    return new Coordinate(xy, xy + 4);
                case 11:
                    return new Coordinate(xy + 2, xy + 4);
            }
        } else if (i == 3) {
            switch (j) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return new Coordinate(xy + 6, xy + 6 - j * 2);
                case 4:
                case 5:
                case 6:
                    return new Coordinate(xy + 6 - (j - 3) * 2, xy + 6 - j * 2);
                case 7:
                case 8:
                case 9:
                    return new Coordinate(xy - (j - 6) * 2, 1);
                case 10:
                case 11:
                case 12:
                    return new Coordinate(1, 1 + (j - 9) * 2);
                case 13:
                case 14:
                case 15:
                    return new Coordinate(1 + (j - 12) * 2, xy + (j - 12) * 2);
                case 16:
                case 17:
                    return new Coordinate(xy + (j - 15) * 2, xy + 6);
            }
        }
        return null;
    }

    private void definePositionsNodes(int streetWidth, int radius) {
        Set<Coordinate> drawnNodes = new HashSet<Coordinate>();
        int[][] addendum = new int[][]{{1, 2}, {0, 1}, {-1, 0}, {0, -1}, {1, 0}, {2, 1}};
        pointsOfNodes = new ArrayList<Coordinate>();
        double xBegin = (lengthOfSideHex + (Math.sin(Math.PI / 6) * streetWidth) / 2);
        double yBegin = (lengthOfSideHex + (Math.sin(Math.PI / 6) * streetWidth) / 2);
        double xTotal = xBegin;
        double yTotal = yBegin;
        int numberOfPoints = 6;
        double theta = (Math.PI * 2) / 6;
        double thetaNul = (Math.PI) / 6;
        double apotheos = Math.cos(Math.PI / 6) * lengthOfSideHex;
        for (int i = 0; i < hexagons.size(); i++) {
            double centerX = hexagons.get(i).getBounds().getLocation().getX() + apotheos;
            double centerY = hexagons.get(i).getBounds().getLocation().getY() + lengthOfSideHex;
            for (int j = 0; j < numberOfPoints; j++) {
                double xCo = xTotal * Math.cos(thetaNul);
                double yCo = yTotal * Math.sin(thetaNul);
                Point point = new Point((int) (Math.round(centerX + xCo)), (int) (Math.round(centerY - yCo)));
                Coordinate c = null;
                switch (j) {
                    case 0:
                        c = new Coordinate(hexagons.get(i).getCoordinate().getX() + 1, hexagons.get(i).getCoordinate().getY() + 2);
                        break;
                    case 1:
                        c = new Coordinate(hexagons.get(i).getCoordinate().getX(), hexagons.get(i).getCoordinate().getY() + 1);
                        break;
                    case 2:
                        c = new Coordinate(hexagons.get(i).getCoordinate().getX() - 1, hexagons.get(i).getCoordinate().getY());
                        break;
                    case 3:
                        c = new Coordinate(hexagons.get(i).getCoordinate().getX(), hexagons.get(i).getCoordinate().getY() - 1);
                        break;
                    case 4:
                        c = new Coordinate(hexagons.get(i).getCoordinate().getX() + 1, hexagons.get(i).getCoordinate().getY());
                        break;
                    case 5:
                        c = new Coordinate(hexagons.get(i).getCoordinate().getX() + 2, hexagons.get(i).getCoordinate().getY() + 1);
                        break;
                }
                if (!drawnNodes.contains(c)) {
                    createNode(point, radius, c);
                    drawnNodes.add(c);
                }
                thetaNul += theta;
            }
            thetaNul = (Math.PI) / 6;
        }
        //return pointsOfNodes;
    }

    private boolean pointExists(Point point, List<Point> pointsOfNodes) {
        boolean exists = false;
        for (int k = 0; k < pointsOfNodes.size(); k++) {
            if (almostEqual(point.getX(), pointsOfNodes.get(k).getX()) && almostEqual(point.getY(), pointsOfNodes.get(k).getY())) {
                exists = true;
            }
            if (point.equals(pointsOfNodes)) {
                exists = true;
            }
        }
        return exists;
    }

    private boolean streetExists(Street street) {
        int i = 0;
        boolean exists = false;
        while (exists == false && i < streets.size()) {
            if (streets.get(i).equals(street)) {
                exists = true;
            }
            i++;
        }
        return exists;
    }

    private void createNode(Point point, int radius, Coordinate c) {
        Node node = new Node(point.getX() - radius, point.getY() - radius, radius, c);
        nodes.add(node);
        this.setComponentZOrder(node, 1);
        this.add(node, 1);
    }

    public void initializeValueChips() {
        int radius = 15;
        double apotheos = (Math.cos(Math.PI / 6) * lengthOfSideHex);
        Map<Coordinate, Tile> tiles = GuiController.getInstance().getTiles();
        for (Coordinate coordinate : tiles.keySet()) {
            int number = tiles.get(coordinate).getNumber();
            for (int i = 0; i < hexagons.size(); i++) {
                if (hexagons.get(i).getCoordinate().equals(tiles.get(coordinate).getCoordinate())) {
                    NumberToken numberToken = new NumberToken(
                            new Point((int) Math.round(hexagons.get(i).getX() + apotheos - radius), (int) Math.round(hexagons.get(i).getY()) + lengthOfSideHex - radius), number, radius);
                    this.add(numberToken);
                }
            }
        }

    }

    /*
     * public void addMouseListenerForNumberTokens() { for (Hexagon hex :
     * hexagons) { hex.removeMouseListener(); hex.addNumberTokenMouseListener();
     * }
     }
     */
    public void addMouseListenerForStreets() {
        for (Street street : streets) {
            street.addMouseListener();
        }
    }

    public void addMouseListenerForNodes() {
        for (Node node : nodes) {
            node.addMouseListener();
        }
    }

    public void removeMouseListenerForStreets() {
        for (Street street : streets) {
            street.removeMouseListener();
        }
    }

    public void removeMouseListenerForNodes() {
        for (Node node : nodes) {
            node.removeMouseListener();
        }
    }

    public void setNodesOnTop() {
        for (Node node : nodes) {
            this.moveToFront(node);
        }
    }

    public void setStreetsOnTop() {
        for (Street street : streets) {
            this.moveToFront(street);
        }
    }

    public List<Node> getNodes() {
        return this.nodes;
    }
}
