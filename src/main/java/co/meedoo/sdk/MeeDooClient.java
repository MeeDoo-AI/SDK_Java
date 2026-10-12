package co.meedoo.sdk;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * MeeDoo AI Java SDK — Main Client
 *
 * Usage:
 *   MeeDooClient client = new MeeDooClient("md-your-api-key");
 *   ChatCompletion resp = client.chat().completions().create(
 *       new ChatCompletionParams("meedoo:latest",
 *           List.of(new Message("user", "Hello!")))
 *   );
 *   System.out.println(resp.getContent());
 */
public class MeeDooClient {
    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;
    private final int maxRetries;

    private final Chat chat;
    final Models models;
    final Usage usage;
    final Judge judge;

    public MeeDooClient(String apiKey) {
        this(apiKey, "https://api.meedoo.co/api", 120, 3);
    }

    public MeeDooClient(String apiKey, String baseUrl) {
        this(apiKey, baseUrl, 120, 3);
    }

    public MeeDooClient(String apiKey, String baseUrl, int timeoutSeconds, int maxRetries) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("apiKey is required");
        }
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.maxRetries = maxRetries;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        this.chat = new Chat(this);
        this.models = new Models(this);
        this.usage = new Usage(this);
        this.judge = new Judge(this);
    }

    public Chat chat() { return chat; }
    public Models models() { return models; }
    public Usage usage() { return usage; }
    public Judge judge() { return judge; }

    String getApiKey() { return apiKey; }
    String getBaseUrl() { return baseUrl; }
    HttpClient getHttpClient() { return httpClient; }

    String request(String method, String path, String body) throws IOException, InterruptedException, MeeDooException {
        URI uri = URI.create(baseUrl + path);
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(120));

        if (body != null) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        MeeDooException lastError = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                HttpResponse<String> resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                int code = resp.statusCode();
                String respBody = resp.body();

                if (code == 200) {
                    return respBody;
                } else if (code == 401) {
                    throw new AuthenticationException("Invalid API key");
                } else if (code == 429) {
                    throw new RateLimitException("Rate limit exceeded");
                } else if (code >= 500) {
                    lastError = new APIException("Server error: " + code + " " + respBody);
                    if (attempt < maxRetries) {
                        Thread.sleep(1000L * (1L << attempt));
                        continue;
                    }
                } else {
                    String detail = respBody;
                    try {
                        JsonObject errJson = JsonParser.parseString(respBody).getAsJsonObject();
                        if (errJson.has("detail")) {
                            detail = errJson.get("detail").getAsString();
                        }
                    } catch (Exception ignored) {}
                    if (detail.contains("Insufficient API credits")) {
                        throw new InsufficientCreditsException(detail);
                    } else if (detail.contains("Model not found")) {
                        throw new ModelNotFoundException(detail);
                    }
                    throw new APIException(detail, code);
                }
            } catch (InterruptedException e) {
                throw e;
            } catch (IOException e) {
                lastError = new APIException("Connection error: " + e.getMessage());
                if (attempt < maxRetries) {
                    Thread.sleep(1000L * (1L << attempt));
                    continue;
                }
            }
        }
        throw lastError != null ? lastError : new APIException("Request failed");
    }

    public String getConfig() throws IOException, InterruptedException, MeeDooException {
        return request("GET", "/config", null);
    }

    public String getHealth() throws IOException, InterruptedException, MeeDooException {
        String healthUrl = baseUrl.replace("/api", "/health");
        URI uri = URI.create(healthUrl);
        HttpRequest req = HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(120))
                .GET().build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }
}