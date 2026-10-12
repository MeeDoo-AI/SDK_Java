package co.meedoo.sdk;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

public class Models {
    private final MeeDooClient client;

    Models(MeeDooClient client) { this.client = client; }

    public String list() throws IOException, InterruptedException, MeeDooException {
        return client.request("GET", "/models", null);
    }

    public JsonObject listAsJson() throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(list()).getAsJsonObject();
    }
}