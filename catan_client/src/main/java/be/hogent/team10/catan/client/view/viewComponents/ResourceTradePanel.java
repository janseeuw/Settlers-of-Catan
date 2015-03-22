/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Joachim
 */
public class ResourceTradePanel extends JPanel {

    private GuiController guiC;
    private Image plus, min;
    private ImageIcon lumber, ore, wool, brick, grain;
    private JLabel lumberAmount, oreAmount, woolAmount, brickAmount, grainAmount, plusBrick, minBrick, plusLumber, minLumber, plusOre, minOre, plusGrain, minGrain, plusWool, minWool;
    private PlusHandler plusHandler;
    private MinHandler minHandler;
    private String receive;
    private int woolQuantity, grainQuantity, brickQuantity, oreQuantity, lumberQuantity;
    private Map<String,Integer> trade;
    private ResourceSet resources;


    public ResourceTradePanel() {
        try {
            guiC = GuiController.getInstance();
            GridLayout grid = new GridLayout(5, 4);
            grid.setHgap(-10);
            setLayout(grid);
            //Resources ophalen van de speler.
            resources = guiC.getClient().getResources();
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

            plus = ImageIO.read(getClass().getResource("/plus.gif"));
            min = ImageIO.read(getClass().getResource("/min.gif"));
            
            plusHandler=new ResourceTradePanel.PlusHandler();
            minHandler=new ResourceTradePanel.MinHandler();
            
            //Startwaarden van de labels nog instellen.
            //Brick
            minBrick = new JLabel(new ImageIcon(min));
            minBrick.addMouseListener(minHandler);
            add(minBrick);
            add(new JLabel(brick));
            plusBrick = new JLabel(new ImageIcon(plus));
            plusBrick.addMouseListener(plusHandler);
            add(plusBrick);
            brickAmount = new JLabel(brickQuantity+"");
            brickAmount.setForeground(Color.BLACK);
            brickAmount.setHorizontalAlignment(JLabel.CENTER);
            add(brickAmount);
            //Lumber
            minLumber = new JLabel(new ImageIcon(min));
            minLumber.addMouseListener(minHandler);
            add(minLumber);
            add(new JLabel(lumber));
            plusLumber = new JLabel(new ImageIcon(plus));
            plusLumber.addMouseListener(plusHandler);
            add(plusLumber);
            lumberAmount = new JLabel(lumberQuantity+"");
            lumberAmount.setForeground(Color.BLACK);
            lumberAmount.setHorizontalAlignment(JLabel.CENTER);
            add(lumberAmount);

            //Wool
            minWool = new JLabel(new ImageIcon(min));
            minWool.addMouseListener(minHandler);
            add(minWool);
            add(new JLabel(wool));
            plusWool = new JLabel(new ImageIcon(plus));
            plusWool.addMouseListener(plusHandler);
            add(plusWool);
            woolAmount = new JLabel(woolQuantity+"");
            woolAmount.setForeground(Color.BLACK);
            woolAmount.setHorizontalAlignment(JLabel.CENTER);
            add(woolAmount);

            //Grain
            minGrain = new JLabel(new ImageIcon(min));
            minGrain.addMouseListener(minHandler);
            add(minGrain);
            add(new JLabel(grain));
            plusGrain = new JLabel(new ImageIcon(plus));
            plusGrain.addMouseListener(plusHandler);
            add(plusGrain);
            grainAmount = new JLabel(grainQuantity+"");
            grainAmount.setForeground(Color.BLACK);
            grainAmount.setHorizontalAlignment(JLabel.CENTER);
            add(grainAmount);
            

            //Ore
            minOre = new JLabel(new ImageIcon(min));
            minOre.addMouseListener(minHandler);
            add(minOre);
            add(new JLabel(ore));
            plusOre = new JLabel(new ImageIcon(plus));
            plusOre.addMouseListener(plusHandler);
            add(plusOre);
            oreAmount = new JLabel(oreQuantity+"");
            oreAmount.setForeground(Color.BLACK);
            oreAmount.setHorizontalAlignment(JLabel.CENTER);
            add(oreAmount);

        } catch (IOException ex) {
            Logger.getLogger(ResourceTradePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getWoolQuantity() {
        return woolQuantity;
    }

    public int getGrainQuantity() {
        return grainQuantity;
    }

    public int getBrickQuantity() {
        return brickQuantity;
    }

    public int getOreQuantity() {
        return oreQuantity;
    }

    public int getLumberQuantity() {
        return lumberQuantity;
    }

    private class PlusHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            if (source == plusLumber) {
                receive = "LUMBER";
                int get = Integer.parseInt(lumberAmount.getText());
                get++;
                lumberAmount.setText("" + get);
                lumberQuantity = get;
            } else if (source == plusOre) {
                receive = "ORE";
                int get = Integer.parseInt(oreAmount.getText());
                get++;
                oreAmount.setText("" + get);
                oreQuantity = get;
            } else if (source == plusBrick) {
                receive = "BRICK";
                int get = Integer.parseInt(brickAmount.getText());
                get++;
                brickAmount.setText("" + get);
                brickQuantity = get;
            } else if (source == plusWool) {
                receive = "WOOL";
                int get = Integer.parseInt(woolAmount.getText());
                get++;
                woolAmount.setText("" + get);
                woolQuantity = get;
            } else if (source == plusGrain) {
                receive = "GRAIN";
                int get = Integer.parseInt(grainAmount.getText());
                get++;
                grainAmount.setText("" + get);
                grainQuantity = get;
            }
        }
    }
    
    private class MinHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            if (source == minLumber) {
                int get = Integer.parseInt(lumberAmount.getText());
                get--;
                if(get+resources.getAmount("LUMBER")<0){
                    ErrorPanel.getInstance().setError("U hebt niet genoeg grondstoffen om te ruilen.");
                } else if(get+resources.getAmount("LUMBER")>=0){
                    lumberAmount.setText("" + get);
                    lumberQuantity = get;
                }
            } else if (source == minOre) {
                int get = Integer.parseInt(oreAmount.getText());
                get--;
                if(get+resources.getAmount("ORE")<0){
                    ErrorPanel.getInstance().setError("U hebt niet genoeg grondstoffen om te ruilen.");
                } else if(get+resources.getAmount("ORE")>=0){
                    oreAmount.setText("" + get);
                    oreQuantity = get;
                }
            } else if (source == minBrick) {

                int get = Integer.parseInt(brickAmount.getText());
                get--;
                if(get+resources.getAmount("BRICK")<0){
                    ErrorPanel.getInstance().setError("U hebt niet genoeg grondstoffen om te ruilen.");
                } else if(get+resources.getAmount("BRICK")>=0){
                    brickAmount.setText("" + get);
                    brickQuantity = get;
                }
            } else if (source == minWool) {
                int get = Integer.parseInt(woolAmount.getText());
                get--;
                if(get+resources.getAmount("WOOL")<0){
                    ErrorPanel.getInstance().setError("U hebt niet genoeg grondstoffen om te ruilen.");
                } else if(get+resources.getAmount("WOOL")>=0){
                    woolAmount.setText("" + get);
                    woolQuantity = get;
                }
            } else if (source == minGrain) {
                int get = Integer.parseInt(grainAmount.getText());
                get--;
                if(get+resources.getAmount("GRAIN")<0){
                    ErrorPanel.getInstance().setError("U hebt niet genoeg grondstoffen om te ruilen.");
                } else if(get+resources.getAmount("GRAIN")>=0){
                    grainAmount.setText("" + get);
                    grainQuantity = get;
                }
            }
        }
    }
}
