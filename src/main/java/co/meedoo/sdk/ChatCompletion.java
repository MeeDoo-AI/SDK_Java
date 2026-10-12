package co.meedoo.sdk;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Represents a chat completion response.
 */
public class ChatCompletion {
    private final JsonObject json;

    ChatCompletion(JsonObject json) {
        this.json = json;
    }

    public static ChatCompletion fromJson(String jsonString) {
        return new ChatCompletion(JsonParser.parseString(jsonString).getAsJsonObject());
    }

    public String getId() { return json.get("id").getAsString(); }
    public String getModel() { return json.get("model").getAsString(); }
    public String getObject() { return json.get("object").getAsString(); }

    public String getContent() {
        return json.getAsJsonArray("choices").get(0).getAsJsonObject()
                .getAsJsonObject("message").get("content").getAsString();
    }

    public String getRole() {
        return json.getAsJsonArray("choices").get(0).getAsJsonObject()
                .getAsJsonObject("message").get("role").getAsString();
    }

    public String getFinishReason() {
        return json.getAsJsonArray("choices").get(0).getAsJsonObject()
                .get("finish_reason").getAsString();
    }

    public JsonObject getUsage() {
        return json.has("usage") ? json.getAsJsonObject("usage") : new JsonObject();
    }

    public int getTotalTokens() {
        return getUsage().has("total_tokens") ? getUsage().get("total_tokens").getAsInt() : 0;
    }

    public int getInputTokens() {
        return getUsage().has("input_tokens") ? getUsage().get("input_tokens").getAsInt() : 0;
    }

    public int getOutputTokens() {
        return getUsage().has("output_tokens") ? getUsage().get("output_tokens").getAsInt() : 0;
    }

    public JsonObject getRawJson() { return json; }

    @Override
    public String toString() {
        return getContent();
    }
}