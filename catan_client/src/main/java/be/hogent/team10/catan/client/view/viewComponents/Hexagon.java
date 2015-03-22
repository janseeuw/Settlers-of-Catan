package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Tile;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;

/**
 * Class that represents a tile of the game. Each tile has an image of the
 * resource that has been assigned to it. During the initialization the
 * resourcetiles have no image assigned, so the background will be gray by
 * default. This label also has a Numbertoken
 */
public class Hexagon extends JLabel implements MouseListener, Observer {

    private Observable subject;
    private Coordinate coordinate;
    private Polygon tile;
    private boolean clicked;
    private int centerX;
    private int centerY;
    private int side;
    private int number = 0;
    private int id;
    private Point middle;
    private String image = "";
    private boolean hasRobber;
    private int radius = 18;
    private boolean robberState = false;
    private NumberToken numberToken;
    private Image cursorImage = null;

    Hexagon(int lengthOfSide, int xPosition, int yPosition, int number, boolean sea, Coordinate c) {
        this.subject = GuiController.getInstance().getTile(c);
        this.coordinate = c;
        side = lengthOfSide;
        int height = lengthOfSide * 2;
        int width = (int) (lengthOfSide * Math.sqrt(3));
        centerY = width / 2;
        centerX = height / 2;
        middle = new Point(xPosition + centerX, yPosition + centerY);
        tile = new Polygon();
        calculateCoordinates(centerX, centerY);
        //Om op de juiste plaats te positioneren
        this.setBounds(xPosition, yPosition, width + 1, height + 1);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(width + 1, height + 1));
        if (!sea) {
            addMouseListener(this);
        }
        subject.addObserver(this);
        update(subject, "");


        try {
            cursorImage = ImageIO.read(getClass().getResource("/robber.png"));
        } catch (IOException ex) {
            Logger.getLogger(Hexagon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Point getMiddle() {
        return middle;
    }

    public int getNumber() {
        return number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Coordinate getCoordinate() {
        if (coordinate == null) {
        }
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getEdgeLength() {
        return side;
    }

    public boolean isHasThief() {
        return hasRobber;
    }

    public void setHasThief(boolean hasThief) {
        this.hasRobber = hasThief;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setTile(Polygon tile) {
        this.tile = tile;
    }

    public Polygon getTile() {
        return tile;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (image != null && !image.equalsIgnoreCase("")) {
            choseImage(g2d);
        }
        if (hasRobber && numberToken == null) {
            drawRobber();
        }
        if (number != 0 && !hasRobber && numberToken == null) {
            drawNumberToken();
        }
        //g2d.drawPolygon(tile);
    }

    public void setIsClicked(boolean clicked) {
        this.clicked = clicked;
    }

    private void calculateCoordinates(int cx, int cy) {
        int xCo[] = new int[6];
        int yCo[] = new int[6];

        for (int i = 0; i < 6; i++) {
            yCo[i] = (int) (cx + side * Math.cos(i * 2 * Math.PI / 6));
            xCo[i] = (int) (cy + side * Math.sin(i * 2 * Math.PI / 6));
        }

        Polygon poly = new Polygon(xCo, yCo, 6);

        this.setTile(poly);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!robberState) {
            GuiController.getInstance().notifyTileClicked(coordinate, e.getButton() == MouseEvent.BUTTON3, this);
        } else {
            GuiController.getInstance().moveRobber(coordinate);
            GuiController.getInstance().disableRobberState();
            ErrorPanel.getInstance().setError("Kies een huis van een andere speler waarvan je een grondstof wilt stelen.");
            GuiController.getInstance().stealWhenRobber(coordinate);
        }
    }

    void removeMouseListener() {
        this.removeMouseListener(this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (GuiController.getInstance().getCurrentPlayer() != null && GuiController.getInstance().getClientNumber() == GuiController.getInstance().getCurrentPlayer().getId()) {
            if (robberState) {
                Point hotspot = new Point(0, 0);
                String cursorName = "Robber Cursor";
                setCursor(getToolkit().createCustomCursor(cursorImage, hotspot, cursorName));

            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private void choseImage(Graphics2D g2d) {
        Image img = null;
        try {
            img = ImageIO.read(getClass().getResource("/" + image + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(Hexagon.class.getName()).log(Level.SEVERE, null, ex);
        }
        img = img.getScaledInstance(GuiController.getInstance().getSize("HexScaleX"), GuiController.getInstance().getSize("HexScaleY"), 1);
        ImageIcon icon = new ImageIcon(img);
        g2d.drawImage(img, 0, 2, null);
    }

    private void drawNumberToken() {
        numberToken = new NumberToken(new Point((int) Math.round(this.getBounds().getWidth() / 2) - radius - 5, (int) Math.round(this.getBounds().getHeight() / 2) - radius - 5), number, radius);
        numberToken.setVisible(true);
        numberToken.setSize(50, 50);
        this.add(numberToken);
    }

    @Override
    public void update(Observable o, Object o1) {
        Tile t = (Tile) o;
        if (t.getTileType() != null) {
            image = t.getTileType().name();
            //there are 2 messages sent for a resourcetile
            if (image.equals("RESOURCE") && t.getResource() != null) {
                image = ((Tile) o).getResource().name();
            }
        } else {
            image = "";
        }
        hasRobber = ((Tile) o).getHasRobber();
        number = ((Tile) o).getNumber();

        if (numberToken != null) {
            numberToken.setNumber(number, hasRobber);

        }
        this.repaint();
    }

    public void showDetails(ComponentDetailPanel lastDetailPanel) {
        this.getParent().add(lastDetailPanel, 10);
        ((JLayeredPane) this.getParent()).moveToFront(lastDetailPanel);
        lastDetailPanel.setSize(250, 500);
        Rectangle r = this.getBounds();
        r.setSize(250, 200);
        r.translate(30, 30);
        lastDetailPanel.setBounds(r);
        lastDetailPanel.setVisible(true);

    }

    private void drawRobber() {
        numberToken = new NumberToken(new Point((int) Math.round(this.getBounds().getWidth() / 2) - radius - 5, (int) Math.round(this.getBounds().getHeight() / 2) - radius - 5), radius);
        numberToken.setVisible(true);
        numberToken.setBackground(Color.WHITE);
        numberToken.setSize(50, 50);
        this.add(numberToken);
    }

    public void enableRobberState() {
        robberState = true;
    }

    public void disableRobberState() {
        robberState = false;
    }

    public void flicker() {
        if (number != 7 && number != 0) {
            numberToken.flicker(0);//the zero is a depth marker for the recursion of this fucntion
        }
    }

    public boolean getHasRobber() {
        return hasRobber;
    }
}
