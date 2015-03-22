package be.hogent.team10.catan_resources;

import be.hogent.team10.catan_businesslogic.model.*;
import be.hogent.team10.catan_businesslogic.model.tileBehaviour.TileBehaviour;
import be.hogent.team10.catan_businesslogic.model.simple.SimpleGame;
import be.hogent.team10.catan_businesslogic.model.exception.GameStateException;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import be.hogent.team10.catan_businesslogic.model.exception.ResourceException;
import be.hogent.team10.catan_businesslogic.model.exception.TradeException;
import be.hogent.team10.catan_businesslogic.model.gameState.GameState;
import be.hogent.team10.catan_businesslogic.model.gameState.InitializationState;
import be.hogent.team10.catan_businesslogic.model.gameState.RobberState;
import be.hogent.team10.catan_businesslogic.model.gameState.SetupState;
import be.hogent.team10.catan_businesslogic.util.Event;
import be.hogent.team10.catan_businesslogic.util.EventContainer;
import be.hogent.team10.catan_businesslogic.util.GameStateDeserializer;
import be.hogent.team10.catan_businesslogic.util.GsonMapper;
import be.hogent.team10.catan_businesslogic.util.PlayerTask;
import be.hogent.team10.catan_businesslogic.util.Task;
import be.hogent.team10.catan_businesslogic.util.TileBehaviourDeserializer;
import be.hogent.team10.catan_datalayer.repository.EventRepository;
import be.hogent.team10.catan_datalayer.repository.GameComponentRepository;
import be.hogent.team10.catan_datalayer.repository.GameDataRepository;
import be.hogent.team10.catan_datalayer.repository.GameRepository;
import be.hogent.team10.catan_datalayer.repository.PlayerRepository;
import be.hogent.team10.catan_datalayer.repository.TradeRepository;
import be.hogent.team10.exceptions.GeneralException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Joachim
 */
@Controller
@RequestMapping(value = "/games", produces = "application/json")
public class GameResource extends AbstractResource {

    private static final Logger slf4jLogger = LoggerFactory.getLogger(GameResource.class);
    private GameRepository gameRepository;
    private EventRepository eventRepository;
    private GameDataRepository gameDataRepository;
    private GameComponentRepository gameComponentRepository;
    private PlayerRepository playerRepository;
    private TradeRepository tradeRepository;

    @Autowired
    public GameResource(GameRepository gameRep) {
        this.gameRepository = gameRep;
    }

