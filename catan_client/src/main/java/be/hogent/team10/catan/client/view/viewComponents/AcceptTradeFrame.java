package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Joachim
 */
public class AcceptTradeFrame extends JFrame {

    private ImageIcon lumber, ore, wool, brick, grain;
    private int woolQuantity, grainQuantity, brickQuantity, oreQuantity, lumberQuantity;
    private JLabel lumberAmount, oreAmount, woolAmount, brickAmount, grainAmount,playerName;
    private GuiController guiC;

    public AcceptTradeFrame() {
        try {
            guiC = GuiController.getInstance();
            GridLayout grid = new GridLayout(7, 2);
            grid.setHgap(-10);
            setVisible(true);
            setResizable(false);
            setLayout(grid);
            //Images ophalen
            Image img1 = ImageIO.read(getClass().getResource("/LUMBERicon.png"));
            img1 = img1.getScaledInstance(42, 42, 1);
            Image img2 = ImageIO.read(getClass().getResource("/GRAINicon.png"));
            img2 = img2.getScaledInstance(42, 42, 1);
            Image img3 = ImageIO.read(getClass().getResource("/BRICKicon.png"));
            img3 = img3.getScaledInstance(42, 42, 1);
            Image img4 = ImageIO.read(getClass().getResource("/WOOLicon.png"));
            img4 = img4.getScaledInstance(42, 42, 1);
            Image img5 = ImageIO.read(getClass().getResource("/OREicon.png"));
            img5 = img5.getScaledInstance(42, 42, 1);
            lumber = new ImageIcon(img1);
            grain = new ImageIcon(img2);
            brick = new ImageIcon(img3);
            wool = new ImageIcon(img4);
            ore = new ImageIcon(img5);
            //Speler
            playerName = new JLabel();
            playerName.setHorizontalAlignment(JLabel.CENTER);
            add(playerName);
            JLabel player = new JLabel("wil ruilen met jou!");
            player.setHorizontalAlignment(JLabel.CENTER);
            add(player);
            //Brick
            add(new JLabel(brick));
            brickAmount = new JLabel(brickQuantity + "");
            brickAmount.setForeground(Color.BLACK);
            brickAmount.setHorizontalAlignment(JLabel.CENTER);
            add(brickAmount);
            //Lumber
            add(new JLabel(lumber));
            lumberAmount = new JLabel(lumberQuantity + "");
            lumberAmount.setForeground(Color.BLACK);
            lumberAmount.setHorizontalAlignment(JLabel.CENTER);
            add(lumberAmount);

            //Wool
            add(new JLabel(wool));
            woolAmount = new JLabel(woolQuantity + "");
            woolAmount.setForeground(Color.BLACK);
            woolAmount.setHorizontalAlignment(JLabel.CENTER);
            add(woolAmount);

            //Grain
            add(new JLabel(grain));
            grainAmount = new JLabel(grainQuantity + "");
            grainAmount.setForeground(Color.BLACK);
            grainAmount.setHorizontalAlignment(JLabel.CENTER);
            add(grainAmount);


            //Ore
            add(new JLabel(ore));
            oreAmount = new JLabel(oreQuantity + "");
            oreAmount.setForeground(Color.BLACK);
            oreAmount.setHorizontalAlignment(JLabel.CENTER);
            add(oreAmount);

            JButton decline = new JButton("Weiger");
            decline.addMouseListener(new DeclineListener());
            JButton accept = new JButton("Accepteer");
            accept.addMouseListener(new AcceptListener());
            add(decline);
            add(accept);
        } catch (IOException ex) {
            Logger.getLogger(AcceptTradeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setWoolQuantity(int woolQuantity) {
        this.woolQuantity = woolQuantity;
        woolAmount.setText(this.woolQuantity+"");
    }

    public void setGrainQuantity(int grainQuantity) {
        this.grainQuantity = grainQuantity;
        grainAmount.setText(this.grainQuantity+"");
    }

    public void setBrickQuantity(int brickQuantity) {
        this.brickQuantity = brickQuantity;
        brickAmount.setText(this.brickQuantity+"");
    }

    public void setOreQuantity(int oreQuantity) {
        this.oreQuantity = oreQuantity;
        oreAmount.setText(this.oreQuantity+"");
    }

    public void setLumberQuantity(int lumberQuantity) {
        this.lumberQuantity = lumberQuantity;
        lumberAmount.setText(this.lumberQuantity+"");
    }
    
    public void setPlayerNameText(String playerName){
        this.playerName.setText(playerName);
    }

    private class AcceptListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            guiC.replyTrade(true);
            dispose();
        }
    }

    private class DeclineListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            guiC.replyTrade(false);
            dispose();
        }
    }
}
