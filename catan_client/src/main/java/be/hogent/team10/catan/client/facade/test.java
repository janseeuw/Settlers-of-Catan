package be.hogent.team10.catan.client.facade;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class test {
    // http://localhost:8080/RESTfulExample/json/product/post

    public static void main(String[] args) {
        try {
            HttpJsonAdaptor adaptor = new HttpJsonAdaptor();
            
            Map<String,String> data = new HashMap<String, String>();
            data.put("username", "demoplayer1");
            data.put("password", "paswoord");
            //System.out.println(adaptor.post("/players/player/login", data));
            
            System.out.println(adaptor.get("/games/game/all"));
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}