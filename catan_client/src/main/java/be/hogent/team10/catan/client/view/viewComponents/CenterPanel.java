package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.gameState.InitializationState;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;

/**
 *
 * @author Brenden Dit paneel omhult het centrum van het frame en is opgebouwd
 * uit 3 panelen: noord, zuid en boardpanel. In het noord en zuidpaneel zitten
 * telkens mogelijk 2 playerinfopanelen.
 */
public class CenterPanel extends JLayeredPane implements Observer, MouseListener {

    private GuiController guiC;
    private BoardPanel boardPanel;
    private JPanel north;
    private JPanel south;
    private Map<Integer, PlayerInfoPanel> playerInfoPanels;
    private ErrorPanel errorPanel;
    private JPanel start;
    private int panelSize;

    public CenterPanel(BoardPanel board) {
        guiC = GuiController.getInstance();
        setPreferredSize(new Dimension(guiC.getSize("CPSizew"), guiC.getSize("CPSizeh")));
        //Toegevoegd voor kleur background
        this.setOpaque(true);
        setBackground(Color.decode("#778899"));
        boardPanel = board;
        playerInfoPanels = new HashMap<Integer, PlayerInfoPanel>(); //aantal spelers
        north = new JPanel(new BorderLayout());
        south = new JPanel(new BorderLayout());
        PlayerInfoPanel panel = new PlayerInfoPanel();
        north.setBounds(guiC.getSize("CPNorth1"), guiC.getSize("CPNorth2"), guiC.getSize("CPNorth3"), guiC.getSize("CPNorth4"));
        north.setOpaque(false);
        boardPanel.setBounds(guiC.getSize("CPBoard1"), guiC.getSize("CPBoard2"), guiC.getSize("CPBoard3"), guiC.getSize("CPBoard4"));
        south.setBounds(guiC.getSize("CPSouth1"), guiC.getSize("CPSouth2") - panel.getHeight(), guiC.getSize("CPSouth3"), guiC.getSize("CPSouth4"));
        south.setOpaque(false);
        if (guiC.getClientNumber() != 0) {

            Player client = guiC.getClient();
            playerInfoPanels.put(client.getId(), panel);
            // you
            guiC.getClient().addObserver(panel);
            south.add(panel, BorderLayout.WEST);
            errorPanel = ErrorPanel.getInstance();
            north.add(errorPanel, BorderLayout.CENTER);
            guiC.addObserver(panel);
            update(null, "ADD_PLAYER");
        }
        panelSize = panel.getHeight();
        add(north, 5);
        add(boardPanel, 0);
        add(south, 5);
        //host client gets a start button


        guiC.addObserver(this);
        guiC.getGameState().addObserver(this);
    }

    //observer zal nieuwe spelers tekenen wanneer ze het spel joinen.
    public void update(Observable o, Object arg) {
        System.out.println("arrived)");
        System.out.println(o);
        System.out.println(arg);
        System.out.println(o == null);
        System.out.println(o instanceof Game);
        System.out.println(arg == null);

        if ((!(o instanceof InitializationState) && o instanceof GameState) && start != null) {
            System.out.println("busted");
            remove(start);
            revalidate();
        } else if ((o == null || o instanceof Game) && arg != null) {
            System.out.println("adding player");
            String a = (String) arg;
            if (a.equals("ADD_PLAYER")) {
                System.out.println("adding player2");
                for (Player p : guiC.getPlayers()) {
                    if (!(playerInfoPanels.containsKey(p.getId()))) {
                        System.out.println(playerInfoPanels.size() + "already here");
                        switch (playerInfoPanels.size()) {
                            case 0:
                                if (guiC.getClientNumber() == 0) {
                                    PlayerInfoPanel panel0 = new PlayerInfoPanel();
                                    playerInfoPanels.put(p.getId(), panel0);
                                    p.addObserver(panel0);
                                    south.add(panel0, BorderLayout.WEST);
                                    guiC.addObserver(panel0);
                                    break;
                                }
                            case 1:
                                PlayerInfoPanel panel = new PlayerInfoPanel();
                                playerInfoPanels.put(p.getId(), panel);
                                p.addObserver(panel);
                                north.add(panel, BorderLayout.WEST);
                                guiC.addObserver(panel);
                                break;
                            case 2:
                                PlayerInfoPanel panel2 = new PlayerInfoPanel();
                                p.addObserver(panel2);
                                playerInfoPanels.put(p.getId(), panel2);
                                north.add(panel2, BorderLayout.EAST);
                                guiC.addObserver(panel2);
                                break;
                            case 3:
                                PlayerInfoPanel panel3 = new PlayerInfoPanel();
                                p.addObserver(panel3);
                                playerInfoPanels.put(p.getId(), panel3);
                                south.add(panel3, BorderLayout.EAST);
                                guiC.addObserver(panel3);
                                break;
                        }
                    }

                }
            }
        }

        revalidate();
    }
    //mouselistener voor startknop

    public void mouseClicked(MouseEvent e) {
        remove(start);
        start.setVisible(false);
        revalidate();
        guiC.startGame();
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void prepareInitializationState() {
        if (start == null) {
            start = new JPanel();
            start.setBounds(350, 300, 200, 100);
            start.setLayout(new BorderLayout());
            start.setBackground(new Color(92, 51, 23));

            if (guiC.getCreator() == guiC.getClientNumber()) {
                JButton startButton = new JButton("Start het spel" + guiC.getCreator() + " " + guiC.getClient().getId());
                startButton.addMouseListener(this);
                start.add(startButton);
            } else {
                JLabel label = new JLabel("  Wachten op andere spelers...");
                label.setForeground(Color.white);
                start.add(label);
            }
            add(start, 0);
            revalidate();
        }
    }
}