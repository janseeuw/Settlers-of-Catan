/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_resources;

import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.exception.ObjectNotFoundException;
import be.hogent.team10.catan_datalayer.repository.PlayerRepository;
import be.hogent.team10.exceptions.GeneralException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * REST Web Service
 *
 * @author Joachim
 */
@Controller
@RequestMapping(value = "/players", produces = "application/json")
public class PlayerResource extends AbstractResource {

    private static final Logger slf4jLogger = LoggerFactory.getLogger(PlayerResource.class);
    private PlayerRepository playerRepository;
    private Map<String, Integer> userTokenMap;
    private Map<String, String> recoverTokenMap;
    private JavaMailSender mailSender;
    @Autowired
    VelocityEngine velocityEngine;

    /**
     * Constructor method for playerresource where the playerRepository is injected.
     *
     * @param playerRep
     */
    @Autowired
    public PlayerResource(PlayerRepository playerRep) {
        userTokenMap = new HashMap<String, Integer>();
        recoverTokenMap = new HashMap<String, String>();
        this.playerRepository = playerRep;
    }

    /**
     *
     * @param mailSender
     */
    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * This method is used to send the registration mail to the user.
     *
     * @param to
     * @param userToken
     * @throws GeneralException
     */
    public void sendSimpleEmail(String to, String userToken) throws GeneralException {
        slf4jLogger.debug("Entering sendSimpleEmail(to={}, userToken={}", to, userToken);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            Map<String, String> model = new HashMap<String, String>();
            model.put("userToken", userToken);
            String emailText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "activateEmailTemplate.vm", model);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("no-reply@catan.com");
            helper.setTo(to);
            helper.setSubject("Registration mail from catan online");
            helper.setText(emailText, true);
            ClassPathResource image = new ClassPathResource("catanLogo.png");
            helper.addInline("catanLogo", image);
            mailSender.send(message);
        } catch (MessagingException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
        slf4jLogger.debug("Leaving sendSimpleEmail()");
    }

    /**
     * This method is used to send the recovery mail to set a new password.
     *
     * @param to
     * @param recoverToken
     * @throws GeneralException
     */
    public void sendRecoverEmail(String to, String recoverToken) throws GeneralException {
        slf4jLogger.debug("Entering sendRecoverEmail(to={}, recoverToken={}", to, recoverToken);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            Map<String, String> model = new HashMap<String, String>();
            model.put("recoverToken", recoverToken);
            String emailText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "recoverEmailTemplate.vm", model);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("no-reply@catan.com");
            helper.setTo(to);
            helper.setSubject("Reset password catan online");
            helper.setText(emailText, true);
            ClassPathResource image = new ClassPathResource("catanLogo.png");
            helper.addInline("catanLogo", image);
            mailSender.send(message);
        } catch (MessagingException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
        slf4jLogger.debug("Leaving sendRecoverEmail()");
    }

    /**
     * This method retrieves a player from the database and returns it as a Gson object.
     *
     * @param id
     * @return A Gson object that represents a player.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/{id}", method = RequestMethod.GET)
    public @ResponseBody
    String getPlayer(@PathVariable("id") int id) throws GeneralException {
        slf4jLogger.debug("Entering getPlayer(id={}", id);
        Gson gson = new GsonBuilder().create();
        try {
            Player player = playerRepository.get(id);
            slf4jLogger.debug("Leaving getPlayer()");
            return gson.toJson(player);
        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("objectNotFoundException");
        }
    }

    /**
     * This method returns all the players.
     *
     * @return List of player objects.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/all", method = RequestMethod.GET)
    public @ResponseBody
    String getAllPlayers() throws GeneralException {
        slf4jLogger.debug("Entering getAllPlayers()");
        Gson gson = new GsonBuilder().create();
        List<Player> players = playerRepository.getAll();
        slf4jLogger.debug("Leaving getAllPlayers()");
        return gson.toJson(players);
    }

    /**
     * This method is used to log in a player.
     *
     * @param loginString
     * @return The player that has logged in.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/login", method = RequestMethod.POST, headers = {"Accept=application/json"})
    public @ResponseBody
    String loginPlayer(@RequestBody String loginString) throws GeneralException {
        slf4jLogger.debug("Entering loginPlayer(loginString={}", loginString);
        String name = null, password = null;
        try {
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(loginString);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("username".equals(fieldname)) {
                    jParser.nextToken();
                    name = jParser.getText();
                }
                if ("password".equals(fieldname)) {
                    jParser.nextToken();
                    password = jParser.getText();
                }
            }
            jParser.close();
            Player playerFromDb = playerRepository.login(name, password);
            if (Character.toUpperCase(playerFromDb.getStatus()) != 'Y') {
                throw new GeneralException("playerNotActiveException");
            }
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            slf4jLogger.info("Player({}) logs in", playerFromDb.getName());
            slf4jLogger.debug("Leaving loginPlayer()");
            return gson.toJson(playerFromDb);
        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("objectNotFoundException");
        } catch (JsonParseException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        } catch (IOException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
    }

    /**
     * This method is used to log in a player using facebook
     *
     * @param accesstoken
     * @return The player that has logged in.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/login/facebook", method = RequestMethod.POST, headers = {"Accept=application/json"})
    public @ResponseBody
    String loginPlayerFacebook(@RequestBody String accesstoken) throws GeneralException {
        slf4jLogger.debug("Entering loginPlayerFacebook(accesstoken={}", accesstoken);
        Player player = null;
        try {
            String token = null;
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(accesstoken);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("accesstoken".equals(fieldname)) {
                    jParser.nextToken();
                    token = jParser.getText();
                }
            }
            Facebook fb = new FacebookTemplate(token);

            int id = Integer.parseInt(fb.userOperations().getUserProfile().getId());
            player = playerRepository.getFacebook(id);
            if (player == null) {
                player = new Player();
                player.setName(fb.userOperations().getUserProfile().getUsername());
                if (fb.userOperations().getUserProfile().getEmail() != null) {
                    player.setEmail(fb.userOperations().getUserProfile().getEmail());
                } else {
                    player.setEmail("no-email@catan.com");
                }
                player.setAvatar(uploadAvatar(fb.userOperations().getUserProfileImage(), id + ""));
                player.setPassword(userToken().substring(0, 20));
                player.setFacebook(id);
                player = playerRepository.createPlayer(player);
                playerRepository.activatePlayer(player);
            }
        } catch (IOException ex) {
        } catch (ObjectNotFoundException ex) {
        } catch (DuplicateKeyException ex) {
            java.util.logging.Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLIntegrityConstraintViolationException ex) {
            java.util.logging.Logger.getLogger(PlayerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Gson().toJson(player);
    }

    /**
     * This method is used to create a player and returns the created player and sends an email to the player to
     * activate his account.
     *
     * @param player
     * @return A Gson object.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/create", method = RequestMethod.POST, headers = {"Accept=application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    String createPlayer(@RequestBody String player) throws GeneralException {
        slf4jLogger.debug("Entering createPlayer(player={}", player);
        String name = null;
        String password = null;
        String email = null;
        String avatar = null;
        try {
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(player);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("username".equals(fieldname)) {
                    jParser.nextToken();
                    name = jParser.getText();
                } else if ("password".equals(fieldname)) {
                    jParser.nextToken();
                    password = jParser.getText();
                } else if ("email".equals(fieldname)) {
                    jParser.nextToken();
                    email = jParser.getText();
                } else if ("avatar".equals(fieldname)) {
                    jParser.nextToken();
                    avatar = jParser.getText();
                }
            }
            Player newPlayer = new Player(name, email, password);
            byte[] image = convertEncodedImageToByteArray(avatar);
            newPlayer.setAvatar(uploadAvatar(image, name));
            Player createdPlayer = playerRepository.createPlayer(newPlayer);
            sendMail(name);
            slf4jLogger.info("A new Player({}) is registered", createdPlayer.getName());
            slf4jLogger.debug("Leaving createPlayer()");
            return new Gson().toJson(createdPlayer);
        } catch (SQLIntegrityConstraintViolationException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("duplicateUsername");
        } catch (DuplicateKeyException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("duplicateUsername");
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
    }

    /**
     * This is the method responsible for activating the player.
     *
     * @param userToken
     * @return A json message with the answer whether the activation was succesfull.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/activate/{userToken}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String activatePlayer(@PathVariable("userToken") String userToken) throws GeneralException {
        slf4jLogger.debug("Entering activatePlayer(userToken={}", userToken);
        try {
            int id = userTokenMap.get(userToken);
            Player player = playerRepository.get(id);
            playerRepository.activatePlayer(player);
            userTokenMap.remove(userToken);
            slf4jLogger.info("Player({}) is activated", player.getName());
        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("objectNotFoundException");
        }
        slf4jLogger.debug("Leaving activatePlayer()");
        return new Gson().toJson("ok");
    }

    /**
     * This method is used to log in a player.
     *
     * @param disable
     * @return The player that has logged in.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/disable", method = RequestMethod.POST, headers = {"Accept=application/json"})
    public @ResponseBody
    String disablePlayer(@RequestBody String disable) throws GeneralException {
        slf4jLogger.debug("Entering disablePlayer(disable={}", disable);
        int id = 0;
        try {
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(disable);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("userid".equals(fieldname)) {
                    jParser.nextToken();
                    id = Integer.parseInt(jParser.getText());
                }
            }
            jParser.close();
            Player playerFromDb = playerRepository.get(id);
            playerRepository.disablePlayer(playerFromDb);
            slf4jLogger.info("Player({}) is disabled", playerFromDb.getName());
            slf4jLogger.debug("Leaving disablePlayer()");
            return new Gson().toJson("ok");
        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("objectNotFoundException");
        } catch (JsonParseException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        } catch (IOException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
    }

    /**
     * This method is used for calling the method to send the recovery mail.
     *
     * @param userName
     * @return A json object with a message whether or not the method was succesfull.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/recover", method = RequestMethod.POST)
    @ResponseBody
    public String recoverPlayerPassword(@RequestBody String userName) throws GeneralException {
        slf4jLogger.debug("Entering recoverPlayerPassword(userName={}", userName);
        try {
            String name = null;
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(userName);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("username".equals(fieldname)) {
                    jParser.nextToken();
                    name = jParser.getText();
                }
            }
            String recoverToken = userToken();
            //Speler die zijn paswoord opnieuw wilt instellen bijhouden.
            recoverTokenMap.put(recoverToken, name);
            Player player = playerRepository.get(name);
            sendRecoverEmail(player.getEmail(), recoverToken);
            slf4jLogger.info("A password recovery mail is sent to Player({})", player.getName());
        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("objectNotFoundException");
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
        slf4jLogger.debug("Leaving recoverPlayerPassword()");
        return new Gson().toJson("ok");
    }

    /**
     * This is the actual method that is used to renew the password of a user.
     *
     * @param recoverToken
     * @param pass
     * @return A json object with a message whether or not the method was succesfull.
     * @throws GeneralException
     */
    @RequestMapping(value = "/player/recover/{recoverToken}", method = RequestMethod.POST)
    @ResponseBody
    public String recoverPassword(@PathVariable("recoverToken") String recoverToken, @RequestBody String pass) throws GeneralException {
        slf4jLogger.debug("Entering recoverPassword(recoverToken={}", recoverToken);
        try {
            //Username voor token ophalen om te weten over welke speler het gaat.
            String userName = recoverTokenMap.get(recoverToken);
            String password = null;
            //Speler ophalen adhv zijn username.
            Player player = playerRepository.get(userName);
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createJsonParser(pass);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if ("password".equals(fieldname)) {
                    jParser.nextToken();
                    password = jParser.getText();
                }
            }
            //Nieuw paswoord instellen.
            player.setPassword(password);
            playerRepository.forceUpdatePassword(player);
            recoverTokenMap.remove(recoverToken);
            slf4jLogger.info("Player({}) his password has been reset", player.getName());
        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("objectNotFoundException");
        } catch (Exception ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
        slf4jLogger.debug("Leaving recoverPassword()");
        return new Gson().toJson("ok");
    }

    private String userToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(150, random).toString(32);
    }

    private void sendMail(String userName) throws GeneralException {
        slf4jLogger.debug("Entering sendMail(userName={}", userName);
        try {
            String userToken = userToken();
            Player player = playerRepository.get(userName);
            userTokenMap.put(userToken, player.getId());
            sendSimpleEmail(player.getEmail(), userToken);
            slf4jLogger.debug("Leaving sendMail()");
        } catch (ObjectNotFoundException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("objectNotFoundException");
        }
    }

    private String uploadAvatar(byte[] image, String name) throws GeneralException {
        slf4jLogger.debug("Entering uploadAvatar(encodedImage={}, name={}", image, name);
        try {
            Sardine sardine = SardineFactory.begin("team10", "Dc56zYzV");
            String imageString = "http://dav.static.vop.tiwi.be/team10/Avatars/" + name + ".png";
            sardine.put(imageString, image);
            slf4jLogger.debug("Leaving uploadAvatar(): {}", imageString);
            return imageString;
        } catch (IOException ex) {
            slf4jLogger.error("", ex);
            throw new GeneralException("unexpectedException");
        }
    }

    private byte[] convertEncodedImageToByteArray(String encodedImage) {
        return DatatypeConverter.parseBase64Binary(encodedImage);
    }
}
