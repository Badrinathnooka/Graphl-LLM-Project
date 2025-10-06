package demo;

import com.opencsv.CSVReader;
import demo.model.CharacterD;
import demo.repository.CharacterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class DataLoader2 implements CommandLineRunner {

    private final CharacterRepository characterRepository;

    public DataLoader2(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/characters.csv"))) {
            String[] line;
            boolean headerSkipped = false;

            while ((line = reader.readNext()) != null) {
                if (!headerSkipped) { 
                    headerSkipped = true;
                    continue;
                }

                final String[] currentLine = line;
                String name = normalize(currentLine[0]);

                // Fetch all matching characters
                List<CharacterD> existingList = characterRepository.findAllByName(name);

                if (existingList.isEmpty()) {
                    // Create a new character if not found
                    CharacterD newCharacter = new CharacterD();
                    newCharacter.setName(name);
                    newCharacter.setHeight(parseIntOrNull(currentLine[1]));
                    newCharacter.setMass(parseIntOrNull(currentLine[2]));
                    newCharacter.setHairColors(parseList(currentLine[3]));
                    newCharacter.setSkinColors(parseList(currentLine[4]));
                    newCharacter.setEyeColors(parseList(currentLine[5]));
                    newCharacter.setBirthYear(normalize(currentLine[6]));
                    newCharacter.setGender(normalize(currentLine[7]));
                    newCharacter.setHomeworld(normalize(currentLine[8]));
                    newCharacter.setSpecies(normalize(currentLine[9]));

                    characterRepository.save(newCharacter);
                    System.out.println("🆕 Created: " + name);
                } else {
                    // Update existing characters
                    for (CharacterD existing : existingList) {
                        updateIfAvailable(() -> parseIntOrNull(currentLine[1]), existing.getHeight(), existing::setHeight);
                        updateIfAvailable(() -> parseIntOrNull(currentLine[2]), existing.getMass(), existing::setMass);
                        updateIfAvailable(() -> parseList(currentLine[3]), existing.getHairColors(), existing::setHairColors);
                        updateIfAvailable(() -> parseList(currentLine[4]), existing.getSkinColors(), existing::setSkinColors);
                        updateIfAvailable(() -> parseList(currentLine[5]), existing.getEyeColors(), existing::setEyeColors);
                        updateIfAvailable(() -> normalize(currentLine[6]), existing.getBirthYear(), existing::setBirthYear);
                        updateIfAvailable(() -> normalize(currentLine[7]), existing.getGender(), existing::setGender);
                        updateIfAvailable(() -> normalize(currentLine[8]), existing.getHomeworld(), existing::setHomeworld);
                        updateIfAvailable(() -> normalize(currentLine[9]), existing.getSpecies(), existing::setSpecies);

                        characterRepository.save(existing);
                        System.out.println("✅ Updated: " + name);
                    }
                }
            }
        }
    }

    // Normalize "NA" and blank strings to null
    private String normalize(String value) {
        return (value == null || value.isBlank() || "NA".equalsIgnoreCase(value)) ? null : value.trim();
    }

    // Parse integer safely
    private Integer parseIntOrNull(String value) {
        if (value == null || value.isBlank() || "NA".equalsIgnoreCase(value)) return null;
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Parse CSV list fields
    private List<String> parseList(String value) {
        if (value == null || value.isBlank() || "NA".equalsIgnoreCase(value)) return null;
        String[] parts = value.split("[,/]"); 
        List<String> list = new ArrayList<>();
        for (String part : parts) {
            String normalized = normalize(part);
            if (normalized != null) list.add(normalized);
        }
        return list.isEmpty() ? null : list;
    }

    // Update only if current value is null and new value is not null
    private <T> void updateIfAvailable(Supplier<T> newValueSupplier, T currentValue, Consumer<T> setter) {
        T newValue = newValueSupplier.get();
        if (newValue != null && currentValue == null) {
            setter.accept(newValue);
        }
    }
}
