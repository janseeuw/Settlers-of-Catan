package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.gameState.RobberState;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;

/**
 *
 * @author Joachim,Brenden Dit paneel stelt een node voor waarop men kan bouwen
 * en implementeert dus zeer veel mouselistener activieiten.
 */
public class Node extends JPanel implements MouseListener, Observer {

    private Observable subject;
    private Coordinate coordinate;
    private String color;
    private double xPosition;
    private double yPosition;
    private double radius;
    private int nummer = 0;
    private Image building;
    private String image;
    private boolean robberState = false;

    Node(double xPosition, double yPosition, double radius, Coordinate c) {
        subject = GuiController.getInstance().getNode(c);
        subject.addObserver(this);
        this.coordinate = c;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.radius = radius;
        this.setBounds((int) xPosition, (int) yPosition, (int) (2 * radius + 1), (int) (2 * radius + 1));
        this.setPreferredSize(new Dimension((int) (2 * radius + 1), (int) (2 * radius + 1)));
        this.setOpaque(false);

        update(subject, null);
    }

    public Image getBuilding() {
        return building;
    }

    public void setBuilding(Image building) {
        this.building = building;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //Methode om coordinaten te plaatsen
        //Node width and node height will be the same.
        Ellipse2D.Double node = new Ellipse2D.Double(xPosition, yPosition, 2 * radius, 2 * radius);
        //g2d.setColor(Color.BLACK);
        nummer++;
        try {
            building = ImageIO.read(getClass().getResource("/" + image.toUpperCase() + "." + color + ".png"));

        } catch (Exception e) {
        }
        node.setFrame(new Rectangle(new Dimension((int) (2 * radius), (int) (2 * radius))));

        if (getBuilding() != null) {
            g2d.clip(node);
            g2d.drawImage(getBuilding(), 0, 0, (int) getBounds().getWidth(), (int) getBounds().getHeight(), this);
        }

    }

    public void mouseClicked(MouseEvent e) {
        if (GuiController.getInstance().getGameState() instanceof RobberState) {
            robberState = true;
        }
        if (!robberState) {
            GuiController.getInstance().notifyNodeClicked(coordinate, e.getButton() == MouseEvent.BUTTON3, this);
        } else {
            try {
                GuiController.getInstance().stealResource(coordinate);
                GuiController.getInstance().disableStealWhenRobber();
            } catch (Exception ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {

        System.out.println(
                this.getMouseListeners().length);
        if (GuiController.getInstance().getClient() == GuiController.getInstance().getCurrentPlayer()) {
            if (robberState) {

                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    public void mouseExited(MouseEvent e) {
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void getCoordinate(Coordinate c) {
        this.coordinate = c;
    }

    public void update(Observable o, Object o1) {
        be.hogent.team10.catan_businesslogic.model.Node node = (be.hogent.team10.catan_businesslogic.model.Node) o;
        image = node.getBuildingtype().name();
        if (node.getOwner() != null && node.getOwner().getPlayerColor() != null) {
            color = node.getOwner().getPlayerColor().name();
        }
        repaint();
    }

    void removeMouseListener() {
        for (MouseListener l : this.getMouseListeners()) {
            removeMouseListener(l);
        }
    }

    void addMouseListener() {
        removeMouseListener();
        addMouseListener(this);
    }

    public void showDetails(ComponentDetailPanel lastDetailPanel) {
        this.getParent().add(lastDetailPanel, 10);
        lastDetailPanel.setSize(250, 500);
        Rectangle r = this.getBounds();
        r.setSize(250, 200);
        r.translate(30, 30);
        lastDetailPanel.setBounds(r);
        lastDetailPanel.setVisible(true);

    }

    public void setRobberState() {
        addMouseListener();
        robberState = true;
    }

    public void endRobberState() {
        robberState = false;
    }
}
