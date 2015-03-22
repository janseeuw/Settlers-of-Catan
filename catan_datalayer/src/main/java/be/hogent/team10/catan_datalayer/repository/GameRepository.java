package be.hogent.team10.catan_datalayer.repository;

import be.hogent.team10.catan_businesslogic.model.DevelopmentCard;
import be.hogent.team10.catan_datalayer.objectMapper.GameRowMapper;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.simple.SimpleGame;
import be.hogent.team10.catan_businesslogic.util.Event;
import be.hogent.team10.catan_datalayer.objectMapper.GameInGameRowMapper;
import be.hogent.team10.catan_datalayer.objectMapper.SimpleGameMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Responsible for fetching data from the database. Gets injected with multiple
 * other repositories. These other repositories are injected here to maintain a
 * loosely coupled database-layer. This implies that the repository classes are
 * more tightly coupled. However, this was an architectural decision we had to
 * make and seemed the best solution at that moment.
 *
 * @author HP
 */
@Repository
public class GameRepository {

  private JdbcTemplate jdbcTemplate;
  private GameComponentRepository gameComponentRepository;
  private GameDataRepository gameDataRepository;
  private TradeRepository tradeRepository;

  /**
   * Constructor, quite obvious.
   *
   * @param jdbcTemplate the jdbcTemplate is injected by spring and acts as a
   * facade to access the database.
   */
  @Autowired
  public GameRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   *
   * @param gameComponentRepository a GameComponentRepository is injected to
   * prevent duplicate code.
   */
  @Autowired
  public void setGameComponentRepository(GameComponentRepository gameComponentRepository) {
    this.gameComponentRepository = gameComponentRepository;
  }

  @Autowired
  public void setTradeRepository(TradeRepository tradeRepository) {
    this.tradeRepository = tradeRepository;
  }

  @Autowired
  public void setGameDataRepository(GameDataRepository gameDataRepository) {
    this.gameDataRepository = gameDataRepository;
  }
  /*create
   */

  public Game addGame(Game g) {
    String addGameQuery = "DECLARE G_ID GAME.PLAYER_ID%TYPE;"
            + " R_ID RESOURCESET.RESOURCESET_ID%TYPE; "
            + "BEGIN "
            + "  INSERT INTO GAME(PLAYER_ID, GAME_STATE) VALUES (?,?) "
            + "  RETURNING GAME_ID INTO G_ID; "
            + "  INSERT INTO RESOURCESET(GRAIN, BRICK, LUMBER, WOOL, ORE, GAME_ID) values(20,20,20,20,20, G_ID) RETURNING RESOURCESET_ID into R_ID; "
            + "  UPDATE GAME SET RESOURCESET_ID = R_ID where game_ID = G_ID;"
            + ""
            + "  COMMIT; "
            + " END;";
    jdbcTemplate.update(addGameQuery, g.getCreator(), "INITIALIZATION_STATE");
    int gameId = jdbcTemplate.queryForInt("SELECT MAX(GAME_ID) from GAME WHERE PLAYER_ID = ?", g.getCreator());
    g.setGameId(gameId);
    return g;
  }

  /**
   * fetches ingame data. used by the client used to play the game.
   */
  public Game getFullGameSkeleton(int gameId) throws SQLException {
    String inGameGameSelectQuery = "SELECT G.GAME_ID, G.PLAYER_ID \"CREATOR\", GRAIN, LUMBER, ORE, WOOL, BRICK, ROBBER_X, ROBBER_Y, DICE_RESULT, CURRENT_PLAYER, "
            + " GAME_STATE, (select max(event_number) from game_Event ev where ev.game_id = g.game_id group by game_id) GAME_STAMP FROM GAME G  "
            + " JOIN RESOURCESET R ON R.RESOURCESET_ID = G.RESOURCESET_ID "
            + " WHERE G.GAME_ID = ?";
    Game g = (Game) jdbcTemplate.queryForObject(inGameGameSelectQuery, new Object[]{gameId}, new GameInGameRowMapper());
    return g;
  }

  /**
   * //TODO csv query to reduce database-access. fetches all games that are
   * currently running or are still searching for more players.
   */
  public List<Game> getAll() {
    String getAllGamesQuery = "Select G.Game_Id, g.PLAYER_ID, G.Time_Created, G.Time_Started from Game G";
    List<Game> games = jdbcTemplate.query(getAllGamesQuery, new GameRowMapper());
    for (Game g : games) {
      g.setPlayers(gameDataRepository.getPlayers(g.getGameId()));
    }
    return games;
  }

