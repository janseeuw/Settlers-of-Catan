/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.repository;

import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.Resource;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.TradeRequest;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import be.hogent.team10.catan_datalayer.objectMapper.PlayerInGameRowMapper;
import be.hogent.team10.catan_datalayer.objectMapper.TradeInGameRowMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author HP
 */
@Repository
public class TradeRepository {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  TradeRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<TradeRequest> getFullSync(int gameId, int playerId) {

    return null;
  }

  List<TradeRequest> getSelectiveSync(int gameId, int playerId, List<Integer> affectedIds) {
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
    String getSelectiveSyncQuery = "SELECT TRADE_ID, SENDING_PLAYER_ID, RECIEVING_PLAYER_ID, STATUS,"
            + " SRS.GRAIN \"S_GRAIN\", SRS.LUMBER \"S_LUMBER\", SRS.ORE \"S_ORE\", SRS.WOOL \"S_WOOL\", SRS.BRICK \"S_BRICK\","
            + " RRS.GRAIN \"S_GRAIN\", RRS.LUMBER \"R_LUMBER\", RRS.ORE \"R_ORE\", RRS.WOOL \"R_WOOL\", RRS.BRICK \"R_BRICK\" "
            + " FROM PLAYER_TRADE PT "
            + " JOIN RESOURCESET SRS ON PT.SENDING_RESOURCESET = SRS.RESOURCESET_ID "
            + " JOIN RESOURCESET RRS ON PT.RECIEVING_RESOURCESET = RRS.RESOURCESET_ID "
            + " where PT.game_id = ? and ( PT.SENDING_PLAYER_ID = ? or PT.RECIEVING_PLAYER_ID = ?) "
            + " AND PT.TRADE_ID in " + id_string;

    List<TradeRequest> requests = jdbcTemplate.query(getSelectiveSyncQuery, new Object[]{gameId, playerId, playerId}, new TradeInGameRowMapper());
    return requests;
  }

  public void updateTradeRequest(TradeRequest request) {
    String updateTradeRequestQuery = "update player_trade set STATUS = ? where TRADE_ID = ?";
    jdbcTemplate.update(updateTradeRequestQuery, request.getStatus().name(), request.getTradeRequestId());
  }

  public void addTradeRequest(TradeRequest request) {
    String insertTradeRequestQuery = "DECLARE S_R_ID RESOURCESET.RESOURCESET_ID%TYPE;"
            + " R_R_ID RESOURCESET.RESOURCESET_ID%TYPE; "
            + "BEGIN "
            + "  INSERT INTO RESOURCESET(GRAIN, BRICK, LUMBER, WOOL, ORE, GAME_ID) values(?,?,?,?,?,?) RETURNING RESOURCESET_ID into S_R_ID; "
            + "  INSERT INTO RESOURCESET(GRAIN, BRICK, LUMBER, WOOL, ORE, GAME_ID) values(?,?,?,?,?,?) RETURNING RESOURCESET_ID into R_R_ID; "
            + "  INSERT INTO PLAYER_TRADE(SENDING_PLAYER_ID, RECIEVING_PLAYER_ID, SENDING_RESOURCESET, RECIEVING_RESOURCESET, STATUS, GAME_ID) VALUES(?,?,S_R_ID,R_R_ID,?, ?); "
            + "  COMMIT; "
            + " END;";
    ResourceSet sset = request.getOffer();
    ResourceSet rset = request.getReward();
    jdbcTemplate.update(insertTradeRequestQuery,
            sset.getAmount(Resource.GRAIN),
            sset.getAmount(Resource.BRICK),
            sset.getAmount(Resource.LUMBER),
            sset.getAmount(Resource.WOOL),
            sset.getAmount(Resource.ORE),
            request.getGameId(),
            rset.getAmount(Resource.GRAIN),
            rset.getAmount(Resource.BRICK),
            rset.getAmount(Resource.LUMBER),
            rset.getAmount(Resource.WOOL),
            rset.getAmount(Resource.ORE),
            request.getGameId(),
            request.getPlayerSending(),
            request.getPlayerReceiving(),
            request.getStatus().name(),
            request.getGameId());
  }

  /*public TradeRequest get(int id) throws ObjectNotFoundException {
    String getTradeRequest = "SELECT TRADE_ID, SENDING_PLAYER_ID, RECIEVING_PLAYER_ID, STATUS,"
            + " SRS.GRAIN \"S_GRAIN\", SRS.LUMBER \"S_LUMBER\", SRS.ORE \"S_ORE\", SRS.WOOL \"S_WOOL\", SRS.BRICK \"S_BRICK\","
            + " RRS.GRAIN \"S_GRAIN\", RRS.LUMBER \"R_LUMBER\", RRS.ORE \"R_ORE\", RRS.WOOL \"R_WOOL\", RRS.BRICK \"R_BRICK\" "
            + " FROM PLAYER_TRADE PT "
            + " JOIN RESOURCESET SRS ON PT.SENDING_RESOURCESET = SRS.RESOURCESET_ID "
            + " JOIN RESOURCESET RRS ON PT.RECIEVING_RESOURCESET = RRS.RESOURCESET_ID "
            + " where PT.TRADE_ID = ?";
    try {
      TradeRequest request = (TradeRequest)jdbcTemplate.query(getTradeRequest, new Object[]{id}, new TradeInGameRowMapper());
      return request;
    } catch (EmptyResultDataAccessException e) {
      throw new ObjectNotFoundException("TradeRequest not found.");
    }
  }*/
  
  public List<TradeRequest> get(int id) throws ObjectNotFoundException {
    String getTradeRequest = "SELECT TRADE_ID, SENDING_PLAYER_ID, RECIEVING_PLAYER_ID, STATUS,"
            + " SRS.GRAIN \"S_GRAIN\", SRS.LUMBER \"S_LUMBER\", SRS.ORE \"S_ORE\", SRS.WOOL \"S_WOOL\", SRS.BRICK \"S_BRICK\","
            + " RRS.GRAIN \"S_GRAIN\", RRS.LUMBER \"R_LUMBER\", RRS.ORE \"R_ORE\", RRS.WOOL \"R_WOOL\", RRS.BRICK \"R_BRICK\" "
            + " FROM PLAYER_TRADE PT "
            + " JOIN RESOURCESET SRS ON PT.SENDING_RESOURCESET = SRS.RESOURCESET_ID "
            + " JOIN RESOURCESET RRS ON PT.RECIEVING_RESOURCESET = RRS.RESOURCESET_ID "
            + " where PT.TRADE_ID = ?";
    try {
      List<TradeRequest> requestList = jdbcTemplate.query(getTradeRequest, new Object[]{id}, new TradeInGameRowMapper());
      return requestList;
    } catch (EmptyResultDataAccessException e) {
      throw new ObjectNotFoundException("TradeRequest not found.");
    }
  }
}