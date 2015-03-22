/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.util;

import be.hogent.team10.catan.client.facade.ServiceFacade;
import be.hogent.team10.catan.client.facade.ServiceFacadeInterface;
import be.hogent.team10.catan_businesslogic.model.Game;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class PollDaemon extends Thread {

    protected Game game = null;
    //Time out van 2 seconden
    protected int timeOut = 2000;
    protected boolean running = true;
    protected ServiceFacadeInterface serviceFacade;

    public PollDaemon(Game g) {
        this.game = g;
        serviceFacade = new ServiceFacade();
    }

    public PollDaemon(Game g, ServiceFacadeInterface f) {
        this.game = g;
        serviceFacade = f;
    }

    @Override
    public void run() {
        try {
            while (running) {
                PollDaemonThread d = new PollDaemonThread(game, serviceFacade);
                d.start();
                sleep(timeOut);
            }
        } catch (Exception e) {
        }
    }

    public void poll() {
            PollDaemonThread d = new PollDaemonThread(game, serviceFacade);
            d.start();
    }

    public void restartPoll() {
        running = true;
        start();
    }

    public void stopPoll() {
        running = false;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
