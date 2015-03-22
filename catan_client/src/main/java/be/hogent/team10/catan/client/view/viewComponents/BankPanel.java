package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Brenden Dit paneel staat in het SidePanel en representeert de
 * volledige bank. Ook is het mogelijk te ruilen via de diverse plus en min
 * icoontjes
 */
public class BankPanel extends JPanel implements Observer {

    private GuiController guiC;
    private ImageIcon lumber, ore, wool, brick, grain;
    private JLabel lumberAmount, oreAmount, woolAmount, brickAmount, grainAmount, plusBrick, minBrick, plusLumber, minLumber, plusOre, minOre, plusGrain, minGrain, plusWool, minWool;
    //private JLabel minLowerBrick,plusLowerBrick,minLowerLumber,plusLowerLumber,minLowerWool,plusLowerWool,minLowerOre,plusLowerOre,minLowerGrain,plusLowerGrain;
    private JTextField tradeLumber, tradeOre, tradeWool, tradeBrick, tradeGrain;
    private JTextField lowerLumber, lowerOre, lowerWool, lowerBrick, lowerGrain;
    private JLabel get;
    private Map<String, Integer> bank;
    private JPanel title = new JPanel();
    private JPanel table = new JPanel();
    private Image plus, min, okay;
    private JLabel lumberOkay, oreOkay, woolOkay, grainOkay, brickOkay;
    private BankPanel.PlusHandler plusHandler;
    private BankPanel.MinHandler minHandler;
    private BankPanel.OkayHandler okayHandler;
    private String receive;
    private int quantity;

