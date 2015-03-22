package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.util.AudioStream;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Joachim, Brenden
 */
public class PlayerResourcePanel extends JPanel implements Observer {

    private final GuiController guiC;
    private List<JLabel> textfields;
    private JPanel resource;
    private JPanel menu;
    private boolean sound = true;
    private JButton options, trade;
    private JLabel phase;
    private Thread t;
    private TradeListener tradeListener;
    

    public PlayerResourcePanel(GuiController guiC) {
        // this.setLayout();
        this.guiC = guiC;
        this.setPreferredSize(new Dimension(guiC.getSize("PRPSizew"), guiC.getSize("PRPSizeh")));

        setLayout(new BorderLayout());
        setBackground(Color.decode("#2F4F4F"));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        resource = new JPanel(new GridLayout(1, 10));
        resource.setBackground(Color.decode("#2F4F4F"));
        resource.setPreferredSize(new Dimension(guiC.getSize("PRPResourcew"), guiC.getSize("PRPResourceh")));
        menu = new JPanel();
        menu.setBackground(Color.decode("#2F4F4F"));
        //menu.setMinimumSize(new Dimension(50,50));

        init();
    }

    public void init() {
        textfields = new ArrayList<JLabel>();
        String[] resourcenames = {"BRICK", "LUMBER", "WOOL", "GRAIN", "ORE"};
        for (String s : resourcenames) {
            JLabel label = new JLabel();
            try {
                Image img = ImageIO.read(getClass().getResource("/" + s + "icon.png"));
                img = img.getScaledInstance(guiC.getSize("PRPScaled"), guiC.getSize("PRPScaled"), 1);
                label.setIcon(new ImageIcon(img));
                label.setHorizontalAlignment(JLabel.CENTER);
            } catch (IOException ex) {
                Logger.getLogger(SidePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            resource.add(label, JPanel.CENTER_ALIGNMENT);
            JLabel veld = new JLabel("0", JLabel.CENTER);
            veld.setHorizontalAlignment(JLabel.CENTER);
            veld.setName(s);

            veld.setForeground(Color.WHITE);
            veld.setBackground(Color.BLACK/*
                     * new Color(92,51,23)
                     */);
            // veld.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 2, Color.BLACK));
            resource.add(veld, JLabel.CENTER_ALIGNMENT);
            textfields.add(veld);
            this.add(resource, BorderLayout.WEST);
        }
        options = new JButton("Mute");
        options.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (sound) {
                    sound = false;
                    options.setText("Unmute");
                } else {
                    sound = true;
                    options.setText("Mute");
                }
                AudioStream.getInstance().setEnabled(sound);
            }
        });

        options.setSize(20, 40);
        options.setHorizontalAlignment(JButton.CENTER);
        trade = new JButton("Trade");
        tradeListener = new TradeListener();
        trade.addMouseListener(tradeListener);
        phase = new JLabel();
        //phase.setText("Fase: "+guiC.getGameState().toString());
        phase.setForeground(Color.GREEN);
        menu.add(phase);
        menu.add(trade);


        menu.add(options);

        this.add(menu, BorderLayout.EAST);
    }

    public void update(Observable o, Object o1) {
        if (o instanceof Player) {
            
            ResourceSet set = ((Player) o).getResources();
            
            for (JLabel field : textfields) {
                int old = Integer.parseInt(field.getText());
                field.setText("" + set.getAmount(field.getName()));
                int neW = Integer.parseInt(field.getText());

                if (old < neW) {
                    field.setForeground(Color.YELLOW);
                } else if (old > neW) {
                    field.setForeground(Color.red);
                }
            }
            Timer timer = new Timer(3000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {

                    for (JLabel field : textfields) {
                        field.setForeground(Color.WHITE);
                    }
                }
            });
            timer.setRepeats(false); // Only execute once
            timer.start(); // Go go go!

        } else {
            //phase.setText("Fase: "+guiC.getGameState().toString());
        }
        
        if(!guiC.myTurn()){
            trade.setEnabled(false);
            trade.removeMouseListener(tradeListener);
        } else {
            trade.setEnabled(true);
            trade.addMouseListener(tradeListener);
        }
        revalidate();
    }
    

    private class TradeListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            guiC.addTradeFrame();
        }
    }
}