  /**
   * //TODO csv query to reduce database-access. fetches all games that are
   * currently running or are still searching for more players.
   */
  public List<SimpleGame> getPaged(int from, int numberPerPage, int sortBy) {
    String getAllGamesQuery = "WITH games  "
            + "     AS (SELECT g.game_id,  "
            + "                  to_char(time_created, 'yyyy-mm-dd HH24:MM:SS') TIME_CREATED, "
            + "                CASE  "
            + "                  WHEN g.game_state = 'INITIALIZATION_STATE'  "
            + "                       AND (SELECT Count(1)  "
            + "                            FROM   player_game pg  "
            + "                            WHERE  pg.game_id = g.game_id) < 4 THEN 'Waiting for players'  "
            + "                  ELSE 'running'  "
            + "                END status  "
            + "         FROM   game g ), "
            + "  games_players AS( "
            + "    SELECT g.game_id, g.time_created, g.status, pg.player_id, p.player_name,  "
            + "    row_number() OVER(PARTITION BY g.game_id ORDER BY g.time_created DESC, g.status DESC) nummer, "
            + "    count(1) over (partition by g.game_id) aantal FROM games g "
            + "    LEFT JOIN player_game pg ON pg.game_id = g.game_id "
            + "    LEFT JOIN player p ON pg.player_id = p.player_id "
            + "  ), paged AS"
            + " (SELECT GAME_ID, TIME_CREATED, STATUS,  ltrim(sys_connect_by_path(player_name, ';'), ';') PLAYERS "
            + " FROM   games_players"
            + " where level = aantal "
            + " START WITH nummer = 1 "
            + " CONNECT BY PRIOR game_id = game_id "
            + " AND PRIOR nummer = nummer -1 "
            + " ), x AS( "
            + " SELECT p.*, row_number() ";

    if (sortBy == 2) {
      getAllGamesQuery += " OVER( ORDER BY time_created desc, status asc ) volgnr";
    } else {
      getAllGamesQuery += " OVER( ORDER BY status asc, time_created desc) volgnr  ";
    }
    getAllGamesQuery += " FROM paged p), aantal as(select count(1) \"AANTAL\" from game) "
            + "SELECT x.*, aantal.aantal FROM x, aantal WHERE volgnr between ? and ? "
            + "";
    List<SimpleGame> games = jdbcTemplate.query(getAllGamesQuery, new Object[]{((from - 1) * numberPerPage), (from * numberPerPage)}, new SimpleGameMapper());
    return games;
  }

  /**
   * Fetches game-data from the database. Mainly for use on website. //TODO is
   * this method needed? (not used at this moment -- 7/04/2013 --
   *
   * @param id id of the game you wish to fetch from the data.
   * @return
   */
  public Game getGameById(int id) {
    String gameSelectQuery = "Select G.Game_Id, g.Time_Created, G.Time_Started, Ev.Event_Number From Game G,"
            + " (Select Max(Event_Number) Event_Number From Game_Event Where Game_Id = ?) Ev Where G.Game_Id = ?";
    Game g = (Game) jdbcTemplate.queryForObject(gameSelectQuery, new Object[]{id, id}, new GameRowMapper());
    return g;
  }

  /**
   * Sometimes a full sync is required. For example when the client has just
   * been downloaded and is still initializing the board. All data is collected
   * immediatly and if any tasks are still not finished, these are also set.
   * Because tradeRequests are not always present we only fetch that data when
   * needed.
   *
   * @param gameId id of the game you wish to fetch from the data.
   * @param playerId id of the player that has asked data.
   * @param events used to determine if any tasks or traderequests are pending.
   * @return
   */
  public Game getFullSync(int gameId, int playerId, List<Event> events) {
    String inGameGameSelectQuery = "SELECT g.GAME_ID, GRAIN, LUMBER, ORE, WOOL, BRICK, ROBBER_X, ROBBER_Y, DICE_RESULT, TURN_ID, G.PLAYER_ID \"CREATOR\", CURRENT_PLAYER, GAME_STATE FROM GAME g JOIN RESOURCESET R on R.RESOURCESET_ID = g.RESOURCESET_ID WHERE g.GAME_ID = ?";
    Game g = (Game) jdbcTemplate.queryForObject(inGameGameSelectQuery, new Object[]{gameId}, new GameInGameRowMapper());
    /* if (events != null) {
     for (Event s : events) {
     if (s.getEventDescription().equalsIgnoreCase("GAME_MOVE_ROBBER")) {
     g.addTask(new Task("GAME_MOVE_ROBBER", true));
     } else if (s.getEventDescription().equalsIgnoreCase("PLAYER_TRADE")) {
     g.setTradeRequests(tradeRepository.getFullSync(gameId, playerId));
     }
     }
     }*/
    g.setMy_id(playerId);
    g.setPlayers(gameDataRepository.getAllPlayerData(gameId, playerId));
    g.setBorders(gameComponentRepository.getAllBorders(gameId));
    g.setNodes(gameComponentRepository.getAllNodes(gameId));
    g.setTiles(gameComponentRepository.getAllTiles(gameId));
    g.setDevelopmentCards(gameDataRepository.getCards(gameId));
    return g;
  }

