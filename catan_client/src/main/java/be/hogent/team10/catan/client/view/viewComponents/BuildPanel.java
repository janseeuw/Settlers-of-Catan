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
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.gameState.SetupState;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;

/**
 *
 * @author Brenden Dit paneel breidt het PlaceComponentspaneel uit met stad en
 * ontwikkelingskaarten.
 */
public class BuildPanel extends PlaceComponentsPanel implements Observer {

    private JPanel lower;
    private JPanel cards;
    private JLabel cityCount, city;
    private BuildHandler BH = new BuildHandler();
    private JLabel knightCount, knight, uniCount, uni;

    public BuildPanel(GuiController guiC) {
        super(guiC);
        this.setPreferredSize(new Dimension(guiC.getSize("BuPSizew"), guiC.getSize("BuPSizeh")));
        this.setLayout(new GridLayout(5, 1));
        super.street.addMouseListener(BH);
        super.settl.addMouseListener(BH);
        setMinimumSize(new Dimension(300, 280));
        super.title.setPreferredSize(new Dimension(guiC.getSize("BuPSmallw"), guiC.getSize("BuPSmallh")));
        super.upper.setPreferredSize(new Dimension(guiC.getSize("BuPSmallw"), guiC.getSize("BuPSmallh")));
        super.mid.setPreferredSize(new Dimension(guiC.getSize("BuPSmallw"), guiC.getSize("BuPSmallh")));
        for (int i = 0; i < super.childLabels.size(); i++) {
            super.childLabels.get(i).setText("1");
        }

        lower = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lower.setPreferredSize(new Dimension(guiC.getSize("BuPSmallw"), guiC.getSize("BuPSmallh")));
        lower.setBackground(Color.decode("#2F4F4F"));

        cards = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cards.setPreferredSize(new Dimension(guiC.getSize("BuPSmallw"), guiC.getSize("BuPSmallh")));
        cards.setBackground(Color.decode("#2F4F4F"));


        cityCount = new JLabel("(" + guiC.getAvailableCities(guiC.getClient()) + ")");
        lower.add(cityCount);
        city = new JLabel();
        try {
            Image img = ImageIO.read(getClass().getResource("/CITY." + guiC.getClient().getPlayerColor().name() + ".png"));
            img = img.getScaledInstance(guiC.getSize("BuPScaled"), guiC.getSize("BuPScaled"), 1);
            city.setIcon(new ImageIcon(img));

            city.addMouseListener(BH);
            lower.add(city);

            lower.add(new JLabel(ore));
            lower.add(new JLabel("3"));
            lower.add(new JLabel(grain));
            lower.add(new JLabel("2"));

            Component lowerComponents[] = lower.getComponents();
            for (int i = 0; i < lowerComponents.length; i++) {
                lowerComponents[i].setForeground(Color.WHITE);
            }

            Image k = ImageIO.read(getClass().getResource("/knight.gif"));
            k = k.getScaledInstance(guiC.getSize("BuPScaled"), guiC.getSize("BuPScaled"), 1);
            knight = new JLabel(new ImageIcon(k));
            knight.setEnabled(false);
            Image u = ImageIO.read(getClass().getResource("/uni.gif"));
            u = u.getScaledInstance(guiC.getSize("BuPScaled"), guiC.getSize("BuPScaled"), 1);
            uni = new JLabel(new ImageIcon(u));
            uni.setEnabled(false);
            knightCount = new JLabel("(0)");
            knightCount.setForeground(Color.WHITE);
            uniCount = new JLabel("(0)");
            uniCount.setForeground(Color.WHITE);

            cards.add(knightCount);
            cards.add(knight);
            cards.add(uniCount);
            cards.add(uni);

        } catch (IOException ex) {
            Logger.getLogger(BuildPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        add(lower);
        add(cards);
        street.setEnabled(guiC.checkEnoughResourcesFor("STREET"));
        settl.setEnabled(guiC.checkEnoughResourcesFor("SETTLEMENT"));
        city.setEnabled(false);
        revalidate();
    }
    //update methode die bepaalt wanneer men mag bouwen adhv de spelregels

    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof SetupState) {
            street.setEnabled(false);
            settl.setEnabled(false);
        } else if (o instanceof Player) {
            Player p = ((Player) o);
            if (super.streetCount != null) {
                super.streetCount.setText("(" + p.getAvailableStreets() + ")");
            }
            if (super.settlCount != null) {
                super.settlCount.setText("(" + p.getAvailableSettlements() + ")");
            }
            if (this.cityCount != null) {
                this.cityCount.setText("(" + p.getAvailableCities() + ")");
            }
            if (this.knightCount != null) {
                this.knightCount.setText("(" + p.getCards(0) + ")");
            }
            if (this.uniCount != null) {
                this.uniCount.setText("(" + p.getCards(1) + ")");
            }
            if (guiC.myTurn() && street != null) {




                if (guiC.checkEnoughResourcesFor("STREET") && p.getAvailableStreets() > 0) {
                    street.setEnabled(true);
                    street.addMouseListener(BH);
                } else {
                    street.setEnabled(false);
                    street.removeMouseListener(BH);
                }
                if (guiC.checkEnoughResourcesFor("SETTLEMENT") && p.getAvailableSettlements() > 0) {
                    settl.setEnabled(true);
                    settl.addMouseListener(BH);
                } else {
                    settl.setEnabled(false);
                    settl.removeMouseListener(BH);
                }
                if (guiC.checkEnoughResourcesFor("CITY") && p.getAvailableCities() > 0) {
                    city.setEnabled(true);
                    city.addMouseListener(BH);
                } else {
                    city.setEnabled(false);
                    city.removeMouseListener(BH);
                }

                if (guiC.getClient().getCards(0) > 0) {
                    knight.setEnabled(true);
                    knight.addMouseListener(BH);
                } else {
                    knight.setEnabled(false);
                    knight.removeMouseListener(BH);
                }
                if (guiC.getClient().getCards(1) > 0) {
                    uni.setEnabled(true);
                    uni.addMouseListener(BH);
                } else {
                    uni.setEnabled(false);
                    uni.removeMouseListener(BH);
                }
            } else if (street != null && city != null) {
                street.setEnabled(false);
                street.removeMouseListener(BH);
                settl.setEnabled(false);
                settl.removeMouseListener(BH);
                city.setEnabled(false);
                city.removeMouseListener(BH);
            }
        }
        revalidate();
    }
    //mouselistener
    //setSelectedSetupElement wordt in de GuiController gebruikt om te bepalen wat men bouwt
    //dubbelklik voor kaarten uit te spelen