    @Autowired
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Autowired
    public void setGameDataRepository(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Autowired
    public void setGameComponentRepository(GameComponentRepository gameComponentRepository) {
        this.gameComponentRepository = gameComponentRepository;
    }

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Autowired
    public void setTradeRepository(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @RequestMapping(value = "/game/{id}", method = RequestMethod.GET)
    public @ResponseBody
    String getGame(@PathVariable("id") int id) {
        slf4jLogger.debug("Entering getGame(id={})", id);
        Gson gson = new GsonBuilder().create();
        Game game = gameRepository.getGameById(id);
        slf4jLogger.debug("Leaving getGame(): {}", gson.toJson(game));
        return gson.toJson(game);
    }

    @RequestMapping(value = "/game/all", method = RequestMethod.GET)
    public @ResponseBody
    String getAllGames() {
        slf4jLogger.debug("Entering getGames()");
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        List<Game> games = gameRepository.getAll();
        slf4jLogger.debug("Leaving getAllGames(): {}", gson.toJson(games));
        return gson.toJson(games);
    }

    @RequestMapping(value = {"/game/paged/page/{from}/{itemsPerPage}/sort/{column}", "/game/paged/page/{from}/{itemsPerPage}", "/game/paged/page/{from}"}, method = RequestMethod.GET)
    public @ResponseBody
    String getPagedGames(@PathVariable("from") int from, @PathVariable("itemsPerPage") int itemsPerPage, @PathVariable("column") int column) {
        slf4jLogger.debug("Entering getPagedGames(from={}, itemsPerPage={}, column={})", from, itemsPerPage, column);
        Gson gson = new GsonBuilder().create();
        List<SimpleGame> games = gameRepository.getPaged(from, itemsPerPage, column);
        slf4jLogger.debug("Leaving getPagedGames(): {}", gson.toJson(games));
        return gson.toJson(games);
    }

    @RequestMapping(value = "/game/{gameId}/poll/{playerId}/{gameStamp}", method = RequestMethod.GET)
    public @ResponseBody
    String poll(@PathVariable("gameId") int gameId, @PathVariable("playerId") int playerId, @PathVariable("gameStamp") int gameStamp) throws GeneralException {
        slf4jLogger.debug("Entering poll(gameId={}, playerId={}, gameStamp={})", gameId, playerId, gameStamp);
        Gson gson = new GsonBuilder().registerTypeAdapter(GameState.class,
                new GameStateDeserializer()).registerTypeAdapter(TileBehaviour.class, new TileBehaviourDeserializer()).enableComplexMapKeySerialization().create();
        try {
            EventContainer eventContainer = eventRepository.checkSync(gameId, gameStamp);
            List<Event> events = eventContainer.getAffectedTables();
            Game game;

            if (eventContainer.isRequiresFullSync()) {
                game = gameRepository.getFullSync(gameId, playerId, events);
                game.getGameState().updateWhatYouMayDo(eventRepository.getWhatYouAllreadyDidList(gameId, playerId), gameStamp);
            } else {
                game = gameRepository.getSelectiveSync(gameId, playerId, events);
            }
            if (game.getCurrentPlayer() != playerId) {
                game.getGameState().setInactive();
            } else {
                game.getGameState().updateWhatYouMayDo(eventRepository.getWhatYouAllreadyDidList(gameId, playerId), game.getTurnId());
            }
            if (eventContainer.getGameStamp() == 0) {
                game.setGameStamp(gameStamp);
            } else {
                game.setGameStamp(eventContainer.getGameStamp());
            }
            slf4jLogger.debug("Leaving poll(): {}", gson.toJson(game));
            return gson.toJson(game);
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
    }

    /**
     * todo : use gamestate to determine next player;
     *     
* @param gameId
     * @param playerId
     * @param inhoud
     * @return
     */
    @RequestMapping(value = "/game/{gameId}/endturn/{playerId}", method = RequestMethod.GET, headers = {"Accept=application/json"})
    public @ResponseBody
    String endTurn(@PathVariable("gameId") int gameId, @PathVariable("playerId") int playerId, @RequestBody String inhoud) throws GeneralException {
        slf4jLogger.debug("Entering endTurn(gameId={}, playerId={})", gameId, playerId);
        try {
            Game g = gameRepository.getFullGameSkeleton(gameId);
            if (g.getCurrentPlayer() != playerId) {
                return this.poll(gameId, playerId, g.getGameStamp());
            }

            g.setPlayers(gameDataRepository.getPlayers(gameId));

            for (int i = 0; i < g.getPlayers().size(); i++) {
                if (g.getPlayers().get(i).getId() == playerId) {
                    if ((i + 1) == g.getPlayers().size()) {
                        gameRepository.setNextPlayer(gameId, g.getPlayers().get(0).getId());
                    } else {
                        gameRepository.setNextPlayer(gameId, g.getPlayers().get(i + 1).getId());
                    }
                    break;
                }
            }
            slf4jLogger.info("Player({}) ends his turn in Game({})", playerId, gameId);
            slf4jLogger.debug("Leaving endTurn()");
            return this.poll(gameId, playerId, g.getGameStamp());
        } catch (SQLException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
    }

    @RequestMapping(value = "/game/{gameId}/start/{playerId}", method = RequestMethod.GET)
    public @ResponseBody
    String startGame(@PathVariable("gameId") int gameId, @PathVariable("playerId") int playerId, @RequestBody String content) throws GeneralException {
        slf4jLogger.debug("Entering startGame(gameId={}, playerId={})", gameId, playerId);
        try {
            Game g = gameRepository.getFullGameSkeleton(gameId);
            if (g.getCreator() != playerId || (!(g.getGameState() instanceof InitializationState))) {
                return this.poll(gameId, playerId, g.getGameStamp());
            }
            g.initialize();
            g.generateTiles();
            g.getBoard().placeRobber();
            g.generateDevelopmentCards();
            this.gameDataRepository.updateRobber(gameId, g.getBoard().getRobber());
            this.gameDataRepository.addCards(g.getDevelopmentCards(), gameId);
            this.gameComponentRepository.addTiles(g.getTiles(), gameId);
            g.setGameState(new SetupState(null));
            this.gameRepository.updateGameState(g);
            this.gameRepository.setNextPlayer(gameId, playerId);
            slf4jLogger.info("Player({}) starts the Game({})", playerId, gameId);
            slf4jLogger.debug("Leaving startGame()");
//return new Gson().toJson(gameRepository.getFullSync(gameId, playerId, null));
            return "";
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
    }

//TODO nog afwerken
    @RequestMapping(value = "/game/create", method = RequestMethod.POST, headers = {"Accept=application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    String createGame(@RequestBody String splayerId) throws IOException {
        int playerId = 0;
        slf4jLogger.debug("Entering createGame(playerId={})", splayerId);
        JsonFactory jfactory = new JsonFactory();
        JsonParser jParser = jfactory.createJsonParser(splayerId);
        while (jParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jParser.getCurrentName();
            if ("userid".equals(fieldname)) {
                jParser.nextToken();
                playerId = Integer.parseInt(jParser.getText());
            }
        }
        Game game = new Game();
        ResourceSet rs = new ResourceSet(20, 20, 20, 20, 20);
        game.setResources(rs);
        game.setCreator(playerId);
        Game g = gameRepository.addGame(game);
        gameDataRepository.addPlayer(g.getGameId(), playerId);
        slf4jLogger.info("Player({}) creates a Game({})", playerId, g.getGameId());
        slf4jLogger.debug("Leaving createGame(): {}", g.getGameId());
        return new Gson().toJson(g);
    }

    @RequestMapping(value = "/game/join", method = RequestMethod.POST, headers = {"Accept=application/json"})
    @ResponseBody
    public String joinGame(@RequestBody String gameIdPlayerId) throws GeneralException {
        try {
            slf4jLogger.debug("Entering joinGame(gameIdPlayerId={}, playerId={})", gameIdPlayerId);
            String playerId = null;
            String gameId = null;
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdPlayerId);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("userid".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = jParser.getText();
                } else if ("gameid".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = jParser.getText();
                }
            }
            gameDataRepository.addPlayer(Integer.parseInt(gameId), Integer.parseInt(playerId));
            slf4jLogger.info("Player({}) joines a Game({})", playerId, gameId);
            slf4jLogger.debug("Leaving joinGame()");
            return new Gson().toJson("");
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
    }

    @Deprecated
    @RequestMapping(value = "/game/add1", method = RequestMethod.GET)
    public @ResponseBody
    String addGame() {
        Gson gson = new GsonBuilder().create();
        Game g = new Game(185);
        return gson.toJson(gameRepository.addGame(g));
    }

//Nieuw methodes
    @RequestMapping(value = "/game/{gameId}/rollDice", method = RequestMethod.POST)
    @ResponseBody
    public String rollDice(@RequestBody String gameIdPlayerId) throws GeneralException {
//public String rollDice(@PathVariable("gameId") int gameId, @PathVariable("playerId") int playerId, @RequestBody String content) {
        slf4jLogger.debug("Entering rollDice(gameIdPlayerId={}", gameIdPlayerId);
        Game game = null;
        int gameId = 0;
        int dice = 0;
        try {
//disabled for browser-based testing.
            int playerId = 0;
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdPlayerId);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("playerId".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = Integer.parseInt(jParser.getText());
                } else if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                }
            }
            game = gameRepository.getFullGameSkeleton(gameId);
            dice = game.getDice().getValue();
            if (game.getCurrentPlayer() == playerId) {
                game.setPlayers(gameDataRepository.getAllPlayerData(gameId));
                game.setNodes(gameComponentRepository.getAllNodes(gameId));
                game.setTiles(gameComponentRepository.getAllTiles(gameId));

                /*
                 * Because we only load nodes and borders that are owned by a player we don't have data about the other
                 * nodes. Therefore we initialize a new board. This means we have a white board with unset tiles,
                 * borders and nodes with no owners whatsoever, but we have all nodes and borders.
                 */

                Board b = new Board();
                b.setNodes(game.getBoard().getNodes());
                b.setTiles(game.getBoard().getTiles());

                game.initialize();

                /*
                 * Since we want the board updated to the latest version we have in our database, we sync it. The sync
                 * method will not override the object itself, but it will override all properties that have changed
                 * with new data.
                 */
                game.getBoard().synchronize(b);
                game.synchronize(game);
                game.setGameInGameState();
                /*
                 * --
                 */
                /*
                 * Since we want the board updated to the latest version we have in our database, we sync it. The sync
                 * method will not override the object itself, but it will override all properties that have changed
                 * with new data.
                 */
                slf4jLogger.info("Player({}) rolls the Dice in Game({})", playerId, gameId);
                game.rollDice();
// so other players see it.
                gameDataRepository.updateDice(gameId, game.getDice().getValue());
// if number of eyes was 7 -> state is now robberState.
// therefore you must update it.
                if (game.getGameState() instanceof RobberState) {
                    gameRepository.updateGameState(game);
                }
// update game resources.
                gameDataRepository.updateResources(gameId, game.getResources());
// update each player's resourceset.
                for (Player p : game.getPlayers()) {
                    gameDataRepository.updatePlayerResources(gameId, p.getId(), p.getResources());
                }
                game.setGameState(null);
//game.setNodes(new ArrayList<Node>());
                slf4jLogger.debug("Leaving rollDice()");
//return new Gson().toJson(game);
            }
        } catch (GameStateException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        } catch (ResourceException ex) {
            if (game.getDice().getValue() != dice) {
                try {
                    gameDataRepository.updateDice(gameId, game.getDice().getValue());
                    throw new GeneralException(("The bank hasn't got enough resources for all of you."));
                } catch (SQLException ex1) {
                    throw new GeneralException("Database could not be reached.");
                }
            }
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        return new Gson().toJson("[Service] Dice Rolled");
    }

    @RequestMapping(value = "/game/{gameId}/buildSettlement", method = RequestMethod.POST)
    @ResponseBody
    public String buildSettlement(@RequestBody String gameIdPlayerIdCoordinate) throws GeneralException {
//public String buildSettlement(@PathVariable("gameId") int gameId, @PathVariable("playerId") int playerId, @PathVariable("x") int x, @PathVariable("y") int y, @RequestBody String content) {
        slf4jLogger.debug("Entering rollDicee(gameIdPlayerIdCoordinate={}", gameIdPlayerIdCoordinate);
        try {
            int playerId = 0;
            int gameId = 0;
            Coordinate coordinate = new Coordinate(0, 0);
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdPlayerIdCoordinate);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("playerId".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = Integer.parseInt(jParser.getText());
                } else if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                } else if ("X".equals(fieldname)) {
                    jParser.nextToken();
                    coordinate.setX(Integer.parseInt(jParser.getText()));
                } else if ("Y".equals(fieldname)) {
                    jParser.nextToken();
                    coordinate.setY(Integer.parseInt(jParser.getText()));
                }
            }

            Game game = gameRepository.getFullGameSkeleton(gameId);
            if (playerId == game.getCurrentPlayer()) {
                Player player = gameDataRepository.getPlayerData(gameId, playerId);
                game.setPlayers(Arrays.asList(new Player[]{player}));
                game.setGameInGameState();
                game.setBorders(gameComponentRepository.getAllBorders(gameId));
                game.setNodes(gameComponentRepository.getAllNodes(gameId));
                /*
                 * Le me being lazy. A list is converted to a map<,> in the set. I simply use them to make a new board.
                 */

                Board b = new Board();
                b.setBorders(game.getBoard().getBorders());
                b.setNodes(game.getBoard().getNodes());

                game.initialize();

                /*
                 * Since we want the board updated to the latest version we have in our database, we sync it. The sync
                 * method will not override the object itself, but it will override all properties that have changed
                 * with new data.
                 */
                game.getBoard().synchronize(b);
                game.synchronize(game);
//return new Gson().toJson(game);
/*
                 * Now we have the same data every other player should have on his screen. This allows us to try to add
                 * the new Settlement.
                 */
                /*
                 * let the domain do the dirty work. If an exception is thrown, no changes are written to the database.
                 *
                 * If everything goes well,
                 */
                slf4jLogger.info("Player({}) builds a Settlement in Game({}) at Coordinate({},{})", playerId, gameId, coordinate.getX(), coordinate.getY());
                game.buildSettlement(coordinate, player);
                /*
                 * the node was captured.
                 */
                gameComponentRepository.addNode(new Node(coordinate), gameId, player);
                /*
                 * I recieved resources.
                 */
                gameDataRepository.updateResources(gameId, game.getResources());
                /*
                 * the player paid resources.
                 */
                gameDataRepository.updatePlayerResources(gameId, playerId, player.getResources());
// todo : update player number of houses ect
// gameDataRepository.updatePlayerProperty(gameId, player);
            } else {
                slf4jLogger.debug("Leaving buildSettlement()");
                return new Gson().toJson("Not your turn" + game.getCurrentPlayer());
            }
//todo : return poll.

        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        slf4jLogger.debug("Leaving buildSettlement()");
        return new Gson().toJson("[Service] Build Settlement worked");
    }

    @RequestMapping(value = "/game/{gameId}/buildCity", method = RequestMethod.POST)
    @ResponseBody
    public void buildCity(@RequestBody String gameIdPlayerIdCoordinate) throws ObjectNotFoundException, GeneralException {
        slf4jLogger.debug("Entering buildCity(gameIdPlayerIdCoordinate={}", gameIdPlayerIdCoordinate);
        try {
            int playerId = 0;
            int gameId = 0;
            Coordinate coordinate = new Coordinate(0, 0);
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdPlayerIdCoordinate);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("playerId".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = Integer.parseInt(jParser.getText());
                } else if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                } else if ("X".equals(fieldname)) {
                    jParser.nextToken();
                    coordinate.setX(Integer.parseInt(jParser.getText()));
                } else if ("Y".equals(fieldname)) {
                    jParser.nextToken();
                    coordinate.setY(Integer.parseInt(jParser.getText()));
                }
            }

