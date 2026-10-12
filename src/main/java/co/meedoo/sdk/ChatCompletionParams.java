package co.meedoo.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;

/**
 * Builder for chat completion request parameters.
 * Supports all MeeDoo API parameters.
 */
public class ChatCompletionParams {
    private final JsonObject json = new JsonObject();
    private static final Gson gson = new Gson();

    public ChatCompletionParams(String model, List<Map<String, String>> messages) {
        json.addProperty("model", model);
        JsonArray msgArray = new JsonArray();
        for (Map<String, String> msg : messages) {
            JsonObject m = new JsonObject();
            m.addProperty("role", msg.getOrDefault("role", "user"));
            m.addProperty("content", msg.getOrDefault("content", ""));
            msgArray.add(m);
        }
        json.add("messages", msgArray);
    }

    public ChatCompletionParams stream(boolean stream) { json.addProperty("stream", stream); return this; }
    public ChatCompletionParams temperature(double t) { json.addProperty("temperature", t); return this; }
    public ChatCompletionParams topP(double p) { json.addProperty("top_p", p); return this; }
    public ChatCompletionParams minP(double p) { json.addProperty("min_p", p); return this; }
    public ChatCompletionParams maxTokens(int n) { json.addProperty("max_tokens", n); return this; }
    public ChatCompletionParams frequencyPenalty(double p) { json.addProperty("frequency_penalty", p); return this; }
    public ChatCompletionParams presencePenalty(double p) { json.addProperty("presence_penalty", p); return this; }
    public ChatCompletionParams reasoningEffort(String effort) { json.addProperty("reasoning_effort", effort); return this; }
    public ChatCompletionParams seed(Object seed) { json.add("seed", gson.toJsonTree(seed)); return this; }

    public ChatCompletionParams stop(List<String> stop) {
        json.add("stop", gson.toJsonTree(stop));
        return this;
    }

    public ChatCompletionParams responseFormat(String type) {
        JsonObject fmt = new JsonObject();
        fmt.addProperty("type", type);
        json.add("response_format", fmt);
        return this;
    }

    public ChatCompletionParams think(boolean think) { json.addProperty("think", think); return this; }

    public ChatCompletionParams features(Map<String, Boolean> features) {
        json.add("features", gson.toJsonTree(features));
        return this;
    }

    public ChatCompletionParams toolIds(List<String> ids) {
        json.add("tool_ids", gson.toJsonTree(ids));
        return this;
    }

    public ChatCompletionParams tools(List<Map<String, Object>> tools) {
        json.add("tools", gson.toJsonTree(tools));
        return this;
    }

    public ChatCompletionParams toolChoice(String choice) { json.addProperty("tool_choice", choice); return this; }

    public ChatCompletionParams params(Map<String, Object> params) {
        json.add("params", gson.toJsonTree(params));
        return this;
    }

    public ChatCompletionParams functionCalling(String mode) {
        JsonObject params = json.has("params") ? json.getAsJsonObject("params") : new JsonObject();
        params.addProperty("function_calling", mode);
        json.add("params", params);
        return this;
    }

    public ChatCompletionParams system(String system) {
        JsonObject params = json.has("params") ? json.getAsJsonObject("params") : new JsonObject();
        params.addProperty("system", system);
        json.add("params", params);
        return this;
    }

    public ChatCompletionParams chatId(String chatId) { json.addProperty("chat_id", chatId); return this; }
    public ChatCompletionParams messageId(String id) { json.addProperty("id", id); return this; }
    public ChatCompletionParams parentId(String id) { json.addProperty("parent_id", id); return this; }
    public ChatCompletionParams sessionId(String id) { json.addProperty("session_id", id); return this; }

    public ChatCompletionParams variables(Map<String, Object> vars) {
        json.add("variables", gson.toJsonTree(vars));
        return this;
    }

    public ChatCompletionParams backgroundTasks(Map<String, Boolean> tasks) {
        json.add("background_tasks", gson.toJsonTree(tasks));
        return this;
    }

    public ChatCompletionParams useCredits(boolean use) { json.addProperty("use_credits", use); return this; }
    public ChatCompletionParams metadata(Map<String, Object> meta) { json.add("metadata", gson.toJsonTree(meta)); return this; }

    public String toJson() { return json.toString(); }
}