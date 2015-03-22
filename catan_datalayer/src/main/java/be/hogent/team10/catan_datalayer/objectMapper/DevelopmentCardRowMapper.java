package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.DevelopmentCard;
import be.hogent.team10.catan_businesslogic.model.DevelopmentCardType;
import be.hogent.team10.catan_businesslogic.model.Player;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class DevelopmentCardRowMapper  implements RowMapper {
    
   @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        String type = rs.getString("CARD_TYPE");
        int id = rs.getInt("CARD_ID");
        Player owner = new Player(rs.getInt("PLAYER_ID"));
        int turn_played = rs.getInt("TURN_PLAYED");
        DevelopmentCard d = new DevelopmentCard();
        if(turn_played != 0)
            d.setPlayed(true);
        
        if(owner.getId()!=0)
            d.setOwner(owner);
        d.setId(id);
        if(type.equalsIgnoreCase("KNIGHT")){
            d.setDevelopmentCardType(DevelopmentCardType.KNIGHT);
        }else{
            d.setDevelopmentCardType(DevelopmentCardType.UNIVERSITY);
        }
        
        return d;
    }
    
}
