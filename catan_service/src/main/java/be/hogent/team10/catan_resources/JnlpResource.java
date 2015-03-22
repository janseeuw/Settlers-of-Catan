/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_resources;

import be.hogent.team10.catan_businesslogic.model.Game;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Joachim
 */
@Controller
@RequestMapping(value = "/jnlp", produces = "application/x-java-jnlp-file")
public class JnlpResource {

    /**
     * This method is responsible for dynamically generating the jnlp file whit the id's that are needed
     * to start up the game for the player.
     * @param gameId
     * @param playerToken
     * @param userId
     * @return A jnlp file that is used for the java webstart.
     */
    @RequestMapping(value = "/create/{gameId}/{playerToken}/{userId}", method = RequestMethod.GET)
    public @ResponseBody
    String createJnlp(@PathVariable("gameId") int gameId, @PathVariable("playerToken") int playerToken, @PathVariable("userId") int userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<jnlp spec=\"1.0+\" codebase=\"http://stable.team10.vop.tiwi.be\" >");
        sb.append("<information>\n"
                + "<title>Settlers Of Catan</title>\n"
                + "<vendor>Hogent Team 10</vendor>\n"
                + "</information>\n"
                + "<security>\n"
                + "      <all-permissions/>\n"
                + "   </security>"
                + "<resources>\n"
                + "<!-- Application Resources -->\n"
                + "<j2se version=\"1.6+\" href=\"http://java.sun.com/products/autodl/j2se\"/>\n"
                + "<jar href=\"catan_client-1.0-SNAPSHOT-jar-with-dependencies.jar\" main=\"true\" />\n"
                + "</resources>\n"
                + "<application-desc name=\"Settlers of catan\" main-class=\"startup.StartUp\" width=\"800\" height=\"600\">\n");
        sb.append("<argument>").append(gameId).append("</argument>\n");
        sb.append("<argument>").append(playerToken).append("</argument>\n");
        sb.append("<argument>").append(userId).append("</argument>\n");
        sb.append("</application-desc>\n"
                + "<update check=\"background\"/>\n"
                + "</jnlp>");
        return sb.toString();
    }
}
