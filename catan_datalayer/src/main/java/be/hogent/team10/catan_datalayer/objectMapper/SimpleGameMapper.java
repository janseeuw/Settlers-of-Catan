/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.simple.SimpleGame;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class SimpleGameMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        SimpleGame game = new SimpleGame();
        for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
            String colname = rs.getMetaData().getColumnName(col);
            if (colname.equalsIgnoreCase("GAME_ID")) {
                game.setGameId(rs.getInt(colname));

            } else if (colname.equalsIgnoreCase("TIME_CREATED") && rs.getTimestamp(colname) != null) {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(rs.getTimestamp(colname).getTime());
                game.setTimeCreated(c);

            } else if (colname.equalsIgnoreCase("AANTAL")) {
                game.setMax(rs.getInt(colname));
            } else if (colname.equalsIgnoreCase("STATUS")) {
                game.setStatus(rs.getString(colname));
            } else if (colname.equalsIgnoreCase("PLAYERS")) {
                game.setPlayers(rs.getString("PLAYERS"));
            }
        }
        return game;

    }
}