    public BankPanel() {
        guiC = GuiController.getInstance();
        guiC.addObserver(this);
        setBackground(Color.decode("#2F4F4F"));
        title.setBackground(Color.decode("#2F4F4F"));
        table.setBackground(Color.decode("#2F4F4F"));
        setPreferredSize(new Dimension(guiC.getSize("BaPSizew"), guiC.getSize("BaPSizeh")));
        GridLayout grid = new GridLayout(5, 7);
        grid.setHgap(-10);
        setLayout(new BorderLayout());
        table.setLayout(grid);
        bank = guiC.getBankResources();
        plusHandler = new BankPanel.PlusHandler();
        minHandler = new BankPanel.MinHandler();
        okayHandler = new BankPanel.OkayHandler();

        try {
            Image img1 = ImageIO.read(getClass().getResource("/LUMBERicon.png"));
            img1 = img1.getScaledInstance(guiC.getSize("BaPScale"), guiC.getSize("BaPScale"), 1);
            Image img2 = ImageIO.read(getClass().getResource("/GRAINicon.png"));
            img2 = img2.getScaledInstance(guiC.getSize("BaPScale"), guiC.getSize("BaPScale"), 1);
            Image img3 = ImageIO.read(getClass().getResource("/BRICKicon.png"));
            img3 = img3.getScaledInstance(guiC.getSize("BaPScale"), guiC.getSize("BaPScale"), 1);
            Image img4 = ImageIO.read(getClass().getResource("/WOOLicon.png"));
            img4 = img4.getScaledInstance(guiC.getSize("BaPScale"), guiC.getSize("BaPScale"), 1);
            Image img5 = ImageIO.read(getClass().getResource("/OREicon.png"));
            img5 = img5.getScaledInstance(guiC.getSize("BaPScale"), guiC.getSize("BaPScale"), 1);
            lumber = new ImageIcon(img1);
            grain = new ImageIcon(img2);
            brick = new ImageIcon(img3);
            wool = new ImageIcon(img4);
            ore = new ImageIcon(img5);

            plus = ImageIO.read(getClass().getResource("/plus.gif"));
            plus = plus.getScaledInstance(guiC.getSize("BaPScale"), guiC.getSize("BaPScale"), 1);
            min = ImageIO.read(getClass().getResource("/min.gif"));
            min = min.getScaledInstance(guiC.getSize("BaPScale"), guiC.getSize("BaPScale"), 1);
            okay = ImageIO.read(getClass().getResource("/okay.gif"));
            okay = okay.getScaledInstance(guiC.getSize("BaPScale") - 10, guiC.getSize("BaPScale") - 10, 1);
        } catch (IOException ex) {
            Logger.getLogger(BankPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        JLabel banktitle = new JLabel("BANK");
        banktitle.setFont(new Font("Serif", Font.BOLD, 30));
        banktitle.setForeground(Color.WHITE);
        title.add(banktitle);


        table.add(new JLabel(brick));
        brickAmount = new JLabel("" + bank.get("BRICK"));
        brickAmount.setForeground(Color.WHITE);
        brickAmount.setHorizontalAlignment(JLabel.CENTER);
        table.add(brickAmount);

        plusBrick = new JLabel(new ImageIcon(plus));
        plusBrick.addMouseListener(plusHandler);
        table.add(plusBrick);
        tradeBrick = new JTextField("0");
        tradeBrick.setHorizontalAlignment(JTextField.CENTER);
        tradeBrick.setEnabled(false);
        tradeBrick.setBackground(Color.decode("#2F4F4F"));
        tradeBrick.setForeground(Color.GREEN);
        tradeBrick.setBorder(BorderFactory.createEmptyBorder());
        table.add(tradeBrick);
        minBrick = new JLabel(new ImageIcon(min));
        minBrick.addMouseListener(minHandler);
        table.add(minBrick);
        //minLowerBrick=new JLabel(new ImageIcon(min));
        //minLowerBrick.addMouseListener(minLowerHandler);
        //table.add(minLowerBrick);
        lowerBrick = new JTextField("0");
        lowerBrick.setHorizontalAlignment(JTextField.CENTER);
        lowerBrick.setEnabled(false);
        lowerBrick.setBackground(Color.decode("#2F4F4F"));
        lowerBrick.setForeground(Color.RED);
        lowerBrick.setBorder(BorderFactory.createEmptyBorder());
        table.add(lowerBrick);
        //plusLowerBrick=new JLabel(new ImageIcon(plus));
        //plusLowerBrick.addMouseListener(plusLowerHandler);
        //table.add(plusLowerBrick);
        brickOkay = new JLabel(new ImageIcon(okay));
        brickOkay.addMouseListener(okayHandler);
        table.add(brickOkay);

        table.add(new JLabel(lumber));
        lumberAmount = new JLabel("" + bank.get("LUMBER"));
        lumberAmount.setForeground(Color.WHITE);
        lumberAmount.setHorizontalAlignment(JLabel.CENTER);
        table.add(lumberAmount);
        plusLumber = new JLabel(new ImageIcon(plus));
        plusLumber.addMouseListener(plusHandler);
        table.add(plusLumber);
        tradeLumber = new JTextField("0");
        tradeLumber.setHorizontalAlignment(JTextField.CENTER);
        tradeLumber.setEnabled(false);
        tradeLumber.setBackground(Color.decode("#2F4F4F"));
        tradeLumber.setForeground(Color.GREEN);
        tradeLumber.setBorder(BorderFactory.createEmptyBorder());
        table.add(tradeLumber);
        minLumber = new JLabel(new ImageIcon(min));
        minLumber.addMouseListener(minHandler);
        table.add(minLumber);
        //minLowerLumber=new JLabel(new ImageIcon(min));
        //minLowerLumber.addMouseListener(minLowerHandler);
        //table.add(minLowerLumber);
        lowerLumber = new JTextField("0");
        lowerLumber.setHorizontalAlignment(JTextField.CENTER);
        lowerLumber.setEnabled(false);
        lowerLumber.setBackground(Color.decode("#2F4F4F"));
        lowerLumber.setForeground(Color.RED);
        lowerLumber.setBorder(BorderFactory.createEmptyBorder());
        table.add(lowerLumber);
        //plusLowerLumber=new JLabel(new ImageIcon(plus));
        //plusLowerLumber.addMouseListener(plusLowerHandler);
        //table.add(plusLowerLumber);
        lumberOkay = new JLabel(new ImageIcon(okay));
        lumberOkay.addMouseListener(okayHandler);
        table.add(lumberOkay);

        table.add(new JLabel(wool));
        woolAmount = new JLabel("" + bank.get("WOOL"));
        woolAmount.setForeground(Color.WHITE);
        woolAmount.setHorizontalAlignment(JLabel.CENTER);
        table.add(woolAmount);
        plusWool = new JLabel(new ImageIcon(plus));
        plusWool.addMouseListener(plusHandler);
        table.add(plusWool);
        tradeWool = new JTextField("0");
        tradeWool.setHorizontalAlignment(JTextField.CENTER);
        tradeWool.setEnabled(false);
        tradeWool.setBackground(Color.decode("#2F4F4F"));
        tradeWool.setForeground(Color.GREEN);
        tradeWool.setBorder(BorderFactory.createEmptyBorder());
        table.add(tradeWool);
        minWool = new JLabel(new ImageIcon(min));
        minWool.addMouseListener(minHandler);
        table.add(minWool);
        //minLowerWool=new JLabel(new ImageIcon(min));
        //minLowerWool.addMouseListener(minLowerHandler);
        //table.add(minLowerWool);
        lowerWool = new JTextField("0");
        lowerWool.setHorizontalAlignment(JTextField.CENTER);
        lowerWool.setEnabled(false);
        lowerWool.setBackground(Color.decode("#2F4F4F"));
        lowerWool.setForeground(Color.RED);
        lowerWool.setBorder(BorderFactory.createEmptyBorder());
        table.add(lowerWool);
        //plusLowerWool=new JLabel(new ImageIcon(plus));
        //plusLowerWool.addMouseListener(plusLowerHandler);
        //table.add(plusLowerWool);
        woolOkay = new JLabel(new ImageIcon(okay));
        woolOkay.addMouseListener(okayHandler);
        table.add(woolOkay);

        table.add(new JLabel(grain));
        grainAmount = new JLabel("" + bank.get("GRAIN"));
        grainAmount.setForeground(Color.WHITE);
        grainAmount.setHorizontalAlignment(JLabel.CENTER);
        table.add(grainAmount);
        plusGrain = new JLabel(new ImageIcon(plus));
        plusGrain.addMouseListener(plusHandler);
        table.add(plusGrain);
        tradeGrain = new JTextField("0");
        tradeGrain.setHorizontalAlignment(JTextField.CENTER);
        tradeGrain.setEnabled(false);
        tradeGrain.setBackground(Color.decode("#2F4F4F"));
        tradeGrain.setForeground(Color.GREEN);
        tradeGrain.setBorder(BorderFactory.createEmptyBorder());
        table.add(tradeGrain);
        minGrain = new JLabel(new ImageIcon(min));
        minGrain.addMouseListener(minHandler);
        table.add(minGrain);
        //minLowerGrain=new JLabel(new ImageIcon(min));
        //minLowerGrain.addMouseListener(minLowerHandler);
        //table.add(minLowerGrain);
        lowerGrain = new JTextField("0");
        lowerGrain.setHorizontalAlignment(JTextField.CENTER);
        lowerGrain.setEnabled(false);
        lowerGrain.setBackground(Color.decode("#2F4F4F"));
        lowerGrain.setForeground(Color.RED);
        lowerGrain.setBorder(BorderFactory.createEmptyBorder());
        table.add(lowerGrain);
        //plusLowerGrain=new JLabel(new ImageIcon(plus));
        //plusLowerGrain.addMouseListener(plusLowerHandler);
        //table.add(plusLowerGrain);
        grainOkay = new JLabel(new ImageIcon(okay));
        grainOkay.addMouseListener(okayHandler);
        table.add(grainOkay);

        table.add(new JLabel(ore));
        oreAmount = new JLabel("" + bank.get("ORE"));
        oreAmount.setForeground(Color.WHITE);
        oreAmount.setHorizontalAlignment(JLabel.CENTER);
        table.add(oreAmount);
        plusOre = new JLabel(new ImageIcon(plus));
        plusOre.addMouseListener(plusHandler);
        table.add(plusOre);
        tradeOre = new JTextField("0");
        tradeOre.setHorizontalAlignment(JTextField.CENTER);
        tradeOre.setEnabled(false);
        tradeOre.setBackground(Color.decode("#2F4F4F"));
        tradeOre.setForeground(Color.GREEN);
        tradeOre.setBorder(BorderFactory.createEmptyBorder());
        table.add(tradeOre);
        minOre = new JLabel(new ImageIcon(min));
        minOre.addMouseListener(minHandler);
        table.add(minOre);
        //minLowerOre=new JLabel(new ImageIcon(min));
        //minLowerOre.addMouseListener(minLowerHandler);
        //table.add(minLowerOre);
        lowerOre = new JTextField("0");
        lowerOre.setHorizontalAlignment(JTextField.CENTER);
        lowerOre.setEnabled(false);
        lowerOre.setBackground(Color.decode("#2F4F4F"));
        lowerOre.setForeground(Color.RED);
        lowerOre.setBorder(BorderFactory.createEmptyBorder());
        table.add(lowerOre);
        //plusLowerOre=new JLabel(new ImageIcon(plus));
        //plusLowerOre.addMouseListener(plusLowerHandler);
        //table.add(plusLowerOre);
        oreOkay = new JLabel(new ImageIcon(okay));
        oreOkay.addMouseListener(okayHandler);
        table.add(oreOkay);

        //minLowerLumber.setVisible(false);
        lowerLumber.setVisible(false);
        lumberOkay.setVisible(false);
        //plusLowerLumber.setVisible(false);
        //minLowerOre.setVisible(false);
        lowerOre.setVisible(false);
        oreOkay.setVisible(false);
        //plusLowerOre.setVisible(false);
        //minLowerGrain.setVisible(false);
        lowerGrain.setVisible(false);
        grainOkay.setVisible(false);
        //plusLowerGrain.setVisible(false);
        //minLowerBrick.setVisible(false);
        lowerBrick.setVisible(false);
        brickOkay.setVisible(false);
        //plusLowerBrick.setVisible(false);
        //minLowerWool.setVisible(false);
        lowerWool.setVisible(false);
        woolOkay.setVisible(false);
        add(title, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);

        guiC.getClient().addObserver(this);
    }

    //de observer update player=>aangepaste resources en game==>of je aan de beurt bent
    public void update(Observable o, Object arg) {
        if (o instanceof Player && brickAmount != null) {
            brickAmount.setText("" + bank.get("BRICK"));
            lumberAmount.setText("" + bank.get("LUMBER"));
            woolAmount.setText("" + bank.get("WOOL"));
            grainAmount.setText("" + bank.get("GRAIN"));
            oreAmount.setText("" + bank.get("ORE"));
            table.revalidate();
        } 
    }

    private void enableTrades(String send, int get) {
        disableAllLowerTrades();
        ArrayList<String> niet = new ArrayList<String>();
        HashMap<String, Integer> freq = new HashMap<String, Integer>();
        freq.put("LUMBER", 0);
        freq.put("ORE", 0);
        freq.put("BRICK", 0);
        freq.put("WOOL", 0);
        freq.put("GRAIN", 0);
        for (String res : guiC.getTradeableBankResources(send, get)) {
            int temp = freq.get(res);
            temp++;
            freq.put(res, temp);
            int numb = get * (-4);
            if (res.equals("LUMBER")) {
                lowerLumber.setVisible(true);
                lowerLumber.setText("" + numb);
                lumberOkay.setVisible(true);

            }
            if (res.equals("ORE")) {

                lowerOre.setVisible(true);
                lowerOre.setText("" + numb);
                oreOkay.setVisible(true);

            }
            if (res.equals("WOOL")) {

                lowerWool.setVisible(true);
                lowerWool.setText("" + numb);
                woolOkay.setVisible(true);

            }
            if (res.equals("GRAIN")) {

                lowerGrain.setVisible(true);
                lowerGrain.setText("" + numb);
                grainOkay.setVisible(true);

            }
            if (res.equals("BRICK")) {

                lowerBrick.setVisible(true);
                lowerBrick.setText("" + numb);
                brickOkay.setVisible(true);

            }
        }
        for (Entry entry : freq.entrySet()) {
            if ((Integer) entry.getValue() == 0) {
                niet.add((String) entry.getKey());
            }
        }
        for (String res : niet) {
            if (res.equals("LUMBER")) {

                lowerLumber.setVisible(false);
                lumberOkay.setVisible(false);

            }
            if (res.equals("ORE")) {

                lowerOre.setVisible(false);
                lowerOre.setVisible(false);

            }
            if (res.equals("WOOL")) {

                lowerWool.setVisible(false);
                lowerWool.setVisible(false);

            }
            if (res.equals("GRAIN")) {

                lowerGrain.setVisible(false);
                lowerGrain.setVisible(false);

            }
            if (res.equals("BRICK")) {

                lowerBrick.setVisible(false);
                lowerBrick.setVisible(false);

            }
        }
        revalidate();
    }

    private void afterTrade() {
        disableAllLowerTrades();
        enableAllTrades();
        tradeLumber.setText("" + 0);
        tradeOre.setText("" + 0);
        tradeGrain.setText("" + 0);
        tradeWool.setText("" + 0);
        tradeBrick.setText("" + 0);
        revalidate();
    }

    private void disableOtherTrades(String res) {
        if (res.equals("LUMBER")) {
            plusLumber.setVisible(true);
            tradeLumber.setVisible(true);
            minLumber.setVisible(true);
            plusOre.setVisible(false);
            tradeOre.setVisible(false);
            minOre.setVisible(false);
            plusWool.setVisible(false);
            tradeWool.setVisible(false);
            minWool.setVisible(false);
            plusGrain.setVisible(false);
            tradeGrain.setVisible(false);
            minGrain.setVisible(false);
            plusBrick.setVisible(false);
            tradeBrick.setVisible(false);
            minBrick.setVisible(false);
        } else if (res.equals("ORE")) {
            plusLumber.setVisible(false);
            tradeLumber.setVisible(false);
            minLumber.setVisible(false);
            plusOre.setVisible(true);
            tradeOre.setVisible(true);
            minOre.setVisible(true);
            plusWool.setVisible(false);
            tradeWool.setVisible(false);
            minWool.setVisible(false);
            plusGrain.setVisible(false);
            tradeGrain.setVisible(false);
            minGrain.setVisible(false);
            plusBrick.setVisible(false);
            tradeBrick.setVisible(false);
            minBrick.setVisible(false);
        } else if (res.equals("WOOL")) {
            plusLumber.setVisible(false);
            tradeLumber.setVisible(false);
            minLumber.setVisible(false);
            plusOre.setVisible(false);
            tradeOre.setVisible(false);
            minOre.setVisible(false);
            plusWool.setVisible(true);
            tradeWool.setVisible(true);
            minWool.setVisible(true);
            plusGrain.setVisible(false);
            tradeGrain.setVisible(false);
            minGrain.setVisible(false);
            plusBrick.setVisible(false);
            tradeBrick.setVisible(false);
            minBrick.setVisible(false);
        } else if (res.equals("GRAIN")) {
            plusLumber.setVisible(false);
            tradeLumber.setVisible(false);
            minLumber.setVisible(false);
            plusOre.setVisible(false);
            tradeOre.setVisible(false);
            minOre.setVisible(false);
            plusWool.setVisible(false);
            tradeWool.setVisible(false);
            minWool.setVisible(false);
            plusGrain.setVisible(true);
            tradeGrain.setVisible(true);
            minGrain.setVisible(true);
            plusBrick.setVisible(false);
            tradeBrick.setVisible(false);
            minBrick.setVisible(false);
        } else if (res.equals("BRICK")) {
            plusLumber.setVisible(false);
            tradeLumber.setVisible(false);
            minLumber.setVisible(false);
            plusOre.setVisible(false);
            tradeOre.setVisible(false);
            minOre.setVisible(false);
            plusWool.setVisible(false);
            tradeWool.setVisible(false);
            minWool.setVisible(false);
            plusGrain.setVisible(false);
            tradeGrain.setVisible(false);
            minGrain.setVisible(false);
            plusBrick.setVisible(true);
            tradeBrick.setVisible(true);
            minBrick.setVisible(true);
        }

        revalidate();
    }

    private void enableAllTrades() {
        plusLumber.setVisible(true);
        tradeLumber.setVisible(true);
        minLumber.setVisible(true);
        plusOre.setVisible(true);
        tradeOre.setVisible(true);
        minOre.setVisible(true);
        plusWool.setVisible(true);
        tradeWool.setVisible(true);
        minWool.setVisible(true);
        plusGrain.setVisible(true);
        tradeGrain.setVisible(true);
        minGrain.setVisible(true);
        plusBrick.setVisible(true);
        tradeBrick.setVisible(true);
        minBrick.setVisible(true);

        revalidate();
    }

    private void disableAllLowerTrades() {

        lowerLumber.setVisible(false);
        lumberOkay.setVisible(false);

        lowerOre.setVisible(false);
        oreOkay.setVisible(false);

        lowerGrain.setVisible(false);
        grainOkay.setVisible(false);

        lowerBrick.setVisible(false);
        brickOkay.setVisible(false);

        lowerWool.setVisible(false);
        woolOkay.setVisible(false);
    }
    //mouselisteners

    private class PlusHandler implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            if (source == plusLumber) {
                receive = "LUMBER";
                int get = Integer.parseInt(tradeLumber.getText());
                get++;
                tradeLumber.setText("" + get);
                quantity = get;
                enableTrades("LUMBER", get);
                disableOtherTrades("LUMBER");
            } else if (source == plusOre) {
                receive = "ORE";
                int get = Integer.parseInt(tradeOre.getText());
                get++;
                tradeOre.setText("" + get);
                quantity = get;
                enableTrades("ORE", get);
                disableOtherTrades("ORE");
            } else if (source == plusBrick) {
                receive = "BRICK";
                int get = Integer.parseInt(tradeBrick.getText());
                get++;
                tradeBrick.setText("" + get);
                quantity = get;
                enableTrades("BRICK", get);
                disableOtherTrades("BRICK");
            } else if (source == plusWool) {
                receive = "WOOL";
                int get = Integer.parseInt(tradeWool.getText());
                get++;
                tradeWool.setText("" + get);
                quantity = get;
                enableTrades("WOOL", get);
                disableOtherTrades("WOOL");
            } else if (source == plusGrain) {
                receive = "GRAIN";
                int get = Integer.parseInt(tradeGrain.getText());
                get++;
                tradeGrain.setText("" + get);
                quantity = get;
                enableTrades("GRAIN", get);
                disableOtherTrades("GRAIN");
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

    private class MinHandler implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            if (source == minLumber) {
                int get = Integer.parseInt(tradeLumber.getText());
                if (get > 0) {
                    get--;
                    tradeLumber.setText("" + get);
                    quantity = get;
                    if (get == 0) {
                        enableAllTrades();
                        disableAllLowerTrades();
                    } else {
                        enableTrades("LUMBER", get);
                        disableOtherTrades("LUMBER");
                    }
                }
            } else if (source == minOre) {
                int get = Integer.parseInt(tradeOre.getText());
                if (get > 0) {
                    get--;
                    tradeOre.setText("" + get);
                    quantity = get;
                    if (get == 0) {
                        enableAllTrades();
                        disableAllLowerTrades();
                    } else {
                        enableTrades("ORE", get);
                        disableOtherTrades("ORE");
                    }
                }
            } else if (source == minBrick) {

                int get = Integer.parseInt(tradeBrick.getText());
                if (get > 0) {
                    get--;
                    tradeBrick.setText("" + get);
                    quantity = get;
                    if (get == 0) {
                        enableAllTrades();
                        disableAllLowerTrades();
                    } else {
                        enableTrades("BRICK", get);
                        disableOtherTrades("BRICK");
                    }
                }
            } else if (source == minWool) {
                int get = Integer.parseInt(tradeWool.getText());
                if (get > 0) {
                    get--;
                    tradeWool.setText("" + get);
                    quantity = get;
                    if (get == 0) {
                        enableAllTrades();
                        disableAllLowerTrades();
                    } else {
                        enableTrades("WOOL", get);
                        disableOtherTrades("WOOL");
                    }
                }
            } else if (source == minGrain) {
                int get = Integer.parseInt(tradeGrain.getText());
                if (get > 0) {
                    get--;
                    tradeGrain.setText("" + get);
                    quantity = get;
                    if (get == 0) {
                        enableAllTrades();
                        disableAllLowerTrades();
                    } else {
                        enableTrades("GRAIN", get);
                        disableOtherTrades("GRAIN");
                    }
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

    private class OkayHandler implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            if (source == lumberOkay) {
                guiC.tradeBank("LUMBER", receive, quantity);

            } else if (source == oreOkay) {
                guiC.tradeBank("ORE", receive, quantity);
            } else if (source == brickOkay) {
                guiC.tradeBank("BRICK", receive, quantity);

            } else if (source == woolOkay) {
                guiC.tradeBank("WOOL", receive, quantity);
            } else if (source == grainOkay) {
                guiC.tradeBank("GRAIN", receive, quantity);
            }
            afterTrade();
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
