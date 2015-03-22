/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.util;

import be.hogent.team10.catan.client.facade.ServiceFacade;
import be.hogent.team10.catan.client.facade.ServiceFacadeInterface;
import be.hogent.team10.catan_businesslogic.model.Game;

/**
 *
 * @author Joachim
 */
public class PollDaemonThread extends Thread {
    public Game game;
    public ServiceFacadeInterface serviceFacade;
   
    public PollDaemonThread(Game game, ServiceFacadeInterface serviceFacade){
        this.game = game;
        this.serviceFacade = serviceFacade;
    }
    
    @Override
    public void run()
    {
        try
        {
            Game g = serviceFacade.poll(game.getGameId(), game.getGameStamp(), game.getMy_id());
            game.synchronize(g);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}