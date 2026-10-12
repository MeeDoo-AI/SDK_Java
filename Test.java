import co.meedoo.sdk.*;
import java.util.*;

public class Test {
    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("MEEDEO_API_KEY");
        String baseUrl = System.getenv("MEEDEO_BASE_URL");
        if (baseUrl == null) baseUrl = "https://api.meedoo.co/api";
        if (apiKey == null || apiKey.isEmpty()) { System.out.println("MEEDEO_API_KEY not set"); return; }

        MeeDooClient client = new MeeDooClient(apiKey, baseUrl);

        System.out.println("=== Test 1: Basic Chat ===");
        ChatCompletion r = client.chat().completions().create(
            new ChatCompletionParams("meedoo:latest", List.of(Map.of("role", "user", "content", "Say hello in one word.")))
        );
        System.out.println("AI says: " + r.getContent());

        System.out.println("\n=== Test 2: Web Search ===");
        ChatCompletion r2 = client.chat().completions().create(
            new ChatCompletionParams("meedoo:latest", List.of(Map.of("role", "user", "content", "Bitcoin price today?")))
                .features(Map.of("web_search", true))
        );
        System.out.println("AI says: " + r2.getContent().substring(0, Math.min(100, r2.getContent().length())) + "...");

        System.out.println("\n=== Test 3: Think Mode ===");
        ChatCompletion r3 = client.chat().completions().create(
            new ChatCompletionParams("meedoo:latest", List.of(Map.of("role", "user", "content", "What is 7*8?"))).think(true)
        );
        System.out.println("AI says: " + r3.getContent().substring(0, Math.min(100, r3.getContent().length())));

        System.out.println("\n=== Test 4: Credits ===");
        var credits = client.usage().creditsAsJson();
        System.out.println("Balance: EUR" + credits.get("credit_balance"));

        System.out.println("\n=== ALL TESTS PASSED ===");
    }
}
