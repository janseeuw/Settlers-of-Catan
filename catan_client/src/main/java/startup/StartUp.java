package startup;

import be.hogent.team10.catan.client.controller.GameController;
import be.hogent.team10.catan.client.view.CatanFrame;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Player;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class StartUp {

    StartUp() { 
        
    }

    public static void main(String[] args) {
        System.setSecurityManager(null);
        try {
            //GameController gc = new GameController(/*Integer.parseInt(args[0]), Integer.parseInt(args[2])*/690 , 3123);
            GameController gc = new GameController(Integer.parseInt(args[0]), Integer.parseInt(args[2]));

            Player player = null;
            while (player == null) {
                try {
                    player = gc.getClient();
                } catch (Exception e) {
                   e.printStackTrace();
                   System.out.println("Waiting for server response....");
                    try {
                        if(player == null){
                            if(gc.getPlayers() !=null&&!gc.getPlayers().isEmpty())
                            {
                                gc.getGame().setMy_id(0);
                                player = new Player(0);
                            }
                        }
                        if (player== null)
                            Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            System.out.println("Player loaded....");
            GuiController guiC = GuiController.getInstance();
            guiC.setGameController(gc);
            //--ok--//
            
            CatanFrame catan = new CatanFrame(guiC);
            catan.setSize(GuiController.getInstance().getWIDTH(), GuiController.getInstance().getHEIGHT());
            catan.setLocationRelativeTo(null);
            catan.pack();
            gc.ready();
            guiC.setNodesOnTop();
        } catch (Exception ex) {
            Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
