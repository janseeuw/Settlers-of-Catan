/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Border;
import be.hogent.team10.catan_businesslogic.model.BuildingType;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.Node;
import be.hogent.team10.catan_businesslogic.model.Player;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class NodeInGameRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        //return new Border(new Coordinate(5,5));
          
        String type = rs.getString("COMPONENT_NAME");
        int x = rs.getInt("COORDINATE_X");
        int y = rs.getInt("COORDINATE_Y");
        Coordinate co = new Coordinate(x, y);
        Player owner = new Player(rs.getInt("PLAYER_ID"));
        if(!type.trim().equalsIgnoreCase("STREET")){
        
            Node n = new Node(co);
            n.setBuildingtype(type.equalsIgnoreCase("SETTLEMENT")?BuildingType.SETTLEMENT:BuildingType.CITY);
            n.setOwner(owner);
            return n;
        }
        return null;
    
    }
    
}
