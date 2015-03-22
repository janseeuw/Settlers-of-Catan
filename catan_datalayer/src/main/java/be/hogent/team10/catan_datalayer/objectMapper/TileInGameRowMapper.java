package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.DesertTileBehaviour;
import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.ResourceTileBehaviour;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.SeaTileBehaviour;
import be.hogent.team10.catan_businesslogic.model.Tile;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class TileInGameRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Tile t = new Tile();
        try {
            String tileBehaviour = rs.getString("TILE_NAME");
            if (tileBehaviour.equalsIgnoreCase("DESERT")) {
                t.setBehaviour(new DesertTileBehaviour());
            } else if (tileBehaviour.equalsIgnoreCase("SEA")) {
                t.setBehaviour(new SeaTileBehaviour());
            } else {
                t.setBehaviour(new ResourceTileBehaviour());
                t.setResource(Resource.valueOf(tileBehaviour));
            }

            t.setNumber(rs.getInt("TILE_NUMBER"));
        } catch (Exception x) {
            throw new SQLException("Could not construct object at tileMapper");
        }
        Coordinate c = (Coordinate) new CoordinateRowMapper().mapRow(rs, i);
        t.setCoordinate(c);
        return t;
    }
}