    private class BuildHandler implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            JLabel source = ((JLabel) e.getSource());
            if (e.getClickCount() == 2 && !e.isConsumed()) {
                e.consume();
                if (e.getSource() == knight) {

                    guiC.playKnight();
                } else if (e.getSource() == uni) {
                    guiC.playUniversity();

                }
            } else {
                if (source == settl) {

                    settl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    city.setBorder(BorderFactory.createEmptyBorder());
                    street.setBorder(BorderFactory.createEmptyBorder());
                    //method setSettl
                    GuiController.getInstance().setNodesOnTop();
                    GuiController.getInstance().addListenersNodes();
                    GuiController.getInstance().removeListenersStreets();
                    guiC.setSelectedSetupElement("SETTLEMENT");
                } else if (source == street) {
                    settl.setBorder(BorderFactory.createEmptyBorder());
                    street.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    city.setBorder(BorderFactory.createEmptyBorder());
                    //method setStreet
                    GuiController.getInstance().setStreetsOnTop();
                    GuiController.getInstance().addListenersStreets();
                    GuiController.getInstance().removeListenersNodes();
                    guiC.setSelectedSetupElement("STREET");
                } else if (source == city) {
                    city.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    settl.setBorder(BorderFactory.createEmptyBorder());
                    street.setBorder(BorderFactory.createEmptyBorder());
                    //method setSettl
                    GuiController.getInstance().setNodesOnTop();
                    GuiController.getInstance().addListenersNodes();
                    GuiController.getInstance().removeListenersStreets();
                    guiC.setSelectedSetupElement("CITY");
                }
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
