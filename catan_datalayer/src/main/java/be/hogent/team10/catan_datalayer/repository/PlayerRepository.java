/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.repository;

import be.hogent.team10.catan_datalayer.objectMapper.PlayerRowMapper;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author HP
 */
@Repository
public class PlayerRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PlayerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     *
     * @param id
     * @return
     * @throws SQLException
     * @throws Exception
     *
     * Fetches player with that id from the database. If not found> exception If
     * spring(=propagation of a sql exception in a new class) problem >
     * ObjectNotFoundException.
     *
     * This player object does not contain any in-game data since no game-id was
     * specified.
     */
    public Player get(int id) throws ObjectNotFoundException {
        String playerSelectQuery = "SELECT PLAYER_ID, PLAYER_NAME, PLAYER_EMAIL from PLAYER WHERE PLAYER_ID = ?";
        try {
            Player player = (Player) jdbcTemplate.queryForObject(playerSelectQuery, new Object[]{id}, new PlayerRowMapper());
            return player;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Player not found.");
        }
    }

    public Player get(String userName) throws ObjectNotFoundException {
        String playerSelectQuery = "SELECT PLAYER_ID, PLAYER_NAME, PLAYER_EMAIL from PLAYER WHERE PLAYER_NAME = ?";
        try {
            Player player = (Player) jdbcTemplate.queryForObject(playerSelectQuery, new Object[]{userName}, new PlayerRowMapper());
            return player;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Player not found.");
        }
    }

    public List<Player> getAll() {
        String playerSelectQuery = "SELECT PLAYER_ID, PLAYER_NAME, PLAYER_ENABLED from PLAYER";
        List<Player> players = jdbcTemplate.query(playerSelectQuery,new PlayerRowMapper());
        return players;
    }

    public Player getFacebook(int facebook) throws ObjectNotFoundException {
        String playerSelectQuery = "SELECT PLAYER_ID, PLAYER_NAME, PLAYER_EMAIL from PLAYER WHERE PLAYER_FACEBOOK_NUMBER = ?";
        try {
            Player player = (Player) jdbcTemplate.queryForObject(playerSelectQuery, new Object[]{facebook}, new PlayerRowMapper());
            return player;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Player createPlayer(Player player) throws ObjectNotFoundException, DuplicateKeyException, SQLIntegrityConstraintViolationException {
        String insertPlayerQuery = "INSERT INTO PLAYER (PLAYER_NAME,PLAYER_EMAIL,PLAYER_PASSWORD,PLAYER_AVATAR,PLAYER_FACEBOOK_NUMBER) VALUES ( ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertPlayerQuery,
                player.getName(),
                player.getEmail(),
                player.getPassword(),
                player.getAvatar(),
                player.getFacebook());
        return get(player.getName());
    }

    public void updatePlayer(Player player) {
        String updatePlayerQuery = "UPDATE Player set PLAYER_NAME = ?, PLAYER_EMAIL = ?, player_AVATAR = ? where player_id = ?";
        jdbcTemplate.update(updatePlayerQuery, player.getName(), player.getEmail(), player.getAvatar(), player.getId());
    }

    public Player login(String playerName, String password) throws ObjectNotFoundException {
        String playerLoginQuery = "SELECT PLAYER_ID, PLAYER_NAME, PLAYER_EMAIL, PLAYER_ENABLED, PLAYER_ADMIN from PLAYER WHERE PLAYER_NAME = ? AND PLAYER_PASSWORD = ?";
        try {
            Player player = (Player) jdbcTemplate.queryForObject(playerLoginQuery, new Object[]{playerName, password}, new PlayerRowMapper());
            return player;
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectNotFoundException("No player exists with such credentials");
        }
    }

    public void activatePlayer(Player player) {
        String activatePlayerQuery = "UPDATE PLAYER SET PLAYER_ENABLED = 'Y' where player_id = ?";
        jdbcTemplate.update(activatePlayerQuery, player.getId());
    }

    public void updatePassword(Player player, String oldPassword) {
        String updatePasswordQuery = "UPDATE PLAYER SET player_password = ? where player_id = ? and player_password = ?";
        jdbcTemplate.update(updatePasswordQuery, player.getPassword(), player.getId(), oldPassword);
    }

    public void forceUpdatePassword(Player player) {
        String updatePasswordQuery = "UPDATE PLAYER SET player_password = ? where player_id = ?";
        jdbcTemplate.update(updatePasswordQuery, player.getPassword(), player.getId());
    }

    public void disablePlayer(Player player) {
        String disablePlayerQuery = "UPDATE PLAYER SET PLAYER_ENABLED = 'B' where player_id = ?";
        jdbcTemplate.update(disablePlayerQuery, player.getId());
    }
}
