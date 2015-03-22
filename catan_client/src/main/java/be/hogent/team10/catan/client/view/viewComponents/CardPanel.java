/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Brenden Dit paneel wordt gebruikt om ontwikkelingskaarten aan te
 * schaffen dmv rechtermuisklik.
 */
public class CardPanel extends JPanel {

    private GuiController guiC;
    private Image buy, knight, university, grain, wool, ore;
    private JLabel buyCard, buyKnight, buyUniversity, title;
    private JPanel price;
    private int kChanged, uChanged;

    public CardPanel() {
        guiC = GuiController.getInstance();
        setPreferredSize(new Dimension(guiC.getSize("BaPSizew"), guiC.getSize("BaPSizeh")));
        setBackground(Color.decode("#2F4F4F"));
        setLayout(new BorderLayout());
        title = new JLabel("Kaarten");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBackground(Color.decode("#2F4F4F"));
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        CardPanel.CardShop cards = new CardPanel.CardShop();
        guiC.getClient().addObserver(cards);
        price = new JPanel();

        int scale = guiC.getSize("PriceScale");
        try {
            wool = ImageIO.read(getClass().getResource("/WOOLicon.png"));
            wool = wool.getScaledInstance(scale, scale, 1);
            grain = ImageIO.read(getClass().getResource("/GRAINicon.png"));
            grain = grain.getScaledInstance(scale, scale, 1);
            ore = ImageIO.read(getClass().getResource("/OREicon.png"));
            ore = ore.getScaledInstance(scale, scale, 1);
        } catch (IOException ex) {
            Logger.getLogger(CardPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        JLabel prices = new JLabel("Prijs:");
        JLabel one = new JLabel("1");
        prices.setForeground(Color.WHITE);
        one.setForeground(Color.WHITE);
        JLabel one2 = new JLabel("1");
        one2.setForeground(Color.WHITE);
        JLabel one3 = new JLabel("1");
        one3.setForeground(Color.WHITE);
        price.add(prices);
        price.add(new JLabel(new ImageIcon(wool)));
        price.add(one);
        price.add(new JLabel(new ImageIcon(grain)));
        price.add(one2);
        price.add(new JLabel(new ImageIcon(ore)));
        price.add(one3);
        price.setBackground(Color.decode("#2F4F4F"));



        add(title, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(price, BorderLayout.SOUTH);

    }
    //Dit paneel is de kaart die men ziet, dus zonder de titel en prijs

    private class CardShop extends JPanel implements Observer {

        private Image cursor;
        private BuyHandler bh = new BuyHandler();

        public CardShop() {
            renew();
        }

        private void renew() {
            removeAll();
            guiC.addObserver(this);
            setBackground(Color.decode("#2F4F4F"));
            setLayout(new BorderLayout());

            try {
                knight = ImageIO.read(getClass().getResource("/knight_card.jpg"));
                knight = knight.getScaledInstance(guiC.getSize("DevCardX"), guiC.getSize("DevCardY"), 1);
                buy = ImageIO.read(getClass().getResource("/card.jpg"));
                buy = buy.getScaledInstance(guiC.getSize("BuyCardX"), guiC.getSize("BuyCardY"), 1);
                buyKnight = new JLabel(new ImageIcon(knight));
                buyCard = new JLabel(new ImageIcon(buy));
                university = ImageIO.read(getClass().getResource("/university_card.jpg"));
                university = university.getScaledInstance(guiC.getSize("DevCardX"), guiC.getSize("DevCardY"), 1);
                buyUniversity = new JLabel(new ImageIcon(university));

                cursor = ImageIO.read(getClass().getResource("/buy_cursor.gif"));
            } catch (IOException ex) {
                Logger.getLogger(CardPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            buyCard.addMouseListener(bh);
            add(buyCard);
        }
        //observer zal kijken of er een kaart gekocht is en of het mogelijk is een kaart te kopen.

        public void update(Observable o, Object b) {

            if (o instanceof Player) {
                if (guiC.myTurn()) {
                    Player p = (Player) o;
                    int k, u;
                    k = p.getCards(0);
                    u = p.getCards(1);
                    if (k > kChanged) {
                        setCard(buyKnight);
                    } else if (u > uChanged) {
                        setCard(buyUniversity);
                    }
                    kChanged = k;
                    uChanged = u;
                }
            } else if (o instanceof Game && buyCard != null) {
                if (guiC.myTurn() && guiC.checkEnoughResourcesFor("DEVELOPMENTCARD")) {
                    buyCard.setEnabled(true);
                    buyCard.addMouseListener(bh);
                } else {
                    buyCard.setEnabled(false);
                    buyCard.removeMouseListener(bh);
                }
            }

        }
        //verzet de kaart image

        private void setCard(JLabel i) {
            buyCard.removeMouseListener(bh);
            remove(buyCard);
            add(i);

            Timer timer = new Timer(2000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    renew();
                }
            });
            timer.setRepeats(false); // Only execute once
            timer.start(); // Go go go!
        }
        //mouselistener

        private class BuyHandler implements MouseListener {

            public void mouseClicked(MouseEvent e) {

                if (e.isMetaDown() && guiC.getClient() == guiC.getCurrentPlayer() && guiC.checkEnoughResourcesFor("DEVELOPMENTCARD")) {
                    guiC.buyDevelopmentCard();
                    renew();
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == buyCard && guiC.getClient() == guiC.getCurrentPlayer() && guiC.checkEnoughResourcesFor("DEVELOPMENTCARD")) {
                    Point hotspot = new Point(0, 0);
                    String cursorName = "Buy Cursor";
                    setCursor(getToolkit().createCustomCursor(cursor, hotspot, cursorName));
                }
            }

            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
}