package co.meedoo.sdk;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

public class Files {
    private final MeeDooClient client;

    Files(MeeDooClient client) { this.client = client; }

    /**
     * Upload a file to the MeeDoo API using multipart/form-data.
     *
     * @param filePath path to the file on disk
     * @return JSON response string with file metadata
     */
    public String upload(String filePath) throws IOException, InterruptedException, MeeDooException {
        return upload(filePath, null, false);
    }

    public String upload(String filePath, String metadata, boolean process)
            throws IOException, InterruptedException, MeeDooException {
        Path path = Path.of(filePath);
        byte[] fileBytes = java.nio.file.Files.readAllBytes(path);
        String filename = path.getFileName().toString();
        String boundary = "----MeeDooBoundary" + System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
          .append(filename).append("\"\r\n");
        sb.append("Content-Type: application/octet-stream\r\n\r\n");

        StringBuilder suffix = new StringBuilder();
        suffix.append("\r\n");
        suffix.append("--").append(boundary).append("\r\n");
        suffix.append("Content-Disposition: form-data; name=\"process\"\r\n\r\n");
        suffix.append(String.valueOf(process)).append("\r\n");

        if (metadata != null && !metadata.isEmpty()) {
            suffix.append("--").append(boundary).append("\r\n");
            suffix.append("Content-Disposition: form-data; name=\"metadata\"\r\n\r\n");
            suffix.append(metadata).append("\r\n");
        }
        suffix.append("--").append(boundary).append("--\r\n");

        byte[] prefixBytes = sb.toString().getBytes();
        byte[] suffixBytes = suffix.toString().getBytes();
        byte[] multipartBody = new byte[prefixBytes.length + fileBytes.length + suffixBytes.length];
        System.arraycopy(prefixBytes, 0, multipartBody, 0, prefixBytes.length);
        System.arraycopy(fileBytes, 0, multipartBody, prefixBytes.length, fileBytes.length);
        System.arraycopy(suffixBytes, 0, multipartBody, prefixBytes.length + fileBytes.length, suffixBytes.length);

        URI uri = URI.create(client.getBaseUrl() + "/v1/files/");
        HttpRequest req = HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer " + client.getApiKey())
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(120))
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> resp = client.getHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        int code = resp.statusCode();
        String respBody = resp.body();

        if (code == 200) {
            return respBody;
        } else if (code == 401) {
            throw new AuthenticationException("Invalid API key");
        } else if (code == 429) {
            throw new RateLimitException("Rate limit exceeded");
        } else {
            String detail = respBody;
            try {
                JsonObject errJson = JsonParser.parseString(respBody).getAsJsonObject();
                if (errJson.has("detail")) {
                    detail = errJson.get("detail").getAsString();
                }
            } catch (Exception ignored) {}
            throw new APIException(detail, code);
        }
    }

    public String list() throws IOException, InterruptedException, MeeDooException {
        return client.request("GET", "/v1/files/", null);
    }

    public JsonObject listAsJson() throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(list()).getAsJsonObject();
    }

    public String get(String fileId) throws IOException, InterruptedException, MeeDooException {
        return client.request("GET", "/v1/files/" + fileId, null);
    }

    public JsonObject getAsJson(String fileId) throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(get(fileId)).getAsJsonObject();
    }

    /**
     * Download the raw content of a file.
     *
     * @param fileId file ID
     * @return raw file content as byte array
     */
    public byte[] getContent(String fileId) throws IOException, InterruptedException, MeeDooException {
        URI uri = URI.create(client.getBaseUrl() + "/v1/files/" + fileId + "/content");
        HttpRequest req = HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer " + client.getApiKey())
                .timeout(Duration.ofSeconds(120))
                .GET().build();

        HttpResponse<byte[]> resp = client.getHttpClient().send(req, HttpResponse.BodyHandlers.ofByteArray());
        int code = resp.statusCode();

        if (code == 200) {
            return resp.body();
        } else if (code == 401) {
            throw new AuthenticationException("Invalid API key");
        } else if (code == 429) {
            throw new RateLimitException("Rate limit exceeded");
        } else {
            String detail = new String(resp.body());
            throw new APIException(detail, code);
        }
    }

    public String delete(String fileId) throws IOException, InterruptedException, MeeDooException {
        return client.request("DELETE", "/v1/files/" + fileId, null);
    }

    public JsonObject deleteAsJson(String fileId) throws IOException, InterruptedException, MeeDooException {
        return JsonParser.parseString(delete(fileId)).getAsJsonObject();
    }
}