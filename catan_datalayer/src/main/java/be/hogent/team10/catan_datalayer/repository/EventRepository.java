package be.hogent.team10.catan_datalayer.repository;

import be.hogent.team10.catan_businesslogic.util.Event;
import be.hogent.team10.catan_businesslogic.util.EventContainer;
import be.hogent.team10.catan_datalayer.objectMapper.EventRowMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author HP
 */
@Repository
public class EventRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EventContainer checkSync(int gameId, int gameStamp) {
        String checkSyncQuery = "SELECT GAME_ID, EVENT_NUMBER, EVENT_DESCRIPTION, AFFECTED_ID  FROM GAME_EVENT WHERE game_id = ? and EVENT_NUMBER > ? ";
        List<Event> events = jdbcTemplate.query(checkSyncQuery, new Object[]{gameId, gameStamp}, new EventRowMapper());
        return new EventContainer(events);
    }

    public List<Integer> getEvents(int gameStamp) {

        return null;
    }

    /**
     *
     * @param gameid what game this event applies to.
     * @param playerid who caused the change
     * @param event textual representation of what has happenend
     * @param minorNumber number to indicate that this update is part of a
     * larger series of updates.
     *
     * majornumber will be: <((previousnumber/100)<to get rid of minor number> *
     * 100)+ (playerid*100) + minornumber (0-99)>
     */
    public void createEvent(int gameid, int playerid, String event, int minorNumber) {
    }

    public void addEvent(int gameId, String event) {
        String addEventQuery = "Insert into Game_Event(Game_id, Event_description) values( ?, ?)";
        jdbcTemplate.update(addEventQuery, new Object[]{gameId, event});

    }

    public Map<Integer, List<String>> getWhatYouAllreadyDidList(int gameId, int playerId) {
        String whatYouAllreadyDidListQuery = "WITH mygame AS( SELECT ? gameid, ? playerid  FROM dual), "
                + " myturns AS (SELECT turn_id, row_number() OVER (PARTITION BY player_id ORDER BY turn_id DESC) rij FROM game_turn, "
                + "mygame WHERE player_id = mygame.playerid ORDER BY turn_id DESC)"
                + ",mylastturns AS (SELECT turn_id FROM myturns WHERE rij <= 2)"
                + "select * from game_event where turn_id in(select * from mylastturns) order by turn_id desc, event_number desc";
        List<Event> whatYouAllreadyDidList = jdbcTemplate.query(whatYouAllreadyDidListQuery, new Object[]{gameId, playerId}, new EventRowMapper());
        return new EventContainer().analyse(whatYouAllreadyDidList);
    }
}