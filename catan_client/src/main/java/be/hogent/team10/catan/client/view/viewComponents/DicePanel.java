package be.hogent.team10.catan.client.view.viewComponents;


import be.hogent.team10.catan.client.util.AudioStream;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.util.Dice;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import javax.swing.JButton;

/**
 *
 * @author Brenden
 * Dit paneel is bedoeld om de mogelijkheid te geven om met de dobbelsteen te rollen en/of naar de volgende speler te gaan.
 * Er is een animatie van rollende dobbelsteen en de dobbelsteen geeft de juiste waarde weer.
 */
public class DicePanel extends JPanel implements Observer {

    private ImageIcon dice_station;
    private ImageIcon dice_move;
    private JLabel roll;
    private JLabel dice;
    private JButton turn;
    private DiceHandler dice_handler = new DiceHandler();
    private TurnHandler turnHandler = new TurnHandler();
    

    public DicePanel() {
        this.setLayout(new GridLayout(3, 1));
        this.setPreferredSize(new Dimension( GuiController.getInstance().getSize("DPSizew"), GuiController.getInstance().getSize("DPSizeh")));
        GuiController.getInstance().addObserver(this);
        roll = new JLabel("Current roll: 0");
        roll.setHorizontalAlignment(JLabel.CENTER);
        this.add(roll);
        dice_station = new ImageIcon(getClass().getResource("/dice_9.gif"), "dice");
        dice_move = new ImageIcon(getClass().getResource("/dice_move.gif"), "dice");
        
        dice = new JLabel();
        dice.setIcon(dice_station);
        dice.setEnabled(false);
        dice.setHorizontalAlignment(JLabel.CENTER);
        this.add(dice);
        turn = new JButton("Einde beurt");
        turn.setEnabled(false);
        this.add(turn);
        GuiController.getInstance().getDice().addObserver(this);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        
        if (o instanceof Dice) {
            Dice d = ((Dice) o);
            int val=((Dice) o).getValue();
            roll.setText("Current roll: " +val );     
            dice_station = new ImageIcon(getClass().getResource("/dice_"+(val==0?7:val)+".gif"), "dice");
            this.revalidate();
        }
        else if (o instanceof Game && dice !=null){
            if(GuiController.getInstance().myTurn()){
                dice.setEnabled(true);
                dice.addMouseListener(dice_handler);
                turn.setEnabled(true);
                turn.addMouseListener(turnHandler);
            }
            else{
                dice.setEnabled(false);
                dice.removeMouseListener(dice_handler);
                turn.setEnabled(false);
                turn.removeMouseListener(turnHandler);
            }
        }
    }

    /**
     * Mouselistener for dices
     */
    private class DiceHandler implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            dice.setIcon(dice_move);
            GuiController.getInstance().rollDice();
            AudioStream.getInstance().playDice();
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
               //     current_roll = GuiController.getInstance().getDiceValue();
                    dice.setIcon(dice_station);
               //     roll.setText("Current roll: " + current_roll);
               //     GuiController.getInstance().flicker(current_roll);
                }
            });
            timer.setRepeats(false); // Only execute once
            timer.start(); // Go go go!
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    private class TurnHandler implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            GuiController.getInstance().endTurn();
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }
}
