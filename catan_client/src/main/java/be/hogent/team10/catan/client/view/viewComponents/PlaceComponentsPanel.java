package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.util.Observer;
import be.hogent.team10.catan_businesslogic.util.Observable;

/**
 *
 * @author Brenden
 */
public class PlaceComponentsPanel extends JPanel implements Observer {

    protected GuiController guiC;
    protected JPanel title;
    protected JPanel upper;
    protected JPanel mid;
    protected JLabel streetCount, settlCount;
    protected JLabel street, settl;
    protected ImageIcon lumber, ore, wool, brick, grain;
    protected ArrayList<JLabel> childLabels = new ArrayList<JLabel>();
    private BuildHandler BH = new BuildHandler();

    public PlaceComponentsPanel(GuiController guiC) {
        this.guiC = guiC;
        guiC.getClient().addObserver(this);

        this.setPreferredSize(new Dimension(guiC.getSize("PCPSizew"), guiC.getSize("PCPSizeh")));
        this.setLayout(new GridLayout(3, 1));
        setBackground(Color.decode("#2F4F4F"));
        title = new JPanel();
        JLabel buildtitle = new JLabel("Acties");
        title.setPreferredSize(new Dimension(guiC.getSize("PCPSmallw"), guiC.getSize("PCPSmallh")));
        title.setBackground(Color.decode("#2F4F4F"));
        buildtitle.setFont(new Font("Serif", Font.BOLD, 30));
        buildtitle.setForeground(Color.WHITE);
        title.add(buildtitle);


        upper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        upper.setPreferredSize(new Dimension(guiC.getSize("PCPSmallw"), guiC.getSize("PCPSmallh")));
        upper.setBackground(Color.decode("#2F4F4F"));

        mid = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mid.setPreferredSize(new Dimension(guiC.getSize("PCPSmallw"), guiC.getSize("PCPSmallh")));
        mid.setBackground(Color.decode("#2F4F4F"));
        BH = new BuildHandler();
        try {
            int scale = guiC.getSize("PriceScale");
            Image img1 = ImageIO.read(getClass().getResource("/LUMBERicon.png"));
            img1 = img1.getScaledInstance(scale, scale, 1);
            Image img2 = ImageIO.read(getClass().getResource("/GRAINicon.png"));
            img2 = img2.getScaledInstance(scale, scale, 1);
            Image img3 = ImageIO.read(getClass().getResource("/BRICKicon.png"));
            img3 = img3.getScaledInstance(scale, scale, 1);
            Image img4 = ImageIO.read(getClass().getResource("/WOOLicon.png"));
            img4 = img4.getScaledInstance(scale, scale, 1);
            Image img5 = ImageIO.read(getClass().getResource("/OREicon.png"));
            img5 = img5.getScaledInstance(scale, scale, 1);
            lumber = new ImageIcon(img1);
            grain = new ImageIcon(img2);
            brick = new ImageIcon(img3);
            wool = new ImageIcon(img4);
            ore = new ImageIcon(img5);

            //street row=upper
            streetCount = new JLabel("(" + guiC.getAvailableStreets(guiC.getClient()) + ")");
            upper.add(streetCount);

            street = new JLabel();
            String temp = "/" + "road_eastwest_" + guiC.getClient().getPlayerColor().name().toLowerCase() + ".png";
            street.setIcon(new ImageIcon(ImageIO.read(getClass().getResource(temp.toLowerCase()))));
            street.setEnabled(true);
            street.addMouseListener(BH);
            upper.add(street);

            upper.add(new JLabel(lumber));
            JLabel temp1 = new JLabel("0");
            upper.add(temp1);
            childLabels.add(temp1);
            upper.add(new JLabel(brick));
            JLabel temp2 = new JLabel("0");
            upper.add(temp2);
            childLabels.add(temp2);
            Component upperComponents[] = upper.getComponents();
            for (int i = 0; i < upperComponents.length; i++) {
                upperComponents[i].setForeground(Color.WHITE);
            }
            //settlement row=mid
            settlCount = new JLabel("(" + guiC.getAvailableSettlements(guiC.getClient()) + ")");
            mid.add(settlCount);
            settl = new JLabel();
            settl.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/SETTLEMENT." + guiC.getClient().getPlayerColor().name() + ".png"))));
            settl.setEnabled(true);
            settl.addMouseListener(BH);
            mid.add(settl);

            mid.add(new JLabel(lumber));
            JLabel temp3 = new JLabel("0");
            mid.add(temp3);
            childLabels.add(temp3);
            mid.add(new JLabel(grain));
            JLabel temp4 = new JLabel("0");
            mid.add(temp4);
            childLabels.add(temp4);
            mid.add(new JLabel(brick));
            JLabel temp5 = new JLabel("0");
            mid.add(temp5);
            childLabels.add(temp5);
            mid.add(new JLabel(wool));
            JLabel temp6 = new JLabel("0");
            mid.add(temp6);
            childLabels.add(temp6);

            Component midComponents[] = mid.getComponents();
            for (int i = 0; i < midComponents.length; i++) {
                midComponents[i].setForeground(Color.WHITE);
            }

        } catch (IOException ex) {
            Logger.getLogger(BuildPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        add(title);
        add(upper);
        add(mid);
        revalidate();
    }

    public void update(Observable o, Object arg) {
        if (o.getClass() == Player.class) {
            Player p = ((Player) o);
            if (streetCount != null) {
                streetCount.setText("(" + p.getAvailableStreets() + ")");
            }
            if (settlCount != null) {
                settlCount.setText("(" + p.getAvailableSettlements() + ")");
            }
        } else {
            if (guiC.getCurrentPlayer() == guiC.getClient()) {
                street.setEnabled(true);
                street.addMouseListener(BH);
                settl.setEnabled(true);
                settl.addMouseListener(BH);
            } else {
                street.setEnabled(false);
                street.removeMouseListener(BH);
                settl.setEnabled(false);
                settl.removeMouseListener(BH);
            }
        }
        revalidate();
    }

    //will start the game by giving first player rights to build
    public void enableBuilding() {
        guiC.addObserver(this);
        street.setEnabled(true);
        street.addMouseListener(BH);

        settl.setEnabled(true);
        settl.addMouseListener(BH);
    }

    private class BuildHandler implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            JLabel source = ((JLabel) e.getSource());
            if (source == settl) {

                settl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                street.setBorder(BorderFactory.createEmptyBorder());
                //method setSettl
                GuiController.getInstance().setNodesOnTop();
                GuiController.getInstance().addListenersNodes();
                GuiController.getInstance().removeListenersStreets();
                guiC.setSelectedSetupElement("SETTLEMENT");
            } else if (source == street) {
                settl.setBorder(BorderFactory.createEmptyBorder());
                street.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                //method setStreet
                GuiController.getInstance().setStreetsOnTop();
                GuiController.getInstance().addListenersStreets();
                GuiController.getInstance().removeListenersNodes();
                guiC.setSelectedSetupElement("STREET");
            }
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
