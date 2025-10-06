package demo.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import demo.service.LlmService;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final LlmService llmService;
    private final GraphQL graphQL;

    @Autowired
    public ChatController(LlmService llmService, GraphQL graphQL) {
        this.llmService = llmService;
        this.graphQL = graphQL;
    }

    @PostMapping("/nl")
    public ResponseEntity<?> chat(@RequestBody Map<String,String> body) {
    	System.out.println("..................fdsafadsfa.....................");
        String text = body.get("text");
        if (text == null || text.isBlank()) return ResponseEntity.badRequest().body(Map.of("error","text required"));

        try {
            String gql = llmService.convertNlToGraphql(text);
            System.out.println(gql);
//            gql = "mutation { createCharacter(character:{ name:\"as\", height: 22, mass: 1 }) { id name height mass } }";
            System.out.println("..................1");
            ExecutionResult result = graphQL.execute(gql);
            System.out.println("..................2");
            if (!result.getErrors().isEmpty()) {
                // return safe error messages (not raw exception objects)
            	System.out.println("..................3");
                return ResponseEntity.badRequest().body(Map.of("errors", result.getErrors().stream().map(e -> e.getMessage()).toList(), "query", gql));
            }
            System.out.println("..................4");
            return ResponseEntity.ok(result.toSpecification());
        } catch (Exception e) {
        	System.out.println("..................5");
            return ResponseEntity.badRequest().body(Map.of("error","Failed to generate/execute GraphQL","message", e.getMessage()));
        }
    }
}
