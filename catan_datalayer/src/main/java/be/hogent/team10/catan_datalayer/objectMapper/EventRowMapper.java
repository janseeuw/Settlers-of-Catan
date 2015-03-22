/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.util.Event;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class EventRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Event event = new Event();
        for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
            
            String colname = rs.getMetaData().getColumnName(col);
            //System.out.println(colname);
            if (colname.equalsIgnoreCase("GAME_ID")) {
                event.setGameId(rs.getInt("GAME_ID"));
            } else if (colname.equalsIgnoreCase("EVENT_NUMBER")) {
                event.setEventNumber(rs.getInt("EVENT_NUMBER"));
            } else if (colname.equalsIgnoreCase("EVENT_DESCRIPTION")) {
                event.setEventDescription(rs.getString("EVENT_DESCRIPTION"));
            } else if (colname.equalsIgnoreCase("TURN_ID")) {
                event.setTurn(rs.getInt("TURN_ID"));
            } else if (colname.equalsIgnoreCase("AFFECTED_ID")) {
                List<Integer> ids = new ArrayList<Integer>();
                ids.add(rs.getInt("AFFECTED_ID"));
                event.setAffectedIds(ids);
            }
        }
        return event;
    }
}
