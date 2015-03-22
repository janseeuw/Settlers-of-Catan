/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class CoordinateRowMapper  implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        int x = rs.getInt("COORDINATE_X");
        int y = rs.getInt("COORDINATE_Y");
        Coordinate co = new Coordinate(x, y);
        return co;
    }
    
}
