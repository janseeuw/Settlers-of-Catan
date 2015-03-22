package be.hogent.team10.catan_businesslogic.util;

import be.hogent.team10.catan_businesslogic.model.Coordinate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 *
 * @author HP
 */
public class MapDeserializer<T, U> implements JsonDeserializer<Map<T, U>> {

    private static final String CLASS_META_KEY = "CLASS_META_KEY";

    @Override
    public Map<T, U> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (!json.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = json.getAsJsonObject();
        Set<Entry<String, JsonElement>> jsonEntrySet = jsonObject.entrySet();


        Map<T, U> deserializedMap = new HashMap<T, U>();

        for (Entry<java.lang.String, JsonElement> entry : jsonEntrySet) {
            try {
                JsonObject jsonObj = entry.getValue().getAsJsonObject();
                String className = jsonObj.get(CLASS_META_KEY).getAsString();
                Class<?> clz = Class.forName(className);
                U value = context.deserialize(entry.getValue(), clz);
                String trimmed[] = entry.getKey().replaceAll("(", "").replace(")", "").split(",");
                Coordinate c = new Coordinate(Integer.parseInt(trimmed[0]), Integer.parseInt(trimmed[1]));
                deserializedMap.put((T) c, value);
            } catch (Exception ex) {
            }
        }

        return deserializedMap;
    }
    /*  {
     JsonElement jsonEle = context.serialize(object, object.getClass());
     jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY,
     object.getClass().getCanonicalName());
     return jsonEle;
     }*/
}
