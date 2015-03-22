package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.util.Dice;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author Brenden,Joachim Dit paneel stelt een nummer van een hexagon voor. Ook
 * bevat dit de flicker methode die een animatie is voor weer te geven welke
 * resources net gerold zijn.
 */
public class NumberToken extends JPanel implements Observer {

    private int number;
    private Image numberImage;
    private Image flickerImage;
    private JLabel imageLabel;
    private Image robberImage;
    private Icon temp;
    private boolean flickering;
    private boolean hasRobber;

    public NumberToken(Point position, int radius) {

        this.setBounds((int) Math.round(position.getX()), (int) Math.round(position.getY()), (int) (2 * radius + 1), (int) (2 * radius + 1));
        this.setPreferredSize(new Dimension((int) (2 * radius + 1), (int) (2 * radius + 1)));
        this.setOpaque(false);
        try {
            this.robberImage = ImageIO.read(getClass().getResource("/robber.png"));
            this.robberImage = robberImage.getScaledInstance(35, 35, 1);
            this.flickerImage = ImageIO.read(getClass().getResource("/flash2.gif"));
            this.flickerImage = flickerImage.getScaledInstance(35, 35, 1);

        } catch (IOException ex) {
            Logger.getLogger(NumberToken.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.imageLabel = new JLabel(new ImageIcon(robberImage));

        add(imageLabel);
        GuiController.getInstance().getDice().addObserver(this);
    }

    NumberToken(Point position, int number, int radius) {

        this.number = number;
        this.setBounds((int) Math.round(position.getX()), (int) Math.round(position.getY()), (int) (2 * radius + 1), (int) (2 * radius + 1));
        this.setPreferredSize(new Dimension((int) (2 * radius + 1), (int) (2 * radius + 1)));
        this.setOpaque(false);
        try {
            this.robberImage = ImageIO.read(getClass().getResource("/robber.png"));
            this.robberImage = robberImage.getScaledInstance(35, 35, 1);
            if (number >= 2 && number <= 12) {
                Image img = ImageIO.read(getClass().getResource("/value_chip_" + number + ".png"));
                this.numberImage = img.getScaledInstance(35, 35, 1);
                this.flickerImage = ImageIO.read(getClass().getResource("/flash2.gif"));
                this.flickerImage = flickerImage.getScaledInstance(35, 35, 1);
            }
        } catch (IOException ex) {
            Logger.getLogger(NumberToken.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.imageLabel = new JLabel(new ImageIcon(numberImage));

        add(imageLabel);
        GuiController.getInstance().getDice().addObserver(this);
    }

    public void setNumber(int number, boolean hasRobber) {
        this.hasRobber = hasRobber;
        if (hasRobber) {
            this.imageLabel.setIcon(new ImageIcon(robberImage));
        } else {
            if (number == 0) {
                this.imageLabel.setIcon(null);

            } else if (number > 2) {
                this.number = number;
                Image img = null;
                try {
                    img = ImageIO.read(getClass().getResource("/value_chip_" + number + ".png"));
                } catch (IOException ex) {
                    Logger.getLogger(NumberToken.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.numberImage = img.getScaledInstance(35, 35, 1);
                this.imageLabel.setIcon(new ImageIcon(numberImage));
            }
        }

        repaint();
        revalidate();
    }

    public void flicker(int d) {
        if (!flickering) {
            flickering = true;
            Thread thread = new Thread() {

                @Override
                public void run() {
                    temp = imageLabel.getIcon();
                    try {
                        for (int i = 0; i < 5; i++) {
                            imageLabel.setIcon(new ImageIcon(flickerImage));
                            repaint();
                            revalidate();
                            sleep(150);
                            imageLabel.setIcon(temp);
                            revalidate();
                            repaint();
                            sleep(250);
                        }
                        flickering = false;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NumberToken.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            thread.start();
            revalidate();
        }

    }

    public void update(Observable o, Object b) {
        if (o instanceof Dice) {
            if (((Dice) o).getValue() == number && number != 0 && number != 7 && !hasRobber) {
                flicker(0);
            }
        }
    }
}
