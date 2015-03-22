/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 *
 * @author Joachim
 */
public class TradeFrame extends JFrame {

    private GuiController guiC;
    private JPanel table=new JPanel();
    private Map<String,Integer> bank;
    private ResourceTradePanel resourceTradePanel;
    private SelectPlayerPanel selectPlayerPanel;
    private SendTradePanel sendTradePanel;
    private Player tradeWithPlayer;
    private Player tradeFromPlayer;
    private int woolQuantity, grainQuantity, brickQuantity, oreQuantity, lumberQuantity;
    private JTextField tradeLumber,tradeOre,tradeWool,tradeBrick,tradeGrain;
    private static TradeFrame instance;
    
    public static TradeFrame getInstance(){
        if(instance == null){
            instance = new TradeFrame();
        }
        return instance;
    }
    public TradeFrame() {
        this.guiC = GuiController.getInstance();
        //setPreferredSize(new Dimension(guiC.getSize("BaPSizew"),guiC.getSize("BaPSizeh")));
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        //PlayerSelect
        //Resources
        //TradeOk
        GridLayout grid=new GridLayout(3, 1);
        grid.setHgap(-10);
        setLayout(new BorderLayout());
        selectPlayerPanel = new SelectPlayerPanel();
        resourceTradePanel = new ResourceTradePanel();
        sendTradePanel = new SendTradePanel(this);
        add(selectPlayerPanel,BorderLayout.NORTH);
        add(resourceTradePanel,BorderLayout.CENTER);
        add(sendTradePanel,BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
                instance = null;
	    }
	});
    }
    
    public void cancelTrade(){
        this.dispose();
        instance = null;
    }

    public void trade() {
        lumberQuantity = resourceTradePanel.getLumberQuantity();
        grainQuantity = resourceTradePanel.getGrainQuantity();
        woolQuantity = resourceTradePanel.getWoolQuantity();
        brickQuantity = resourceTradePanel.getBrickQuantity();
        oreQuantity = resourceTradePanel.getOreQuantity();
        tradeWithPlayer = selectPlayerPanel.getSelectedPlayer();
        tradeFromPlayer = guiC.getClient();
        ResourceSet rs = new ResourceSet(); 
        rs.add(Resource.BRICK, brickQuantity);
        rs.add(Resource.LUMBER, lumberQuantity);
        rs.add(Resource.GRAIN, grainQuantity);
        rs.add(Resource.WOOL, woolQuantity);
        rs.add(Resource.ORE, oreQuantity);
        guiC.trade(tradeFromPlayer, tradeWithPlayer, rs);
        this.dispose();       
        instance = null;
    }
        
}
