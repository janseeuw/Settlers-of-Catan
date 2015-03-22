package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import be.hogent.team10.catan.client.view.GuiController;
import java.util.HashMap;

/**
 *
 * @author HP,Brenden Dit paneel was deel van sprint 1 om het spelbord zelf te
 * maken. Dit wordt hedendaags niet meer gebruikt en is enkel aanwezig ter
 * volledigheid.
 */
public class InitializationPanel extends JPanel {

    private GuiController guiC;
    private JLabel[] resourceLabels;
    private JLabel[] resourceCountLabels;
    private String[] tileNames = {"DESERT", "BRICK", "GRAIN", "LUMBER", "WOOL", "ORE"};
    private ImageIcon[] images = new ImageIcon[tileNames.length];
    private ResourceHandler res_handler = new ResourceHandler();
    private JLabel previous;

    public InitializationPanel(GuiController guiC) {
        this.guiC = guiC;
        setSize(250, 100);
        setVisible(true);
        setLayout(new GridLayout(12, 3));
        initResources();
    }

    public void updatedata(Map<String, Integer> tilesToPlace) {
        for (int i = 0; i < this.resourceCountLabels.length; i++) {
            this.resourceCountLabels[i].setText(resourceCountLabels[i].getName() + ": " + tilesToPlace.get(resourceCountLabels[i].getName()));

            if (tilesToPlace.get(resourceCountLabels[i].getName()) == 0) {
                resourceLabels[i].setEnabled(false);
                resourceLabels[i].removeMouseListener(res_handler);


            } else {//in case erasor makes another one available.
                resourceLabels[i].setEnabled(true);
                resourceLabels[i].addMouseListener(res_handler);
            }
        }


    }

    private void initResources() {
        resourceLabels = new JLabel[tileNames.length];
        resourceCountLabels = new JLabel[resourceLabels.length];
        for (int i = 0; i < tileNames.length; i++) {
            resourceLabels[i] = new JLabel();
            //resourceLabels[i].setEditable(false);
            resourceLabels[i].addMouseListener(res_handler);
            resourceLabels[i].setHorizontalAlignment(JLabel.CENTER);
            resourceCountLabels[i] = new JLabel();
            resourceCountLabels[i].setHorizontalAlignment(JLabel.CENTER);
            resourceLabels[i].setName(tileNames[i]);
            resourceCountLabels[i].setName(tileNames[i]);
        }
        try {
            images[0] = new ImageIcon(ImageIO.read(getClass().getResource("/DESERT.png")));
            images[1] = new ImageIcon(ImageIO.read(getClass().getResource("/BRICK.png")));
            images[2] = new ImageIcon(ImageIO.read(getClass().getResource("/GRAIN.png")));
            images[3] = new ImageIcon(ImageIO.read(getClass().getResource("/LUMBER.png")));
            images[4] = new ImageIcon(ImageIO.read(getClass().getResource("/WOOL.png")));
            images[5] = new ImageIcon(ImageIO.read(getClass().getResource("/ORE.png")));

        } catch (IOException ex) {
            Logger.getLogger(SidePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        Map<String, Integer> tilesToPlace = new HashMap<String, Integer>();
        for (int i = 0; i < this.tileNames.length; i++) {
            JLabel label = resourceLabels[i];
            JLabel countLabel = resourceCountLabels[i];
            countLabel.setText(tileNames[i] + ": " + tilesToPlace.get(countLabel.getName()));
            countLabel.setForeground(Color.BLACK);
            add(countLabel);
            images[i] = new ImageIcon(images[i].getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            label.setIcon(images[i]);
            add(label);
        }

        JLabel label = new JLabel();
        label.addMouseListener(res_handler);
        label.setText("Eraser");
        label.setName("ERASER");
        label.setForeground(Color.BLACK);
        label.setBackground(UIManager.getColor("Panel.background"));
        // label.setEditable(false);
        label.setHorizontalAlignment(JLabel.CENTER);
        add(label);
    }

    private class ResourceHandler implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if (previous != null) {
                previous.setBorder(BorderFactory.createEmptyBorder());
                previous.setFont(new Font("test", Font.PLAIN, 12));
            }
            JLabel source = (JLabel) e.getSource();
            //source.setEnabled(true);
            guiC.setSelectedTileType(source.getName());
            source.setFont(new Font("test", Font.BOLD, 12));
            source.setBorder(BorderFactory.createBevelBorder(1, Color.BLACK, Color.BLACK));
            previous = source;
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(BorderFactory.createBevelBorder(1, Color.BLACK, Color.BLACK));
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            if (!guiC.getSelectedTileType().equals(source.getName())) {
                source.setBorder(BorderFactory.createEmptyBorder());
            }
        }
    }
}
