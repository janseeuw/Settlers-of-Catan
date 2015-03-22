package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import be.hogent.team10.catan.client.view.GuiController;

import javax.swing.*;

/**
 *
 * @author Brenden Dit paneel is in feite een verzameling van panelen die aan de
 * zijkant naast het centerpanel staan. Het omvat ongeveer alle acties en
 * besturingsmogelijkheden die een speler kan hebben.
 */
public class SidePanel extends JPanel {

    private GuiController guiC;
    private BankPanel bank;
    private JPanel upper;
    private DicePanel dicePanel;
    private PlaceComponentsPanel placeComponentsPanel;
    private BuildPanel playerInfoPanel;
    private JTabbedPane split, split2;

    //deze methode was voor sprint 1, je eigen spelbord maken en is nu deprecated
    @Deprecated
    public void prepareInitializeState() {
        this.removeAll();
        setLayout(new GridLayout(1, 1));


        upper = new JPanel(new GridLayout(7, 3));
        add(upper);

        revalidate();
    }
    //deze methode initialiseert het paneel nodig voor de setupfase

    public void prepareSetupState(PlaceComponentsPanel panel) {
        this.removeAll();
        this.placeComponentsPanel = panel;
        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);

        revalidate();
    }
    //deze methode zal alle nodige panelen voor te spelen initialiseren.

    public void preparePlayState(BuildPanel buildpanel) {
        this.removeAll();
        playerInfoPanel = buildpanel;
        setPreferredSize(new Dimension(guiC.getSize("SPSizew"), guiC.getSize("SPSizeh")));

        setLayout(new BorderLayout());
        bank = new BankPanel();

        CardPanel cardPanel = new CardPanel();
        split2 = new JTabbedPane();
        split2.addTab("Kaarten", cardPanel);

        split2.addTab("Bank", bank);
        add(split2, BorderLayout.NORTH);
        dicePanel = new DicePanel();
        guiC.getClient().addObserver(dicePanel);
        guiC.addObserver(dicePanel);
        split = new JTabbedPane();
        split.addTab("Acties", buildpanel);
        split.addTab("Dobbelstenen", dicePanel);

        add(split, BorderLayout.SOUTH);



        revalidate();

    }

    public DicePanel getDicePanel() {
        return dicePanel;
    }

    public SidePanel(GuiController guic) {
        this.guiC = guic;
        setLayout(new BorderLayout());

        setVisible(true);


    }

    public BuildPanel getBuildPanel() {
        return playerInfoPanel;
    }

    public PlaceComponentsPanel getPlayerCompPanel() {
        return this.placeComponentsPanel;
    }

    public void setPlayerInfoPanel(BuildPanel playerInfoPanel) {
        this.playerInfoPanel = playerInfoPanel;
    }
}
