package startup;

import be.hogent.team10.catan.client.controller.GameController;
import be.hogent.team10.catan.client.util.PollDaemon;
import be.hogent.team10.catan.client.view.CatanFrame;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author HP
 */
public class StartUpOffline {

    StartUpOffline() {
    }

    public static void main(String[] args) {
        try {
            GameController gc = new GameController(new PollDaemon(null, null), 10000, 1);

            System.out.println("ok");
            Thread.sleep(3000);
            System.out.println("ok");
            GuiController guiC = GuiController.getInstance();
            guiC.setGameController(gc);
            CatanFrame catan = new CatanFrame(GuiController.getInstance());
            catan.setSize(GuiController.getInstance().getWIDTH(), GuiController.getInstance().getHEIGHT());
            catan.setLocationRelativeTo(null);
            catan.pack();
            gc.ready();
            gc.startGame();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(StartUpOffline.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(StartUpOffline.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}