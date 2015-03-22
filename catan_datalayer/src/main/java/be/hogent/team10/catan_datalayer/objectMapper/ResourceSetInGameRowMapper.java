package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class ResourceSetInGameRowMapper implements RowMapper {
    private String prefix = "";
    
    public ResourceSetInGameRowMapper(){
        super();
    }
    
    public ResourceSetInGameRowMapper(String prefix){
        this();
        this.prefix = prefix;
    }

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ResourceSet resourceSet = new ResourceSet();
        for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
            String colname = rs.getMetaData().getColumnName(col);
            if (colname.equalsIgnoreCase(prefix+"GRAIN")) {
                resourceSet.add(Resource.GRAIN, rs.getInt(prefix+"GRAIN"));
            } else if (colname.equalsIgnoreCase(prefix+"LUMBER")) {
                resourceSet.add(Resource.LUMBER, rs.getInt(prefix+"LUMBER"));

            } else if (colname.equalsIgnoreCase(prefix+"ORE")) {
                resourceSet.add(Resource.ORE, rs.getInt(prefix+"ORE"));

            } else if (colname.equalsIgnoreCase(prefix+"WOOL")) {
                resourceSet.add(Resource.WOOL, rs.getInt(prefix+"WOOL"));

            } else if (colname.equalsIgnoreCase(prefix+"BRICK")) {
                resourceSet.add(Resource.BRICK, rs.getInt(prefix+"BRICK"));

            } else if (colname.equalsIgnoreCase("TOTAL")) {
                resourceSet.setTotal(rs.getInt("TOTAL"));
            }
        }
        return resourceSet;
    }
}
