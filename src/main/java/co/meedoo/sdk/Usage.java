package co.meedoo.sdk;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

public class Usage {
    private final MeeDooClient client;

    Usage(MeeDooClient client) { this.client = client; }

    public String credits() throws IOException, InterruptedException, MeeDooException {
        return client.request("GET", "/v1/usage/credits", null);
    }

    public JsonObject creditsAsJson() throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(credits()).getAsJsonObject();
    }

    public String stats() throws IOException, InterruptedException, MeeDooException {
        return client.request("GET", "/v1/usage/stats", null);
    }

    public JsonObject statsAsJson() throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(stats()).getAsJsonObject();
    }
}