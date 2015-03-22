package be.hogent.team10.catan_businesslogic.util;

import be.hogent.team10.catan_businesslogic.model.Game;
import com.google.gson.Gson;


/**
 *
 * @author HP
 */
public class JsonGameDeSerializer {
    
    public Game deSerialize(String json){
        Gson gson = new Gson();
        Game g = gson.fromJson(json, Game.class);
        return g;
    }
}
