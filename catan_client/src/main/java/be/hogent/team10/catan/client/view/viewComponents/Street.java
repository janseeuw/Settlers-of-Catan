package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Border;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import be.hogent.team10.catan_businesslogic.util.PlayerColor;

/**
 *
 * @author Joachim,Brenden Dit paneel is een straat.
 */
public class Street extends JPanel implements MouseListener, Observer {

    private Observable subject;
    private Polygon rectangle;
    private Color color = Color.BLACK;
    private int id = 0;
    private Coordinate coordinate;

    public Street(double middleX, double middleY, int lengthOfSideHex, int widthStreet, Coordinate coordinate, double rotationAngle) {
        subject = GuiController.getInstance().getBorder(coordinate);
        subject.addObserver(this);
        Point rotatedTopLeft = rotate((middleX - (lengthOfSideHex / 2)), (middleY - (widthStreet) / 2), middleX, middleY, rotationAngle);
        Point rotatedTopRight = rotate((middleX + (lengthOfSideHex / 2)), (middleY - (widthStreet) / 2), middleX, middleY, rotationAngle);
        Point rotatedBottomLeft = rotate((middleX - (lengthOfSideHex / 2)), (middleY + (widthStreet) / 2), middleX, middleY, rotationAngle);
        Point rotatedBottomRight = rotate((middleX + (lengthOfSideHex / 2)), (middleY + (widthStreet) / 2), middleX, middleY, rotationAngle);
        //calculate how much space you need for the rectangle that describes bounds.
        Polygon dummyRectangle = new Polygon();
        dummyRectangle.addPoint(rotatedTopRight.x, rotatedTopRight.y);
        dummyRectangle.addPoint(rotatedTopLeft.x, rotatedTopLeft.y);
        dummyRectangle.addPoint(rotatedBottomLeft.x, rotatedBottomLeft.y);
        dummyRectangle.addPoint(rotatedBottomRight.x, rotatedBottomRight.y);
        double subtractXForBounds = middleX - dummyRectangle.getBounds().width / 2 - 1;
        double subtractYForBounds = middleY - dummyRectangle.getBounds().height / 2 - 1;
        //System.out.println(dummyRectangle.getBounds() + "for bounds x");
        //System.out.println(subtractYForBounds + "for bounds y");
        rectangle = new Polygon();
        rectangle.addPoint((int) (rotatedTopRight.x - subtractXForBounds), (int) (rotatedTopRight.y - subtractYForBounds));
        rectangle.addPoint((int) (rotatedTopLeft.x - subtractXForBounds), (int) (rotatedTopLeft.y - subtractYForBounds));
        rectangle.addPoint((int) (rotatedBottomLeft.x - subtractXForBounds), (int) (rotatedBottomLeft.y - subtractYForBounds));
        rectangle.addPoint((int) (rotatedBottomRight.x - subtractXForBounds), (int) (rotatedBottomRight.y - subtractYForBounds));

        ///System.out.println(rectangle.getBounds() + "rect bounds");
        this.coordinate = coordinate;
        this.setBounds((int) subtractXForBounds + GuiController.getInstance().getSize("StreetX"), (int) subtractYForBounds + GuiController.getInstance().getSize("StreetY"), dummyRectangle.getBounds().width, dummyRectangle.getBounds().height);

        this.setPreferredSize(new Dimension(lengthOfSideHex, lengthOfSideHex));
        this.setOpaque(false);
        //addMouseListener(this);
        update(subject, null);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Polygon getRectangle() {
        return rectangle;
    }

    public void setRectangle(Polygon rectangle) {
        this.rectangle = rectangle;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setBackground(color);
        g2d.setColor(color);
        g2d.fillPolygon(rectangle);
        g2d.drawPolygon(rectangle);
        //System.out.println(rectangle.getBounds());

    }

    public void mouseClicked(MouseEvent e) {
        if (this.getMouseListeners().length > 1) {
            this.removeMouseListener();
        } else {
            GuiController.getInstance().notifyStreetClicked(coordinate, e.getButton() == MouseEvent.BUTTON3, this);
        }
    }

    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Method based on :
     * http://stackoverflow.com/questions/4145609/rotate-rectangle-in-java used
     * for rotation of the shape around its centre. returns Point where that
     * point will finally be.
     */
    private Point rotate(double cornerX, double cornerY, double middleX, double middleY, double angle) {
        double dx = cornerX - middleX;
        double dy = cornerY - middleY;
        double newX = Math.round(middleX - dx * Math.cos(angle) + dy * Math.sin(angle));
        double newY = Math.round(middleY - dx * Math.sin(angle) - dy * Math.cos(angle));
        return new Point((int) newX, (int) newY);

    }

    public int getId() {
        return this.id;
    }

    public void update(Observable o, Object o1) {
        Border b = ((Border) o);
        if (b.getOwner() != null && b.getOwner().getPlayerColor() != null) {
            color = b.getOwner().getPlayerColor().getColor();
        }
        this.repaint();
    }

    void removeMouseListener() {
        removeMouseListener(this);
    }

    void addMouseListener() {
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
}
