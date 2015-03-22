/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Player;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Joachim
 */
public class PlayerRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Player player = new Player();
        for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
            String colname = rs.getMetaData().getColumnName(col);
            if (colname.equalsIgnoreCase("PLAYER_ID")) {
                player.setId(rs.getInt("PLAYER_ID"));
            } else if (colname.equalsIgnoreCase("PLAYER_NAME")) {
                player.setName(rs.getString("PLAYER_NAME"));
            } else if (colname.equalsIgnoreCase("PLAYER_EMAIL")) {
                player.setEmail(rs.getString("PLAYER_EMAIL"));
            } else if (colname.equalsIgnoreCase("PLAYER_PASSWORD")) {
                player.setPassword(rs.getString("PLAYER_PASSWORD"));
            } else if (colname.equalsIgnoreCase("PLAYER_AVATAR")) {
                player.setAvatar(rs.getString("PLAYER_AVATAR"));
            } else if (colname.equalsIgnoreCase("PLAYER_ENABLED")) {
                player.setStatus(rs.getString("PLAYER_ENABLED").charAt(0));
            } else if (colname.equalsIgnoreCase("PLAYER_FACEBOOK_NUMBER")){
              player.setFacebook(Integer.parseInt(rs.getString("PLAYER_FACEBOOK_NUMBER")));
            } else if (colname.equalsIgnoreCase("PLAYER_ADMIN")) {
                player.setPlayerAdmin(rs.getString("PLAYER_ADMIN").charAt(0));
            }
        }
        return player;
    }
}