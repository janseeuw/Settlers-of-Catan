/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.gameState.InitializationState;
import be.hogent.team10.catan_businesslogic.model.gameState.PlayGameState;
import be.hogent.team10.catan_businesslogic.model.gameState.RobberState;
import be.hogent.team10.catan_businesslogic.model.gameState.SetupState;
import be.hogent.team10.catan_businesslogic.util.Dice;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class GameInGameRowMapper implements RowMapper<Game> {

    @Override
    public Game mapRow(ResultSet rs, int i) throws SQLException {
        Game g = new GameRowMapper().mapRow(rs, i);
        g.setResources((ResourceSet) (new ResourceSetInGameRowMapper().mapRow(rs, i)));
        for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
            String colname = rs.getMetaData().getColumnName(col);
            if (colname.equalsIgnoreCase("GAME_STAMP")) {
                g.setGameStamp(rs.getInt("GAME_STAMP"));
            } else if (colname.equalsIgnoreCase("CURRENT_PLAYER")) {
                g.setCurrentPlayer(rs.getInt("CURRENT_PLAYER"));
            } else if (colname.equalsIgnoreCase("CREATOR")) {
                g.setCreator(rs.getInt("CREATOR"));
            } else if (colname.equalsIgnoreCase("DICE_RESULT")) {
                Dice dice = new Dice();
                dice.setValue(rs.getInt("DICE_RESULT"));
                g.setDice(dice);
            } else if (colname.equalsIgnoreCase("ROBBER_X")) {
                Coordinate coordinate = new Coordinate(0, 0);
                coordinate.setX(rs.getInt("ROBBER_X"));
                coordinate.setY(rs.getInt("ROBBER_Y"));
                if (coordinate.getX() != 0) {
                    g.getBoard().setRobber(coordinate);
                }
            } else if (colname.equalsIgnoreCase("GAME_STATE")) {
                String s = rs.getString("GAME_STATE");
                if (s.equalsIgnoreCase("PLAY_GAMESTATE")) {
                    g.setGameState(new PlayGameState(null));
                } else if (s.equalsIgnoreCase("SETUP_STATE")) {
                    g.setGameState(new SetupState(null));
                } else if (s.equalsIgnoreCase("ROBBER_STATE")) {
                    g.setGameState(new RobberState(null));
                } else if (s.equalsIgnoreCase("INITIALIZATION_STATE")) {
                    g.setGameState(new InitializationState(null));
                }
            }
        }
        return g;
    }
}