            Game game = gameRepository.getFullGameSkeleton(gameId);
            if (playerId == game.getCurrentPlayer()) {

                Player player = gameDataRepository.getPlayerData(gameId, playerId);
                game.setGameInGameState();
                game.setBorders(gameComponentRepository.getAllBorders(gameId));
                game.setNodes(gameComponentRepository.getAllNodes(gameId));
                /*
                 * Le me being lazy. A list is converted to a map<,> in the set. I simply use them to make a new board.
                 */
                Board b = new Board();
                b.setNodes(game.getBoard().getNodes());
                /*
                 * Because we only load nodes that are owned by a player we don't have data about the other nodes.
                 * Therefore we initialize a new board. This means we have a white board with unset tiles, borders and
                 * nodes with no owners whatsoever, but we have all nodes and borders.
                 */
                game.initialize();
                /*
                 * Since we want the board updated to the latest version we have in our database, we sync it. The sync
                 * method will not override the object itself, but it will override all properties that have changed
                 * with new data.
                 */
                game.getBoard().synchronize(b);
                /*
                 * Now we have the same data every other player should have on his screen. This allows us to try to add
                 * the new Settlement.
                 */

                /*
                 * let the domain do the dirty work. If an exception is thrown, no changes are written to the database.
                 *
                 * If everything goes well,
                 */
                slf4jLogger.info("Player({}) builds a City in Game({}) at Coordinate({},{})", playerId, gameId, coordinate.getX(), coordinate.getY());
                game.buildCity(coordinate, player);
                /*
                 * the city was built.
                 */
                gameComponentRepository.updateNode(new Node(coordinate), gameId, player);
                /*
                 * I recieved resources.
                 */
                gameDataRepository.updateResources(gameId, game.getResources());
                /*
                 * the player paid resources.
                 */
                gameDataRepository.updatePlayerResources(gameId, playerId, player.getResources());
//gameDataRepository.updatePlayerProperty(gameId, player);
            }
//todo : return poll
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        slf4jLogger.debug("Leaving buildCity()");
    }

    @RequestMapping(value = "/game/{gameId}/buildStreet", method = RequestMethod.POST)
    @ResponseBody
    public void buildStreet(@RequestBody String gameIdPlayerIdCoordinate) throws GeneralException {
        slf4jLogger.debug("Entering buildStreet(gameIdPlayerIdCoordinate={}", gameIdPlayerIdCoordinate);
        try {
            int playerId = 0;
            int gameId = 0;
            Coordinate coordinate = new Coordinate(0, 0);
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdPlayerIdCoordinate);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("playerId".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = Integer.parseInt(jParser.getText());
                } else if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                } else if ("X".equals(fieldname)) {
                    jParser.nextToken();
                    coordinate.setX(Integer.parseInt(jParser.getText()));
                } else if ("Y".equals(fieldname)) {
                    jParser.nextToken();
                    coordinate.setY(Integer.parseInt(jParser.getText()));
                }
            }
            Game game = gameRepository.getFullGameSkeleton(gameId);
            if (playerId == game.getCurrentPlayer()) {
                Player player = gameDataRepository.getPlayerData(gameId, playerId);
                game.setPlayers(gameDataRepository.getAllPlayerData(gameId));
                game.setGameInGameState();
                game.setBorders(gameComponentRepository.getAllBorders(gameId));
                game.setNodes(gameComponentRepository.getAllNodes(gameId));

                /*
                 * Le me being lazy. A list is converted to a map<,> in the set. I simply use them to make a new board.
                 */
                Board b = new Board();
                b.setBorders(game.getBoard().getBorders());
                b.setNodes(game.getBoard().getNodes());
                /*
                 * Because we only load nodes and borders that are owned by a player we don't have data about the other
                 * nodes. Therefore we initialize a new board. This means we have a white board with unset tiles,
                 * borders and nodes with no owners whatsoever, but we have all nodes and borders.
                 */
                game.initialize();
                /*
                 * Since we want the board updated to the latest version we have in our database, we sync it. The sync
                 * method will not override the object itself, but it will override all properties that have changed
                 * with new data.
                 */
                game.getBoard().synchronize(b);
                game.synchronize(game);
                player = game.getPlayer(player.getId());
                /*
                 * Now we have the same data every other player should have on his screen. This allows us to try to add
                 * the new Street.
                 */

                slf4jLogger.info("Player({}) builds a Street in Game({}) at Coordinate({},{})", playerId, gameId, coordinate.getX(), coordinate.getY());
                game.buildStreet(coordinate, player);

                /*
                 * let the domain do the dirty work. If an exception is thrown, no changes are written to the database.
                 *
                 * If everything goes well,
                 */
                gameComponentRepository.addBorder(new Border(coordinate), gameId, player);
                /*
                 * I recieved resources.
                 */
                gameDataRepository.updateResources(gameId, game.getResources());
                /*
                 * the player paid resources.
                 */
                gameDataRepository.updatePlayerResources(gameId, playerId, player.getResources());
                if (playerId != game.getCurrentPlayer() || game.getTurnId() != -1) {
                    gameRepository.setNextPlayer(game.getGameId(), game.getCurrentPlayer());
                }
                gameRepository.updateGameState(game);
            }
