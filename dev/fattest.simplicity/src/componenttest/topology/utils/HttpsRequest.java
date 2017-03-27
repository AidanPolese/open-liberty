package componenttest.topology.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import componenttest.topology.impl.LibertyServer;

public class HttpsRequest {

    private final String url;
    private int expectedResponseCode = HttpsURLConnection.HTTP_OK;
    private String reqMethod = "GET";
    private String json = null;
    private String basicAuth = null;
    private final Map<String, String> props = new HashMap<String, String>();

    public HttpsRequest(String url) {
        this.url = url;
    }

    public HttpsRequest(LibertyServer server, String... pathParts) {
        String base = "https://" + server.getHostname() + ":" + server.getHttpDefaultSecurePort();
        for (String part : pathParts)
            base += part;
        this.url = base;
    }

    /**
     * The HTTP request method. Default method is GET.
     */
    public HttpsRequest method(String method) {
        this.reqMethod = method;
        return this;
    }

    /**
     * Add a HTTP request property name and value using HttpUrlConnection.setRequestProperty()
     */
    public HttpsRequest requestProp(String key, String value) {
        props.put(key, value);
        return this;
    }

    /**
     * Set the expected response code. Default is HTTP_OK
     */
    public HttpsRequest expectCode(int expectedResponse) {
        this.expectedResponseCode = expectedResponse;
        return this;
    }

    /**
     * Set the json data to send with the request.
     */
    public HttpsRequest jsonBody(String json) {
        this.json = json;
        return this;
    }

    public HttpsRequest basicAuth(String user, String pass) {
        try {
            String userPass = user + ':' + pass;
            String base64Auth = javax.xml.bind.DatatypeConverter.printBase64Binary((userPass).getBytes("UTF-8"));
            this.basicAuth = "Basic " + base64Auth;
        } catch (UnsupportedEncodingException e) {
            // nothing to be done
        }
        return this;
    }

    /**
     * Make an HTTPS request and receive the response as a JSON object.
     */
    public String run() throws Exception {
        System.out.println(reqMethod + ' ' + url);

        HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
        try {
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod(reqMethod);

            if (json != null) {
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/json");
                OutputStream out = con.getOutputStream();
                out.write(json.getBytes("UTF-8"));
                out.close();
            }

            if (basicAuth != null)
                con.setRequestProperty("Authorization", basicAuth);

            if (props != null)
                for (Map.Entry<String, String> entry : props.entrySet())
                    con.setRequestProperty(entry.getKey(), entry.getValue());

            int responseCode = con.getResponseCode();
            if (responseCode != expectedResponseCode)
                throw new Exception("Unexpected response (See HTTP_* constant values on HttpURLConnection): " + responseCode);

            if (responseCode / 100 == 2) { // response codes in the 200s mean success
                StringBuilder response = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                while (in.ready())
                    response.append(in.readLine()).append('\n');
                in.close();
                return response.toString();
            } else
                return null;
        } finally {
            con.disconnect();
        }
    }
}
