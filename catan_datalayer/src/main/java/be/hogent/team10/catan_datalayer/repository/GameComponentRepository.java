package be.hogent.team10.catan_datalayer.repository;

import be.hogent.team10.catan_businesslogic.model.Border;
import be.hogent.team10.catan_businesslogic.model.Node;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.Tile;
import be.hogent.team10.catan_businesslogic.model.TileType;
import be.hogent.team10.catan_datalayer.objectMapper.BorderInGameRowMapper;
import be.hogent.team10.catan_datalayer.objectMapper.NodeInGameRowMapper;
import be.hogent.team10.catan_datalayer.objectMapper.TileInGameRowMapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author HP
 */
@Repository
public class GameComponentRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GameComponentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*get 1
     */
    public Node getNode(int componentId, int gameId) {
        return null;
    }

    public Border getBorder(int componentId, int gameId) {
        return null;
    }

    /*get many
     */
    public List<Border> getAllBorders(int gameId) {
        String selectAllBordersQuery = "SELECT GAME_ID, COORDINATE_X, COORDINATE_Y, COMPONENT_NAME, PLAYER_ID FROM COMPONENT_GAME WHERE GAME_ID = ? and COMPONENT_NAME = 'STREET'";
        List<Border> data = jdbcTemplate.query(selectAllBordersQuery, new Object[]{gameId}, new BorderInGameRowMapper());
        return data;
    }

    public List<Border> getSelectiveBorders(int gameId, List<Integer> affectedIds) {
        String id_string = "( ;";
        for(int i : affectedIds){
            id_string = id_string.replace(";", " "+i+ ",; ");
        }
        //last occurence
        if(id_string.contains(",;"))
            id_string = id_string.replace(",;", ")");
        //no numbers in list.
        if(id_string.contains(";"))
            id_string = id_string.replace(";", ")");
        String selectiveBorderQuery = "SELECT GAME_ID, COORDINATE_X, COORDINATE_Y, COMPONENT_NAME, PLAYER_ID FROM COMPONENT_GAME WHERE game_id = ? and COMPONENT_NAME = 'STREET' and COMPONENT_ID in "+id_string;
        List<Border> data = jdbcTemplate.query(selectiveBorderQuery, new Object[]{ gameId}, new BorderInGameRowMapper());
        return data;
    }
    
    public List<Node> getAllNodes(int gameId) {
        String selectAllNodesQuery = "select GAME_ID, COORDINATE_X, COORDINATE_Y, COMPONENT_NAME, PLAYER_ID from component_game where game_id = ? and (component_name = 'SETTLEMENT' or component_name = 'CITY')";
        List<Node> nodes = jdbcTemplate.query(selectAllNodesQuery, new Object[]{gameId}, new NodeInGameRowMapper());
        return nodes;
    }
    
    public List<Node> getSelectiveNodes(int gameId, List<Integer> affectedIds) {       
        String id_string = "( ;";
        for(int i : affectedIds){
            id_string = id_string.replace(";", " "+i+ ",; ");
        }
        //last occurence
        if(id_string.contains(",;"))
            id_string = id_string.replace(",;", ")");
        //no numbers in list.
        if(id_string.contains(";"))
            id_string = id_string.replace(";", ")");
        String selectiveBorderQuery = "SELECT GAME_ID, COORDINATE_X, COORDINATE_Y, COMPONENT_NAME, PLAYER_ID FROM COMPONENT_GAME WHERE game_id = ? AND COMPONENT_NAME <> 'STREET' and COMPONENT_ID in "+id_string;
        List<Node> data = jdbcTemplate.query(selectiveBorderQuery, new Object[]{ gameId}, new NodeInGameRowMapper());
        return data;
    }
    
    public List<Tile> getAllTiles(int gameId) {
        String selectAllTilesQuery = "select game_id, coordinate_x, coordinate_y, tile_name, tile_number from tile_game where game_id = ?";
        List<Tile> tiles = jdbcTemplate.query(selectAllTilesQuery, new Object[]{gameId}, new TileInGameRowMapper());
        return tiles;
    }
    /*insert
     */
    public void addBorder(Border b, int gameId, Player player) {
        String insertBorderQuery = "Insert into component_game(game_id, coordinate_x, coordinate_y, component_name, player_id) values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertBorderQuery, new Object[]{gameId, b.getCoordinate().getX(), b.getCoordinate().getY(),"STREET", player.getId()});
    }

    public void addNode(Node node, int gameId, Player player) {
        String insertNodeQuery = "Insert into component_game(game_id, Coordinate_x, coordinate_y, component_name, player_id) values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertNodeQuery, new Object[]{gameId, node.getCoordinate().getX(), node.getCoordinate().getY(), "SETTLEMENT", player.getId()});
    }

    public void addTiles(final List<Tile> tiles, final int gameId){
        String insertTileQuery = "Insert into tile_game(game_id, coordinate_x, coordinate_y, tile_name, tile_number) values(?,?,?,?,?)";
        jdbcTemplate.batchUpdate(insertTileQuery, new BatchPreparedStatementSetter() {
 
	@Override
	public void setValues(PreparedStatement ps, int i) throws SQLException {
		Tile tile = tiles.get(i);
		ps.setInt(1, gameId);
		ps.setInt(2, tile.getCoordinate().getX());
		ps.setInt(3, tile.getCoordinate().getY());
		ps.setString(4, (tile.getTileType() == TileType.RESOURCE ? tile.getResource().name() : tile.getTileType().name()));
		ps.setInt(5, tile.getNumber());
	}
 
	@Override
	public int getBatchSize() {
		return tiles.size();
	}
  });
    }
    
    
    /*update
     */
    public void updateNode(Node node, int gameId, Player player) {
        String updateNodeQuery = "Update component_game set component_name = 'CITY' where game_id = ? and coordinate_x = ? and coordinate_y =? and player_id = ? and component_name = 'SETTLEMENT'";
        jdbcTemplate.update(updateNodeQuery, new Object[]{gameId, node.getCoordinate().getX(), node.getCoordinate().getY(), player.getId()});
    }
}