  /**
   * When polling, there is no need to send all data about the game over the
   * cable. Therefore, a list of events has been fetched from the database
   * before calling this method. NPC-data, such as bank-resources, robber and
   * dice are always updated. other, player-controlled data is only fetched from
   * database when required.
   *
   * @param gameId id of the game you wish to fetch from the data.
   * @param playerId id of the player that has asked data.
   * @param events used to determine if any tasks or traderequests are pending.
   * @return
   */
  public Game getSelectiveSync(int gameId, int playerId, List<Event> events) {
    String inGameGameSelectQuery = "SELECT g.GAME_ID, GRAIN, LUMBER, ORE, WOOL, BRICK, ROBBER_X, ROBBER_Y, DICE_RESULT, TURN_ID, G.PLAYER_ID \"CREATOR\", CURRENT_PLAYER, GAME_STATE FROM GAME g JOIN RESOURCESET R on R.RESOURCESET_ID = g.RESOURCESET_ID WHERE g.GAME_ID = ?";
    Game g = (Game) jdbcTemplate.queryForObject(inGameGameSelectQuery, new Object[]{gameId}, new GameInGameRowMapper());
    List<Integer> cardIds = new ArrayList<Integer>();

    for (Event s : events) {
      if (s.getEventDescription().equalsIgnoreCase("PLAYER_GAME")) {
        g.setPlayers(gameDataRepository.getAllPlayerData(gameId, playerId));
      } else if (s.getEventDescription().equalsIgnoreCase("COMPONENT_GAME")) {
        g.setBorders(this.gameComponentRepository.getSelectiveBorders(gameId, s.getAffectedIds()));
        g.setNodes(this.gameComponentRepository.getSelectiveNodes(gameId, s.getAffectedIds()));
      } else if (s.getEventDescription().equalsIgnoreCase("PLAYER_RESOURCE")) {
        g.setPlayers(gameDataRepository.getSelectivePlayerData(playerId, gameId, s.getAffectedIds()));
      } else if (s.getEventDescription().equalsIgnoreCase("PLAYER_TRADE_SENT")) {
        g.setTradeRequests(tradeRepository.getSelectiveSync(gameId, playerId, s.getAffectedIds()));
      } else if (s.getEventDescription().equalsIgnoreCase("PLAYER_TRADE_ANSWERED")) {
        g.setTradeRequests(tradeRepository.getSelectiveSync(gameId, playerId, s.getAffectedIds()));
      } else if (s.getEventDescription().equalsIgnoreCase("BOUGHT_CARD")) {
        cardIds.addAll(s.getAffectedIds());
      } else if (s.getEventDescription().equalsIgnoreCase("PLAYED_CARD")) {
        cardIds.addAll(s.getAffectedIds());
      }
      if (!cardIds.isEmpty()) {
        g.setDevelopmentCards(gameDataRepository.getSelectiveCardSync(gameId, g.getCurrentPlayer(), s.getAffectedIds()));
      }

    }
    g.setMy_id(playerId);
    return g;
  }

  /**
   * don't use
   */
  @Deprecated
  public void endTurn(int gameId, int playerId) {
    String endTurnQuery = "INSERT ALL "
            + "INTO GAME_EVENT(GAME_ID, EVENT_DESCRIPTION, AFFECTED_ID) VALUES(?, 'PLAYER_LOCK', ?)"
            + "INTO GAME_EVENT(GAME_ID, EVENT_DESCRIPTION, AFFECTED_ID) "
            + "VALUES(?, 'PLAYER_UNLOCK',"
            + " ( select player_id from "
            + "  (SELECT * FROM PLAYER_GAME WHERE PLAYER_ID NOT IN "
            + "   (SELECT * FROM "
            + "    ("
            + "      SELECT  AFFECTED_ID FROM "
            + "        (SELECT DISTINCT AFFECTED_ID, ROW_NUMBER() OVER(PARTITION BY EVENT_DESCRIPTION ORDER BY EVENT_NUMBER DESC) ROWNR FROM GAME_EVENT WHERE EVENT_DESCRIPTION = "
            + "            'PLAYER_UNLOCK' AND GAME_ID = ? "
            + "        ) "
            + "       "
            + "      WHERE ROWNR < (SELECT COUNT(1) FROM PLAYER_GAME  WHERE GAME_ID = ? GROUP BY GAME_ID) "
            + "     ) "
            + "   ) "
            + "  ) "
            + " )"
            + ") "
            + "  select * from dual ";
    jdbcTemplate.update(endTurnQuery, gameId, playerId, gameId, gameId, gameId);
  }

  public void setNextPlayer(int gameId, int playerId) {
    String nextPlayerQuery = "update game set current_player = ? where game_id = ?";
    jdbcTemplate.update(nextPlayerQuery, playerId, gameId);
  }

  public void updateGameState(Game g) {
    String updateGameStateQuery = "update game set game_state = ? where game_id = ?";
    jdbcTemplate.update(updateGameStateQuery, g.getGameState().getCurrentState(), g.getGameId());
  }
}