package demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

@Service
public class LlmService {

    private final WebClient webClient;
    private final String model;

    public LlmService(WebClient.Builder builder,
                      @Value("${ollama.base-url}") String baseUrl,
                      @Value("${ollama.model}") String model) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.model = model;
    }

    public String convertNlToGraphql(String userText) {
        String prompt = buildPrompt(userText);
        System.out.println("Prompt:\n" + prompt);

        Map<String, Object> payload = Map.of(
            "model", model,
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "stream", false
        );

        Map<String, Object> response;
        try {
            response = webClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Ollama API error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Ollama API", e);
        }

        if (response == null) {
            throw new RuntimeException("LLM returned null response");
        }

        Map<String, Object> msg = (Map<String, Object>) response.get("message");
        if (msg == null) {
            throw new RuntimeException("Missing 'message' field in response: " + response);
        }

        String message = (String) msg.get("content");
        if (message == null || message.isBlank()) {
            throw new RuntimeException("LLM returned empty content");
        }

        System.out.println("Raw LLM message:\n" + message);

        // 🔧 Fix GraphQL according to schema
        String content = fixMutations(message);

        // sanitize markdown fences
        String sanitized = sanitizeContent(content);

        // validate GraphQL
        return validateGraphql(sanitized);
    }

    private String buildPrompt(String userText) {
        return """
        You are a strict translator: convert the user's natural language into a single valid GraphQL operation (query or mutation) using ONLY the Star Wars schema (Character, Species).
        Return ONLY the GraphQL operation. No explanations, no markdown, no backticks.
        If you cannot, return [NO_GQL].

        Examples:
        NL: Show all characters
        GraphQL:
        query { getAllCharacters { id name height mass } }

        NL: Create character named Leia with height 150 mass 49
        GraphQL:
        mutation { createCharacter(character:{ name:"Leia", height:"150", mass:"49" }) { id name height mass } }

        Now convert this:
        %s
        """.formatted(userText);
    }

    private String sanitizeContent(String raw) {
        return raw.replaceAll("(?s)```.*?```", "").trim();
    }

    private String validateGraphql(String content) {
        String trimmed = content.trim();

        if (trimmed.startsWith("query") || trimmed.startsWith("mutation")) {
            return trimmed;
        }

        if (trimmed.contains("[NO_GQL]")) {
            throw new RuntimeException("[NO_GQL] returned by LLM");
        }

        int q = trimmed.indexOf("query");
        int m = trimmed.indexOf("mutation");
        int idx = q >= 0 ? q : m;
        if (idx >= 0) {
            return trimmed.substring(idx).trim();
        }

        throw new RuntimeException("Invalid GraphQL content: " + trimmed);
    }

//    private String fixMutations(String message) {
//        String content = message;
//
//        // 1️⃣ Standardize argument names
//        content = content.replaceAll("speciesUpdate|data", "species");
//        content = content.replaceAll("characterUpdate", "character");
//
//        // 2️⃣ Remove subselection from delete mutations (String return type)
//        content = content.replaceAll(
//        	    "(delete(Character|Species)\\s*\\(.*?\\))\\s*\\{[^}]*\\}", 
//        	    "$1"
//        	);
//
//        // 3️⃣ Fix update mutations: extract id and rebuild correctly
//        Pattern updatePattern = Pattern.compile(
//            "(update(Character|Species))\\s*\\(\\s*(character|species)\\s*:\\s*\\{\\s*id\\s*:\\s*\"([^\"]+)\"\\s*,(.*?)\\}\\s*\\)",
//            Pattern.DOTALL
//        );
//        Matcher updateMatcher = updatePattern.matcher(content);
//        if (updateMatcher.find()) {
//            String mutationName = updateMatcher.group(1);
//            String inputField = updateMatcher.group(3);
//            String id = updateMatcher.group(4).trim();
//            String fields = updateMatcher.group(5).trim();
//            content = "mutation { " + mutationName + "(id: \"" + id + "\", " + inputField + ":{" + fields + "}) { id } }";
//        }
//
//        // 4️⃣ Ensure numeric strings are converted to Int
//        Pattern numericPattern = Pattern.compile("(height|mass|averageHeight|averageLifespan)\\s*:\\s*\"(\\d+)\"");
//        Matcher numMatcher = numericPattern.matcher(content);
//        content = numMatcher.replaceAll("$1:$2");
//
//        // 5️⃣ Ensure update/create mutations have subselection { id } if missing
//        if (content.matches("(?s).*\\b(update|create)(Character|Species)\\b\\s*\\(.*\\)\\s*\\}$")) {
//            content = content.replaceAll("(\\b(update|create)(Character|Species)\\b\\s*\\(.*\\))\\s*\\}$", "$1 { id } }");
//        }
//
//        return content.trim();
//    }
    private String fixMutations(String message) {
        String content = message.trim();

        // 1️⃣ Normalize argument names
        content = content.replaceAll("\\bcharacterUpdate\\b", "character");
        content = content.replaceAll("\\bspeciesUpdate\\b|\\bdata\\b", "species");

        // 2️⃣ Fix delete by id (existing)
        content = content.replaceAll("\\bdeleteSpecies\\s*\\(\\s*speciesId\\s*:", "deleteSpecies(id:");
        content = content.replaceAll("\\bdeleteCharacter\\s*\\(\\s*characterId\\s*:", "deleteCharacter(id:");
        
        // 3️⃣ Fix delete by name -> use ByName mutations
        content = content.replaceAll(
            "deleteSpecies\\s*\\(\\s*species\\s*:\\s*\"([^\"]+)\"\\s*\\)",
            "deleteSpeciesByName(name:\"$1\")"
        );
        content = content.replaceAll(
            "deleteCharacter\\s*\\(\\s*character\\s*:\\s*\"([^\"]+)\"\\s*\\)",
            "deleteCharacterByName(name:\"$1\")"
        );

        // 4️⃣ Remove subselection from delete mutations that return String
        content = content.replaceAll("(delete(Character|Species)(ByName)?\\s*\\(.*?\\))\\s*\\{[^}]*\\}", "$1");

        // 5️⃣ Fix update mutations: ensure 'id' argument is outside the object
        Pattern updatePattern = Pattern.compile(
            "(update(Character|Species))\\s*\\(\\s*(character|species)\\s*:\\s*\\{\\s*id\\s*:\\s*\"([^\"]+)\"\\s*,(.*?)\\}\\s*\\)",
            Pattern.DOTALL
        );
        Matcher updateMatcher = updatePattern.matcher(content);
        if (updateMatcher.find()) {
            String mutationName = updateMatcher.group(1);
            String inputField = updateMatcher.group(3);
            String id = updateMatcher.group(4).trim();
            String fields = updateMatcher.group(5).trim();
            content = "mutation { " + mutationName + "(id: \"" + id + "\", " + inputField + ":{" + fields + "}) { id } }";
        }

        // 6️⃣ Convert numeric string fields to Int
        Pattern numericPattern = Pattern.compile("(height|mass|averageHeight|averageLifespan)\\s*:\\s*\"(\\d+)\"");
        Matcher numMatcher = numericPattern.matcher(content);
        content = numMatcher.replaceAll("$1:$2");

        // 7️⃣ Ensure create/update mutations have subselection { id } if missing
        if (content.matches("(?s).*\\b(update|create)(Character|Species)\\b\\s*\\(.*\\)\\s*\\}$")) {
            content = content.replaceAll("(\\b(update|create)(Character|Species)\\b\\s*\\(.*\\))\\s*\\}$", "$1 { id } }");
        }

        return content.trim();
    }

}
