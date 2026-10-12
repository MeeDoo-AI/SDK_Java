package co.meedoo.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Chat {
    private final MeeDooClient client;
    private final Completions completions;
    private static final Gson gson = new Gson();

    Chat(MeeDooClient client) {
        this.client = client;
        this.completions = new Completions(client);
    }

    public Completions completions() { return completions; }

    public static class Completions {
        private final MeeDooClient client;
        private static final Gson gson = new Gson();

        Completions(MeeDooClient client) {
            this.client = client;
        }

        public ChatCompletion create(ChatCompletionParams params) throws IOException, InterruptedException, MeeDooException {
            String body = params.toJson();
            String resp = client.request("POST", "/chat/completions", body);
            return ChatCompletion.fromJson(resp);
        }

        public ChatCompletion create(String model, List<Map<String, String>> messages) throws IOException, InterruptedException, MeeDooException {
            return create(new ChatCompletionParams(model, messages));
        }
    }
}