//todo : return poll
        } catch (ObjectNotFoundException ex) {
            ex.printStackTrace();
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        slf4jLogger.debug("Leaving buildStreet()");
    }

    @RequestMapping(value = "/game/{gameId}/buyDevelopmentCard", method = RequestMethod.POST)
    @ResponseBody
    public void buyDevelopmentCard(@RequestBody String gameIdPlayerId) throws GeneralException, IOException {
        slf4jLogger.debug("Entering buyDevelopmentCard(gameIdPlayerId={}", gameIdPlayerId);
        try {
            int playerId = 0;
            int gameId = 0;
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdPlayerId);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("playerId".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = Integer.parseInt(jParser.getText());
                } else if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                }
            }

            Game game = gameRepository.getFullGameSkeleton(gameId);
            if (playerId == game.getCurrentPlayer()) {
                Player player = gameDataRepository.getPlayerData(gameId, playerId);
                game.setGameInGameState();
                game.setDevelopmentCards(gameDataRepository.getCards(gameId));
                game.initialize();

                game.synchronize(game);

                DevelopmentCard card = game.buyDevelopmentCard(player);

                gameDataRepository.buyCard(playerId, gameId, card.getId());
                gameDataRepository.updatePlayerResources(gameId, playerId, player.getResources());
                gameDataRepository.updateResources(gameId, game.getResources());
            } else {
                throw new GameStateException("Je bent niet aan de beurt.");
            }
        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        slf4jLogger.debug("Leaving buyDevelopmentCard()");
    }

    //@TODO: 
    @RequestMapping(value = "/game/{gameId}/playDevelopmentCard/{type}", method = RequestMethod.POST)
    @ResponseBody
    public void playDevelopmentCard(@RequestBody String gameIdPlayerIdType) throws GeneralException {
        slf4jLogger.debug("Entering playDevelopmentCard(gamePlayerIdType={}", gameIdPlayerIdType);
        try {
            int playerId = 0;
            int gameId = 0;
            int type = 0;
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdPlayerIdType);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("playerId".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = Integer.parseInt(jParser.getText());
                } else if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                } else if ("type".equals(fieldname)) {
                    jParser.nextToken();
                    type = Integer.parseInt(jParser.getText());
                }
            }

            Game game = gameRepository.getFullGameSkeleton(gameId);
            if (playerId == game.getCurrentPlayer()) {
                Player player = gameDataRepository.getPlayerData(gameId, playerId);

// We set all the players to calculate their knightforce
                game.setPlayers(gameDataRepository.getAllPlayerData(gameId));
// Figure out which type of card we are playing
                String typeString = "";
                if (type == 0) {
                    typeString = "KNIGHT";
                } else {
                    typeString = "UNIVERSITY";
                }
// Get the first card of this type in the db, it will only return a card,
// if their is a card that isnt played and isnt bought in the same turn
                List<DevelopmentCard> cards = gameDataRepository.getCard(gameId, playerId, typeString);
                DevelopmentCard card = null;

                if (!cards.isEmpty()) {
                    card = cards.get(0);
                }

                game.setGameInGameState();

                game.initialize();

                game.synchronize(game);

// Use this particular card
                game.useDevelopmentCard(card, player);

// GameState can be updated now if we are playing the knight card
                gameRepository.updateGameState(game);

// And finaly we update the card in the database
                gameDataRepository.updateDevelopmentCard(gameId, card.getId());
                gameDataRepository.updatePlayerResources(gameId, playerId, player.getResources());
            }
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        slf4jLogger.debug("Leaving playDevelopmentCard()");

    }

    @RequestMapping(value = "/game/{gameId}/moveRobber", method = RequestMethod.POST)
    @ResponseBody
    public void moveRobber(@RequestBody String gameIdPlayerIdCoordinate) throws GeneralException {
        slf4jLogger.debug("Entering moveRobber(gameIdPlayerIdCoordinate={}", gameIdPlayerIdCoordinate);
        try {
            int playerId = 0;
            int gameId = 0;
            Coordinate coordinate = new Coordinate(0, 0);
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdPlayerIdCoordinate);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("playerId".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = Integer.parseInt(jParser.getText());
                } else if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                } else if ("X".equals(fieldname)) {
                    jParser.nextToken();
                    coordinate.setX(Integer.parseInt(jParser.getText()));
                } else if ("Y".equals(fieldname)) {
                    jParser.nextToken();
                    coordinate.setY(Integer.parseInt(jParser.getText()));
                }
            }

            Game game = gameRepository.getFullGameSkeleton(gameId);

            if (playerId == game.getCurrentPlayer()) {
                game.setPlayers(gameDataRepository.getAllPlayerData(gameId));
                game.setNodes(gameComponentRepository.getAllNodes(gameId));
                game.setTiles(gameComponentRepository.getAllTiles(gameId));

                /*
                 * Because we only load nodes and borders that are owned by a player we don't have data about the other
                 * nodes. Therefore we initialize a new board. This means we have a white board with unset tiles,
                 * borders and nodes with no owners whatsoever, but we have all nodes and borders.
                 */

                Board b = new Board();
                b.setNodes(game.getBoard().getNodes());
                b.setTiles(game.getBoard().getTiles());

                game.initialize();

                /*
                 * Since we want the board updated to the latest version we have in our database, we sync it. The sync
                 * method will not override the object itself, but it will override all properties that have changed
                 * with new data.
                 */
                game.getBoard().synchronize(b);
                game.synchronize(game);
                game.setGameInGameState();

                slf4jLogger.info("Player({}) moves the Robber in Game({}) to Coordinate({},{})", playerId, gameId, coordinate.getX(), coordinate.getY());
                int dicevalue = this.gameDataRepository.getDice(gameId);
                game.moveRobber(coordinate, dicevalue);
                gameDataRepository.updateRobber(gameId, coordinate);
                gameDataRepository.updateResources(gameId, game.getResources());
                for (Player p : game.getPlayers()) {
                    gameDataRepository.updatePlayerResources(gameId, p.getId(), p.getResources());
                }
                gameRepository.updateGameState(game);
            }

        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        slf4jLogger.debug("Leaving moveRobber()");
    }

    @RequestMapping(value = "/game/{gameId}/trade", method = RequestMethod.POST)
    @ResponseBody
    public void trade(@RequestBody String tradeRequest) throws GeneralException {
        slf4jLogger.debug("Entering trade(tradeRequest={}", tradeRequest);
        try {
            TradeRequest request = new GsonMapper<TradeRequest>().fromJson(tradeRequest, TradeRequest.class);
            Game game = gameRepository.getFullGameSkeleton(request.getGameId());
            Player player = gameDataRepository.getPlayerData(request.getGameId(), request.getPlayerSending());
            game.setPlayers(Arrays.asList(new Player[]{player}));
            if (request.getPlayerReceiving() == 0) {
                slf4jLogger.info("Player({}) trades with the Bank in Game({}) at Coordinate({},{})", player.getId(), game.getGameId());
                game.tradeWithBank(request);
                gameDataRepository.updateResources(game.getGameId(), game.getResources());
                gameDataRepository.updatePlayerResources(game.getGameId(), player.getId(), player.getResources());
            } else {
                game.tradeWithPlayer(request);
                tradeRepository.addTradeRequest(request);
            }
        } catch (TradeException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        slf4jLogger.debug("Leaving trade()");
    }

// Todo
    @RequestMapping(value = "/game/{gameId}/replyTrade", method = RequestMethod.POST)
    @ResponseBody
    public void replyTrade(@RequestBody String tradeRequestIdbooleanAccepted) throws GeneralException, IOException {
        slf4jLogger.debug("Entering replyTrade(playerIdtradeRequestIdAccepted={}", tradeRequestIdbooleanAccepted);
        TradeRequest tr = null;
        try {
            int playerId = 0;
            int gameId = 0;
            int tradeRequestId = 0;
            boolean accepted = false;
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(tradeRequestIdbooleanAccepted);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("playerId".equals(fieldname)) {
                    jParser.nextToken();
                    playerId = Integer.parseInt(jParser.getText());
                } else if ("tradeRequestId".equals(fieldname)) {
                    jParser.nextToken();
                    tradeRequestId = Integer.parseInt(jParser.getText());
                } else if ("accepted".equals(fieldname)) {
                    jParser.nextToken();
                    accepted = jParser.getText().equalsIgnoreCase("true");
                } else if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                }
            }
            List<TradeRequest> trList = tradeRepository.get(tradeRequestId);

            tr = trList.get(0);

            Game game = gameRepository.getFullGameSkeleton(gameId);
            Player receiver = gameDataRepository.getPlayerData(gameId, tr.getPlayerReceiving());
            if (accepted && playerId == receiver.getId()) {

                Player sender = gameDataRepository.getPlayerData(gameId, tr.getPlayerSending());

                game.setPlayers(Arrays.asList(new Player[]{receiver, sender}));
                game.setGameInGameState();
                game.initialize();

                game.synchronize(game);

                game.acceptTrade(tr);

// update both players their resources
                gameDataRepository.updatePlayerResources(gameId, receiver.getId(), receiver.getResources());
                gameDataRepository.updatePlayerResources(gameId, sender.getId(), sender.getResources());
// update the traderequest
                tradeRepository.updateTradeRequest(tr);
            } else {
// REJECTED
                tr.setStatus(TradeRequestStatus.R);
                tradeRepository.updateTradeRequest(tr);
            }
        } catch (ObjectNotFoundException ex) {
            if (tr != null) {
                tr.setStatus(TradeRequestStatus.R);
                tradeRepository.updateTradeRequest(tr);
            }
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        } catch (Exception ex) {
            if (tr != null) {
                tr.setStatus(TradeRequestStatus.R);
                tradeRepository.updateTradeRequest(tr);
            }
            slf4jLogger.error("", ex);
            throw new GeneralException(ex.getMessage());
        }
        slf4jLogger.debug("Leaving replyTrade()");
    }

    @RequestMapping(value = "/game/{gameId}/steal", method = RequestMethod.POST)
    @ResponseBody
    public void steal(@RequestBody String gameIdRobberIdVictimId) throws Exception {
        slf4jLogger.debug("Entering steal(gameIdRobberIdVictimId={}", gameIdRobberIdVictimId);
        try {
            int gameId = 0, robberId = 0, victimId = 0;
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(gameIdRobberIdVictimId);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("gameId".equals(fieldname)) {
                    jParser.nextToken();
                    gameId = Integer.parseInt(jParser.getText());
                } else if ("robberId".equals(fieldname)) {
                    jParser.nextToken();
                    robberId = Integer.parseInt(jParser.getText());
                } else if ("victimId".equals(fieldname)) {
                    jParser.nextToken();
                    victimId = Integer.parseInt(jParser.getText());
                }
            }

            Game game = gameRepository.getFullGameSkeleton(gameId);

            if (robberId == game.getCurrentPlayer()) {
                game.setPlayers(gameDataRepository.getAllPlayerData(gameId));
                Player robber = game.getPlayer(robberId);
                Player victim = game.getPlayer(victimId);
                game.setGameInGameState();
                game.setBorders(gameComponentRepository.getAllBorders(gameId));
                game.setNodes(gameComponentRepository.getAllNodes(gameId));

                Board b = new Board();
                b.setBorders(game.getBoard().getBorders());
                b.setNodes(game.getBoard().getNodes());

                game.initialize();

                game.getBoard().synchronize(b);
                game.synchronize(game);
                slf4jLogger.info("Player({})steals Resource from Victim({}) in Game({})", robberId, victimId, gameId);

// After decreasing all players' resources, the current player can steal a resource from an opponent
                game.stealResource(robber, victim);

// Every action happend, now we have to update everything inside the database
                gameDataRepository.updateResources(gameId, game.getResources());
                for (Player p : game.getPlayers()) {
                    this.gameDataRepository.updatePlayerResources(game.getGameId(), p.getId(), p.getResources());
                }

            }
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new Exception(ex);
        }
        slf4jLogger.debug("Leaving steal()");
    }
}
