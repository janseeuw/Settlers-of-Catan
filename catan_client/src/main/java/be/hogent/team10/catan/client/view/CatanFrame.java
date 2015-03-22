package be.hogent.team10.catan.client.view;

import be.hogent.team10.catan.client.view.viewComponents.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Joachim en Brenden Dit is het grote frame dat alle andere panels zal
 * toegewezen krijgen.
 */
public class CatanFrame extends JFrame {

    private JPanel all;
    private CenterPanel centerPanel;
    private BoardPanel boardPanel;
    private SidePanel infoPanel;
    private PlayerResourcePanel resourcePanel;
    private JWindow optionsMenu;
    private boolean isOptionsMenu = false;

    public CatanFrame(GuiController c) {
        try {
            this.setIconImage(ImageIO.read(getClass().getResource("/startIcon.png")));
        } catch (IOException ex) {
            Logger.getLogger(CatanFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setTitle("Kolonisten van Catan");
        setResizable(false);
        infoPanel = c.getSidePanel();
        boardPanel = c.getBoard();
        resourcePanel = c.getResourcePanel();

        this.setPreferredSize(new Dimension(GuiController.getInstance().getWIDTH(), GuiController.getInstance().getHEIGHT()));
        all = new JPanel(new BorderLayout(-getWidth() / 10, getHeight() / 3));

        optionsMenu = new JWindow();
        optionsMenu.setLayout(new GridLayout(5, 1));
        optionsMenu.setSize(125, 300);
        optionsMenu.setLocationRelativeTo(null);

        optionsMenu.addKeyListener(new KeyHandler());
        JButton newG = new JButton("Nieuw Spel");
        newG.addKeyListener(new KeyHandler());
        JButton save = new JButton("Spel Opslaan");
        save.addKeyListener(new KeyHandler());
        JButton load = new JButton("Spel laden");
        load.addKeyListener(new KeyHandler());
        JButton op = new JButton("Opties");
        op.addKeyListener(new KeyHandler());
        JButton closeW = new JButton("Sluiten");
        closeW.addKeyListener(new KeyHandler());
        optionsMenu.add(newG);
        optionsMenu.add(save);
        optionsMenu.add(load);
        optionsMenu.add(op);
        optionsMenu.add(closeW);
        optionsMenu.setVisible(false);
        init();

        c.setFrame(this);
    }

    public CenterPanel getCenterPanel() {
        return centerPanel;
    }

    private void init() {

        setLocationRelativeTo(null);
        boardPanel.initializeBoard(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addKeyListener(new KeyHandler());

        infoPanel.setMaximumSize(new Dimension(getWidth() / 4, getHeight()));

        Dimension minimumSize = new Dimension(this.getWidth() / 4, this.getHeight());
        infoPanel.setMaximumSize(minimumSize);
    }

    @Override
    public void setSize(int w, int h) {

        centerPanel = new CenterPanel(boardPanel);
        all.add(centerPanel, BorderLayout.CENTER);
        if (GuiController.getInstance().getClientNumber() != 0) {
            all.add(infoPanel, BorderLayout.EAST);
            all.add(resourcePanel, BorderLayout.NORTH);
        }

        add(all);

        pack();
        boardPanel.setSize(new Dimension(w, h));

    }

    void prepareInitializationState() {
        if (centerPanel != null) {
            centerPanel.prepareInitializationState();
        }
    }

    private class KeyHandler implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

                if (isOptionsMenu) {
                    isOptionsMenu = false;
                    optionsMenu.setVisible(false);

                } else {
                    isOptionsMenu = true;
                    optionsMenu.setVisible(true);

                }
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }
}
