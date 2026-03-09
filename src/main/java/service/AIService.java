package service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import util.AppConfig;

public class AIService {

    private static final String API_KEY = AppConfig.GEMINI_API_KEY;
    private static final String MODEL = "gemini-2.5-flash";

    public static String generateResponse(String prompt) {
        HttpURLConnection conn = null;

        try {
            String endpoint =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                + MODEL
                + ":generateContent?key="
                + API_KEY;

            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(180000);
            conn.setDoOutput(true);

            JsonObject part = new JsonObject();
            part.addProperty("text", prompt);

            JsonArray parts = new JsonArray();
            parts.add(part);

            JsonObject content = new JsonObject();
            content.add("parts", parts);

            JsonArray contents = new JsonArray();
            contents.add(content);

            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.7);

            JsonObject body = new JsonObject();
            body.add("contents", contents);
            body.add("generationConfig", generationConfig);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();

            InputStream stream;
            if (status >= 200 && status < 300) {
                stream = conn.getInputStream();
            } else {
                stream = conn.getErrorStream();
                String errorText = readStream(stream);
                System.out.println("Gemini API error status: " + status);
                System.out.println("Gemini API error body: " + errorText);
                return "Gemini API request failed. Check server console for the exact error.";
            }

            String responseText = readStream(stream);
            System.out.println("Gemini raw response: " + responseText);

            JsonObject json = JsonParser.parseString(responseText).getAsJsonObject();

            if (json.has("candidates")) {
                JsonArray candidates = json.getAsJsonArray("candidates");
                if (candidates != null && candidates.size() > 0) {
                    JsonObject first = candidates.get(0).getAsJsonObject();
                    JsonObject contentObj = first.getAsJsonObject("content");
                    JsonArray responseParts = contentObj.getAsJsonArray("parts");
                    if (responseParts != null && responseParts.size() > 0) {
                        JsonObject firstPart = responseParts.get(0).getAsJsonObject();
                        if (firstPart.has("text")) {
                            return firstPart.get("text").getAsString();
                        }
                    }
                }
            }

            return "Gemini returned no usable text.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, ChatOrbit could not generate a response.";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readStream(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}