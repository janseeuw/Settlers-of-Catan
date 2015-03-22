/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Player;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Joachim
 */
public class SelectPlayerPanel extends JPanel {

  private GuiController guiC;
  private JComboBox tradePlayer;
  private Player selectedPlayer;

  public SelectPlayerPanel() {
    this.guiC = GuiController.getInstance();
    GridLayout grid = new GridLayout(1, 2);
    grid.setHgap(-10);
    setLayout(grid);
    final List<Player> players = guiC.getPlayers();
    tradePlayer = new JComboBox();
    for (Player player : players) {
      if (!player.equals(guiC.getCurrentPlayer())) {
        tradePlayer.addItem(player.getName());
      }
    }
    for (Player player : players) {
      if (player.getName().equalsIgnoreCase(tradePlayer.getItemAt(0).toString())) {
        selectedPlayer = player;
      }
    }
    tradePlayer.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
          Object item = event.getItem();
          for (Player player : players) {
            if (player.getName().equalsIgnoreCase(item + "")) {
              selectedPlayer = player;
            }
          }
        }
      }
    });

    JLabel trader = new JLabel("Kies een speler:");
    this.add(trader);
    this.add(tradePlayer);
  }

  public Player getSelectedPlayer() {
    return selectedPlayer;
  }
}
