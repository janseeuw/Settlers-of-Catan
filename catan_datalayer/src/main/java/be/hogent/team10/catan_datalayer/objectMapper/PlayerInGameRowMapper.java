package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.util.PlayerColor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class PlayerInGameRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Player p = (Player) new PlayerRowMapper().mapRow(rs, i);
        p.setResources((ResourceSet) (new ResourceSetInGameRowMapper().mapRow(rs, i)));

        for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
            String colname = rs.getMetaData().getColumnName(col);
            String playerColor = colname.trim().toUpperCase();
            if (playerColor.equalsIgnoreCase("BLUE")) {
                p.setPlayerColor(PlayerColor.BLUE);
            } else if (playerColor.equalsIgnoreCase("GREEN")) {
                p.setPlayerColor(PlayerColor.GREEN);
            } else if (playerColor.equalsIgnoreCase("RED")) {
                p.setPlayerColor(PlayerColor.RED);
            } else if (playerColor.equalsIgnoreCase("YELLOW")) {
                p.setPlayerColor(PlayerColor.YELLOW);
            }
            if (colname.equalsIgnoreCase("STREETS")) {
                p.setAvailableStreets(p.getAvailableStreets() - rs.getInt("STREETS"));
            } else if (colname.equalsIgnoreCase("SETTLEMENTS")) {
                p.setAvailableSettlements(p.getAvailableSettlements() - rs.getInt("SETTLEMENTS"));
            } else if (colname.equalsIgnoreCase("CITIES")) {
                p.setAvailableCities(p.getAvailableCities() - rs.getInt("CITIES"));
            }
            else if (colname.equalsIgnoreCase("TIME_JOINED")) {
                Calendar c = new GregorianCalendar();
                c.setTime(rs.getDate("TIME_JOINED"));
                p.setTimeJoined(c);
            }
            p.setPlayerColor(PlayerColor.valueOf(rs.getString("PLAYER_COLOR").trim().toUpperCase()));
        }
        return p;
    }
}
