package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Joachim
 */
public class GameRowMapper implements RowMapper<Game> {
    //TODO afwerken en vragen aan Emmanuel wat de get van game juist doet in de reposiroty.

    public Game mapRow(ResultSet rs, int i) throws SQLException {
        Game game = new Game();
        for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
            String colname = rs.getMetaData().getColumnName(col);
            if (colname.equalsIgnoreCase("GAME_ID")) {
                game.setGameId(rs.getInt(colname));
                
            } else if (colname.equalsIgnoreCase("TIME_CREATED") && rs.getTimestamp(colname) != null) {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(rs.getTimestamp(colname).getTime());
                game.setTimeCreated(c);

            } else if (colname.equalsIgnoreCase("TIME_STARTED") && rs.getTimestamp(colname) != null) {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(rs.getTimestamp(colname).getTime());
                game.setTimeCreated(c);
            } else if (colname.equalsIgnoreCase("PLAYER_ID")) {
                game.setCreator(rs.getInt("PLAYER_ID"));              
            } else if (colname.equalsIgnoreCase("TURN_ID")){
                game.setTurnId(rs.getInt("TURN_ID"));
            }
        }
        return game;
    }
}
