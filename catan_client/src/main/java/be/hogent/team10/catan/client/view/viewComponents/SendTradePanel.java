/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.view.GuiController;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Joachim
 */
public class SendTradePanel extends JPanel {

    private GuiController guiC;
    private TradeFrame frame;

    public SendTradePanel(TradeFrame frame) {
        this.frame = frame;
        guiC = GuiController.getInstance();
        GridLayout grid = new GridLayout(1, 2);
        grid.setHgap(-10);
        setLayout(grid);
        
        JButton cancel = new JButton("Annuleer");
        cancel.addMouseListener(new CancelListener());
        add(cancel);
        JButton trade = new JButton("Ruilen");
        trade.addMouseListener(new TradeListener());
        add(trade);
    }

    private class CancelListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            frame.cancelTrade();
        }
    }

    private class TradeListener extends MouseAdapter {
        
        @Override
        public void mouseClicked(MouseEvent e) {
            frame.trade();
        }
    }
}
