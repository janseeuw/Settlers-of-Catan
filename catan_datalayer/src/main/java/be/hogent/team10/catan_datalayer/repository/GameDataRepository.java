package be.hogent.team10.catan_datalayer.repository;

import be.hogent.team10.catan_businesslogic.model.ChatMessage;
import be.hogent.team10.catan_businesslogic.model.Coordinate;
import be.hogent.team10.catan_businesslogic.model.DevelopmentCard;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.Tile;
import be.hogent.team10.catan_businesslogic.model.TileType;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import be.hogent.team10.catan_datalayer.objectMapper.CoordinateRowMapper;
import be.hogent.team10.catan_datalayer.objectMapper.DevelopmentCardRowMapper;
import be.hogent.team10.catan_datalayer.objectMapper.PlayerInGameRowMapper;
import be.hogent.team10.catan_datalayer.objectMapper.PlayerRowMapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author HP
 */
@Repository
public class GameDataRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    GameDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /*get 1
     */

    public int getDice(int gameId) throws SQLException, ObjectNotFoundException {
        String getDiceQuery = "select dice_result from game where game_id = ?";
        try {
            int result = jdbcTemplate.queryForInt(getDiceQuery, new Object[]{gameId});
            return result;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Someone lost the dices, result unknown.");
        }
    }

    public ResourceSet getResources(int gameId) {
        return null;
    }

    public Coordinate getRobber(int gameId) {
        String getRobberQuery = "Select Robber_x, Robber_y from game_data where game_id = ?";
        Coordinate x = (Coordinate) jdbcTemplate.queryForObject(getRobberQuery, new Object[]{gameId}, new CoordinateRowMapper());
        return x;
    }

    public List<Player> getAllPlayerData(int gameId, int playerId) {
        String getAllPlayerData = "With Curplayer As (Select ? Pid From Dual) "
                + "Select p.player_id, p.player_name, p.player_avatar, pg.player_color \"PLAYER_COLOR\","
                + "Case When pg.Player_Id = Curplayer.Pid Then lumber Else 0 End lumber, "
                + "Case When pg.Player_Id = Curplayer.Pid Then grain Else 0 End grain, "
                + "Case When pg.Player_Id = Curplayer.Pid Then ore Else 0 End ore, "
                + "Case When pg.Player_Id = Curplayer.Pid Then Brick Else 0 End Brick, "
                + "Case When pg.Player_Id = Curplayer.Pid Then Wool Else 0 End Wool, "
                + "(lumber + grain + ore + brick + wool) \"total\", "+
"                (SELECT count(1) FROM component_game WHERE game_id = ? AND player_id = p.player_id AND component_name = 'STREET' )  \"STREETS\" ,\n" +
"                (SELECT count(1) FROM component_game WHERE game_id = ? AND player_id = p.player_id AND component_name = 'SETTLEMENT' )  \"SETTLEMENTS\" ,\n" +
"                (select count(1) from component_game where game_id = ? and player_id = p.player_id and component_name = 'CITY' )  \"CITIES\"   "
                
                + "From Curplayer, Player_Game pg join Player p on pg.player_id = p.player_Id "
                + "join resourceset r on r.resourceset_id = pg.resourceset_id where pg.game_id = ? ";
        List<Player> playerData = jdbcTemplate.query(getAllPlayerData, new Object[]{playerId, gameId, gameId, gameId, gameId}, new PlayerInGameRowMapper());
        return playerData;
    }

    /*
     * only to be used for serverside - validation purposes. don't use to send to clients.
     * fetches all data including full resourcesets;
     */
    public List<Player> getAllPlayerData(int gameId) {
        String getAllPlayerData = "Select p.player_id, p.player_name, p.player_avatar, pg.time_joined \"TIME_JOINED\", pg.player_color \"PLAYER_COLOR\","
                + "lumber, "
                + "grain, "
                + "ore, "
                + "Brick, "
                + "Wool, "+
"                (SELECT count(1) FROM component_game WHERE game_id = ? AND player_id = p.player_id AND component_name = 'STREET' )  \"STREETS\" ,\n" +
"                (SELECT count(1) FROM component_game WHERE game_id = ? AND player_id = p.player_id AND component_name = 'SETTLEMENT' )  \"SETTLEMENTS\" ,\n" +
"                (select count(1) from component_game where game_id = ? and player_id = p.player_id and component_name = 'CITY' )  \"CITIES\" "
                + " from "
                + "Player_Game pg join Player p on pg.player_id = p.player_Id "
                + "join resourceset r on r.resourceset_id = pg.resourceset_id where pg.game_id = ? ";
        List<Player> playerData = jdbcTemplate.query(getAllPlayerData, new Object[]{gameId,gameId,gameId,gameId}, new PlayerInGameRowMapper());
        return playerData;
    }

    /*
     * only to be used for serverside - validation purposes. don't use to send to clients.
     */
    public Player getPlayerData(int gameId, int playerId) {
        String getAllPlayerData = "Select p.player_id, p.player_name, p.player_avatar , pg.player_color \"PLAYER_COLOR\","
                + "lumber, "
                + "grain, "
                + "ore, "
                + "Brick, "
                + "Wool, " +
"                (SELECT count(1) FROM component_game WHERE game_id = ? AND player_id = p.player_id AND component_name = 'STREET' )  \"STREETS\" ,\n" +
"                (SELECT count(1) FROM component_game WHERE game_id = ? AND player_id = p.player_id AND component_name = 'SETTLEMENT' )  \"SETTLEMENTS\" ,\n" +
"                (select count(1) from component_game where game_id = ? and player_id = p.player_id and component_name = 'CITY' )  \"CITIES\"  from "
                + "Player_Game pg join Player p on pg.player_id = p.player_Id "
                + "join resourceset r on r.resourceset_id = pg.resourceset_id where pg.game_id = ? and p.player_id = ?";
        Player playerData = (Player) jdbcTemplate.queryForObject(getAllPlayerData, new Object[]{gameId, gameId, gameId, gameId, playerId}, new PlayerInGameRowMapper());
        return playerData;
    }

    /*get many
     */
    public List<Player> getPlayers(int gameId) {
        String getGamePlayers = "SELECT P.Player_Id, P.Player_Name FROM PLAYER_GAME PG JOIN PLAYER P ON PG.PLAYER_ID = P.PLAYER_ID WHERE PG.GAME_ID = ? order by PG.TIME_JOINED";
        List<Player> players = jdbcTemplate.query(getGamePlayers, new Object[]{gameId}, new PlayerRowMapper());
        return players;
    }

    /*insert
     */
    public void addPlayer(int gameId, int playerid) {
        String newQuery = "DECLARE R_ID RESOURCESET.RESOURCESET_ID%TYPE; "
                + "BEGIN "
                + "  INSERT INTO PLAYER_GAME(GAME_ID, PLAYER_COLOR, PLAYER_ID)VALUES (?,(WITH colors AS (SELECT player_color FROM player_game WHERE game_id = ?) SELECT CASE WHEN 'BLUE' IN (SELECT * FROM colors) AND 'GREEN' IN (SELECT * FROM colors) AND 'RED' IN (SELECT * FROM colors) THEN 'YELLOW' \n"
                + " WHEN 'BLUE' IN (SELECT * FROM colors) AND 'GREEN' IN (SELECT * FROM colors)  THEN 'RED' \n"
                + " WHEN 'BLUE' IN (SELECT * FROM colors) THEN 'GREEN' \n"
                + " ELSE 'BLUE' END from dual) ,?);"
                + "  INSERT INTO RESOURCESET(GAME_ID, PLAYER_ID)VALUES (?, ?) RETURNING RESOURCESET_ID INTO R_ID;"
                + "  UPDATE PLAYER_GAME SET RESOURCESET_ID = R_ID where game_ID = ? AND PLAYER_ID = ?;"
                + "  COMMIT; "
                + " END;";
        jdbcTemplate.update(newQuery, gameId, gameId, playerid, gameId, playerid, gameId, playerid);
    }

    /*update
     */
    public void updateDice(int gameId, int eyes) throws SQLException {
        String updateDiceQuery = "update game set dice_result = ? where game_id = ?";
        jdbcTemplate.update(updateDiceQuery, eyes, gameId);
    }
    
    public void updateDevelopmentCard(int gameId, int cardId) throws SQLException {
        String updateCardQuery = "UPDATE GAME_CARD SET TURN_PLAYED = (select turn_id from game where game_id = ?) WHERE GAME_ID = ? AND CARD_ID = ?";
        jdbcTemplate.update(updateCardQuery, gameId, gameId, cardId);
    }

    public void updateResources(int gameId, ResourceSet resourceSet) {
        String updateGameResourcesQuery = "Update resourceset set lumber = ?, grain = ?, ore = ?, brick = ?, wool = ? where resourceset_id = (select resourceset_id from game where game_id = ?)";
        jdbcTemplate.update(updateGameResourcesQuery, new Object[]{resourceSet.getAmount(Resource.LUMBER), resourceSet.getAmount(Resource.GRAIN), resourceSet.getAmount(Resource.ORE), resourceSet.getAmount(Resource.BRICK), resourceSet.getAmount(Resource.WOOL), gameId});
    }

    public void updatePlayerResources(int gameId, int playerId, ResourceSet resourceSet) {
        String updatePlayerResourcesQuery = "Update resourceset set lumber = ?, grain = ?, ore = ?, brick = ?, wool = ? where game_id = ? and player_id = ?";
        jdbcTemplate.update(updatePlayerResourcesQuery, new Object[]{resourceSet.getAmount(Resource.LUMBER), resourceSet.getAmount(Resource.GRAIN), resourceSet.getAmount(Resource.ORE), resourceSet.getAmount(Resource.BRICK), resourceSet.getAmount(Resource.WOOL), gameId, playerId});
    }

    public void updateRobber(int gameId, Coordinate robber) {
        String updateRobberQuery = "Update game set ROBBER_X = ?, ROBBER_Y= ? where game_id = ?";
        jdbcTemplate.update(updateRobberQuery, new Object[]{robber.getX(), robber.getY(), gameId});
    }

    /*remove
     */
    public void removePlayer(int gameId, int playerid) {
        String removePlayerToGame = "DELETE FROM PLAYER_GAME where game_id = ? and player_id = ?";
        jdbcTemplate.update(removePlayerToGame, gameId, playerid);
    }

    List<Player> getSelectivePlayerData(int playerId, int gameId, List<Integer> affectedIds) {
        String id_string = "( ;";
        for (int i : affectedIds) {
            id_string = id_string.replace(";", " " + i + ",; ");
        }
        //last occurence
        if (id_string.contains(",;")) {
            id_string = id_string.replace(",;", ")");
        }
        //no numbers in list.
        if (id_string.contains(";")) {
            id_string = id_string.replace(";", ")");
        }
        String getAllPlayerData = "With Curplayer As (Select ? Pid From Dual) "
                + "Select p.player_id, p.player_name, p.player_avatar,"
                + "Case When pg.Player_Id = Curplayer.Pid Then lumber Else 0 End lumber, "
                + "Case When pg.Player_Id = Curplayer.Pid Then grain Else 0 End grain, "
                + "Case When pg.Player_Id = Curplayer.Pid Then ore Else 0 End ore, "
                + "Case When pg.Player_Id = Curplayer.Pid Then Brick Else 0 End Brick, "
                + "Case When pg.Player_Id = Curplayer.Pid Then Wool Else 0 End Wool, "
                + "(lumber + grain + ore + brick + wool) \"total\", "
                + "pg.player_color \"PLAYER_COLOR\", "+
"                (SELECT count(1) FROM component_game WHERE game_id = ? AND player_id = p.player_id AND component_name = 'STREET' )  \"STREETS\" ,\n" +
"                (SELECT count(1) FROM component_game WHERE game_id = ? AND player_id = p.player_id AND component_name = 'SETTLEMENT' )  \"SETTLEMENTS\" ,\n" +
"                (select count(1) from component_game where game_id = ? and player_id = p.player_id and component_name = 'CITY' )  \"CITIES\"   "
                
                + "From Curplayer, Player_Game pg join Player p on pg.player_id = p.player_Id "
                + "join resourceset r on r.resourceset_id = pg.resourceset_id where pg.game_id = ? and pg.player_id in " + id_string;
        List<Player> playerData = jdbcTemplate.query(getAllPlayerData, new Object[]{playerId, gameId, gameId, gameId, gameId}, new PlayerInGameRowMapper());
        return playerData;
    }

    public void updatePlayerProperty(int gameId, Player player) {
        String updatePlayerPropertyQuery = "UPDATE ";
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<ChatMessage> getAllMessages(int gameId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<ChatMessage> getSelectiveMessages(int gameId, List<Integer> ids) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void buyCard(int playerId, int gameId, int cardId) {
        String buyCardQuery = "Update game_card set player_id = ?, turn_bought = (select turn_id from game where game_id = ?) where card_id = ?";
        jdbcTemplate.update(buyCardQuery, new Object[]{playerId, gameId, cardId});
    }

    public void playCard(int playerId, int cardId, int gameId) {
        String playCardQuery = "Update game_card set turn_played = (select turn_id from game where game_id = ?)";
        jdbcTemplate.update(playCardQuery, new Object[]{cardId});
    }

    public List<DevelopmentCard> getCards(int gameId) {
        String getCardsQuery = "Select CARD_ID, PLAYER_ID, CARD_TYPE, TURN_BOUGHT, TURN_PLAYED from game_card where game_id = ?";
        List<DevelopmentCard> cards = jdbcTemplate.query(getCardsQuery, new Object[]{gameId}, new DevelopmentCardRowMapper());
        return cards;
    }

    public List<DevelopmentCard> getCard(int gameId, int playerId, String type){
        String getCardQuery = "SELECT CARD_ID, PLAYER_ID, CARD_TYPE, TURN_BOUGHT, TURN_PLAYED FROM GAME_CARD"
                +" WHERE PLAYER_ID = ? AND GAME_ID = ? AND CARD_TYPE = ? AND TURN_PLAYED IS NULL AND TURN_BOUGHT <> (select turn_id from game where game_id = ?)";
        List<DevelopmentCard> card = jdbcTemplate.query(getCardQuery, new Object[]{playerId,gameId,type, gameId}, new DevelopmentCardRowMapper());
        return card;
    }
    public void addCards(final List<DevelopmentCard> cards, final int gameId) {
        String insertTileQuery = "Insert into game_card(game_id, card_type) values(?,?)";
        jdbcTemplate.batchUpdate(insertTileQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DevelopmentCard card = cards.get(i);
                ps.setInt(1, gameId);
                ps.setString(2, card.getDevelopmentCardType().name());
            }

            @Override
            public int getBatchSize() {
                return cards.size();
            }
        });
    }

    List<DevelopmentCard> getSelectiveCardSync(int gameId, int playerId, List<Integer> affectedIds) {
        String id_string = "( ;";
        for (int i : affectedIds) {
            id_string = id_string.replace(";", " " + i + ",; ");
        }
        //last occurence
        if (id_string.contains(",;")) {
            id_string = id_string.replace(",;", ")");
        }
        //no numbers in list.
        if (id_string.contains(";")) {
            id_string = id_string.replace(";", ")");
        }
        String getSelectiveCards = "select CARD_ID, CARD_TYPE, TURN_BOUGHT, TURN_PLAYED, PLAYER_ID from game_card where game_id = ? AND player_id = ?";
        
        List<DevelopmentCard> cards = jdbcTemplate.query(getSelectiveCards, new Object[]{gameId, playerId}, new DevelopmentCardRowMapper());
        return cards;
    }
}