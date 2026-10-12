package co.meedoo.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Map;

public class Judge {
    private final MeeDooClient client;
    private static final Gson gson = new Gson();

    Judge(MeeDooClient client) { this.client = client; }

    public String status() throws IOException, InterruptedException, MeeDooException {
        return client.request("GET", "/v1/judge/status", null);
    }

    public String decide(Object state, Map<String, Object> questions) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.add("state", gson.toJsonTree(state));
        body.add("questions", gson.toJsonTree(questions));
        return client.request("POST", "/v1/judge/decide", body.toString());
    }

    public String noul(Object state, String instructions, Map<String, String> criteria) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.add("state", gson.toJsonTree(state));
        body.addProperty("instructions", instructions);
        if (criteria != null) body.add("criteria", gson.toJsonTree(criteria));
        return client.request("POST", "/v1/judge/noul", body.toString());
    }

    public String choice(Object state, String instructions, Map<String, String> criteria) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.add("state", gson.toJsonTree(state));
        body.addProperty("instructions", instructions);
        body.add("criteria", gson.toJsonTree(criteria));
        return client.request("POST", "/v1/judge/choice", body.toString());
    }

    public String score(Object state, String instructions, Object levels) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.add("state", gson.toJsonTree(state));
        body.addProperty("instructions", instructions);
        body.add("levels", gson.toJsonTree(levels));
        return client.request("POST", "/v1/judge/score", body.toString());
    }

    public String shouldSearchWeb(String userMessage) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.addProperty("user_message", userMessage);
        return client.request("POST", "/v1/judge/should-search-web", body.toString());
    }

    public String detectInjection(String userMessage) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.addProperty("user_message", userMessage);
        return client.request("POST", "/v1/judge/detect-injection", body.toString());
    }

    public String moderate(String text) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.addProperty("text", text);
        return client.request("POST", "/v1/judge/moderate", body.toString());
    }

    public String detectSpam(String message, String userHistory) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.addProperty("message", message);
        body.addProperty("user_history", userHistory != null ? userHistory : "");
        return client.request("POST", "/v1/judge/detect-spam", body.toString());
    }

    public String validateResponse(String userMessage, String llmResponse, String sources) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.addProperty("user_message", userMessage);
        body.addProperty("llm_response", llmResponse);
        body.addProperty("sources", sources != null ? sources : "");
        return client.request("POST", "/v1/judge/validate-response", body.toString());
    }

    public String filterMemory(String information, String conversationContext) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.addProperty("information", information);
        body.addProperty("conversation_context", conversationContext != null ? conversationContext : "");
        return client.request("POST", "/v1/judge/filter-memory", body.toString());
    }

    public String isPrayerTimes(String userMessage) throws IOException, InterruptedException, MeeDooException {
        JsonObject body = new JsonObject();
        body.addProperty("user_message", userMessage);
        return client.request("POST", "/v1/judge/is-prayer-times", body.toString());
    }

    public String classifyTicket(String subject, String body) throws IOException, InterruptedException, MeeDooException {
        JsonObject json = new JsonObject();
        json.addProperty("subject", subject);
        json.addProperty("body", body);
        return client.request("POST", "/v1/judge/classify-ticket", json.toString());
    }

    public String sentiment() throws IOException, InterruptedException, MeeDooException {
        return client.request("GET", "/v1/judge/sentiment", null);
    }

    public JsonObject statusAsJson() throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(status()).getAsJsonObject();
    }

    public JsonObject shouldSearchWebAsJson(String msg) throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(shouldSearchWeb(msg)).getAsJsonObject();
    }

    public JsonObject detectInjectionAsJson(String msg) throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(detectInjection(msg)).getAsJsonObject();
    }

    public JsonObject moderateAsJson(String text) throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(moderate(text)).getAsJsonObject();
    }
}