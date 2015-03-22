/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.gameState.SetupState;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import javax.swing.JPanel;

/**
 *
 * @author HP
 */
public class PricePanel extends JPanel implements Observer {

    public void setPrices(ResourceSet price){
        
    }
    
    public void update(Observable o, Object o1) {
        if (o instanceof SetupState) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
        revalidate();
    }
}
