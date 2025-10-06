package demo.config;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import demo.model.CharacterD;
import demo.model.CharacterDTO;
import demo.model.Species;
import demo.model.SpeciesDTO;
import demo.repository.CharacterRepository;
import demo.repository.SpeciesRepository;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

@Configuration
public class GraphQLConfig {

    private final CharacterRepository characterRepo;
    private final SpeciesRepository speciesRepo;

    public GraphQLConfig(CharacterRepository characterRepo, SpeciesRepository speciesRepo) {
        this.characterRepo = characterRepo;
        this.speciesRepo = speciesRepo;
    }

    @Bean
    public GraphQL graphQL() throws Exception {
        InputStreamReader schemaReader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("schema.graphqls")
        );
        if (schemaReader == null) throw new RuntimeException("schema.graphqls not found");

        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaReader);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);

        return GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
            // ==================== Query ====================
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("getAllCharacters", env ->
                        characterRepo.findAll().stream()
                                .map(CharacterDTO::fromEntity)
                                .collect(Collectors.toList()))
                .dataFetcher("getCharacter", env ->
                        characterRepo.findById(env.getArgument("id"))
                                .map(CharacterDTO::fromEntity)
                                .orElse(null))
                .dataFetcher("getAllSpecies", env ->
                        speciesRepo.findAll().stream()
                                .map(SpeciesDTO::fromEntity)
                                .collect(Collectors.toList()))
                .dataFetcher("getSpecies", env ->
                        speciesRepo.findById(env.getArgument("id"))
                                .map(SpeciesDTO::fromEntity)
                                .orElse(null))
            )

            // ==================== Mutation ====================
            .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                // Character mutations
                .dataFetcher("createCharacter", env -> CharacterDTO.fromEntity(
                        characterRepo.save(mapToCharacter(env.getArgument("character")))))
                .dataFetcher("updateCharacter", env -> {
                    CharacterD existing = characterRepo.findById(env.getArgument("id")).orElseThrow();
                    updateCharacter(existing, env.getArgument("character"));
                    return CharacterDTO.fromEntity(characterRepo.save(existing));
                })
                .dataFetcher("deleteCharacter", env -> {
                    String id = env.getArgument("id");
                    CharacterD deleted = characterRepo.findById(id).orElse(null);
                    if (deleted != null) characterRepo.deleteById(id);
                    return deleted;
                })
                

                // Species mutations
                .dataFetcher("createSpecies", env -> SpeciesDTO.fromEntity(
                        speciesRepo.save(mapToSpecies(env.getArgument("species")))))
                .dataFetcher("updateSpecies", env -> {
                    Species existing = speciesRepo.findById(env.getArgument("id")).orElseThrow();
                    updateSpecies(existing, env.getArgument("species"));
                    return SpeciesDTO.fromEntity(speciesRepo.save(existing));
                })
                .dataFetcher("deleteSpecies", env -> {
                    String id = env.getArgument("id");
                    Species deleted = speciesRepo.findById(id).orElse(null);
                    if (deleted != null) speciesRepo.deleteById(id);
                    return deleted;
                })
                
            )
            .build();
    }

    // ==================== Character Helpers ====================
    private CharacterD mapToCharacter(Map<String, Object> map) {
        CharacterD c = new CharacterD();
        if (map == null) return c;

        c.setName(asString(map.get("name")));
        c.setHeight(asInteger(map.get("height")));
        c.setMass(asInteger(map.get("mass")));
        c.setHairColors(parseList(map.get("hairColors")));
        c.setSkinColors(parseList(map.get("skinColors")));
        c.setEyeColors(parseList(map.get("eyeColors")));
        c.setBirthYear(asString(map.get("birthYear")));
        c.setGender(asString(map.get("gender")));
        c.setHomeworld(asString(map.get("homeworld")));
        c.setSpecies(asString(map.get("species")));
        return c;
    }

    private void updateCharacter(CharacterD c, Map<String, Object> map) {
        if (map == null) return;
        updateField(map.get("name"), c::setName);
        updateField(map.get("height"), val -> c.setHeight(asInteger(val)));
        updateField(map.get("mass"), val -> c.setMass(asInteger(val)));
        updateField(map.get("hairColors"), val -> c.setHairColors(parseList(val)));
        updateField(map.get("skinColors"), val -> c.setSkinColors(parseList(val)));
        updateField(map.get("eyeColors"), val -> c.setEyeColors(parseList(val)));
        updateField(map.get("birthYear"), c::setBirthYear);
        updateField(map.get("gender"), c::setGender);
        updateField(map.get("homeworld"), c::setHomeworld);
        updateField(map.get("species"), c::setSpecies);
    }

    // ==================== Species Helpers ====================
    private Species mapToSpecies(Map<String, Object> map) {
        Species s = new Species();
        if (map == null) return s;

        s.setName(asString(map.get("name")));
        s.setClassification(asString(map.get("classification")));
        s.setDesignation(asString(map.get("designation")));
        s.setAverageHeight(asInteger(map.get("averageHeight")));
        s.setAverageLifespan(asInteger(map.get("averageLifespan")));
        s.setHairColors(parseList(map.get("hairColors")));
        s.setSkinColors(parseList(map.get("skinColors")));
        s.setEyeColors(parseList(map.get("eyeColors")));
        s.setLanguage(asString(map.get("language")));
        s.setHomeworld(asString(map.get("homeworld")));
        return s;
    }

    private void updateSpecies(Species s, Map<String, Object> map) {
        if (map == null) return;
        updateField(map.get("name"), s::setName);
        updateField(map.get("classification"), s::setClassification);
        updateField(map.get("designation"), s::setDesignation);
        updateField(map.get("averageHeight"), val -> s.setAverageHeight(asInteger(val)));
        updateField(map.get("averageLifespan"), val -> s.setAverageLifespan(asInteger(val)));
        updateField(map.get("hairColors"), val -> s.setHairColors(parseList(val)));
        updateField(map.get("skinColors"), val -> s.setSkinColors(parseList(val)));
        updateField(map.get("eyeColors"), val -> s.setEyeColors(parseList(val)));
        updateField(map.get("language"), s::setLanguage);
        updateField(map.get("homeworld"), s::setHomeworld);
    }

    // ==================== Utility Helpers ====================
    private String asString(Object value) {
        if (value == null) return null;
        String str = value.toString().trim();
        return str.isEmpty() || "NA".equalsIgnoreCase(str) ? null : str;
    }

    private Integer asInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            String str = value.toString().trim();
            if (str.isEmpty() || "NA".equalsIgnoreCase(str)) return null;
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> parseList(Object value) {
        if (value == null) return null;
        if (value instanceof List) return (List<String>) value;
        if (value instanceof String) {
            String str = (String) value;
            if (str.isBlank() || "NA".equalsIgnoreCase(str)) return null;
            return List.of(str.split("\\s*,\\s*"));
        }
        return null;
    }

    private <T> void updateField(Object value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept((T) value);
    }
}
