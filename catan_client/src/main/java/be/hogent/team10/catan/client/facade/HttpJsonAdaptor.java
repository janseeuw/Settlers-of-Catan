package be.hogent.team10.catan.client.facade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author HP
 */
public class HttpJsonAdaptor {

   private String restServiceRoot = "http://stable.team10.vop.tiwi.be/catan/games";
   //private String restServiceRoot = "http://localhost:8084/catan_service/catan/games";

    public String get(String paramString) throws MalformedURLException, IOException {
        URL url = new URL(restServiceRoot + paramString);
        /*
         * setup
         */
        HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));

        String response = "";
        String output = "";
        while ((output = in.readLine()) != null) {
            response += output;
        }

        in.close();
        urlconnection.disconnect();

        return response;
    }

    public String post(String paramString, String json) throws MalformedURLException, IOException, Exception {
        URL url = new URL(restServiceRoot + paramString);
        /*
         * setup
         */
        HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
        urlconnection.setDoOutput(true);
        urlconnection.setRequestMethod("POST");
        urlconnection.setRequestProperty("Content-Type", "application/json");
        OutputStream os = urlconnection.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        BufferedReader in;
        System.out.println(urlconnection.getResponseCode());
        if (urlconnection.getResponseCode() == HttpURLConnection.HTTP_CONFLICT) {
            for(Entry<String, List<String> > data : urlconnection.getHeaderFields().entrySet()){
                System.out.println(data.getKey());
                for(String s : data.getValue())
                    System.out.println("-- "+ s);
            }
            throw new Exception(urlconnection.getHeaderField("ERROR_MESSAGE"));
        } /*
         * read answer
         */ else {
            in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
        }
        String response = "";
        String output = "";
        while ((output = in.readLine()) != null) {
            response += output;
        }
        /*
         * close what remained opened
         */
        in.close();
        os.close();
        urlconnection.disconnect();



        /*
         * return
         */
        return response;

    }

    public String post(String url, Map<String, String> data) throws MalformedURLException, IOException, Exception {
        /*
         * set data
         */
        String input = "{";
        if (data != null) {
            for (Entry<String, String> entry : data.entrySet()) {
                input += "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",";
            }
        }
        // to avoid problems with strange strings (f.e. usernames contining , or }
        input += "}}--}";
        input = input.replace(",}}--}", "}");
        /*
         * post
         */
        return post(url, input);
    }
}
