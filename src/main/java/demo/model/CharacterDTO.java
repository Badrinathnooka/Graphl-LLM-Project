package demo.model;

import java.util.List;
import org.bson.types.ObjectId;

public class CharacterDTO {
    private String id;
    private String name;
    private Integer height;   // changed to Integer
    private Integer mass;     // changed to Integer
    private List<String> hairColors;
    private String species; // only keep species name

    public static CharacterDTO fromEntity(CharacterD c) {
        CharacterDTO dto = new CharacterDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());

        // direct Integer mapping (no need for String conversion)
        dto.setHeight(c.getHeight());
        dto.setMass(c.getMass());

        dto.setHairColors(c.getHairColors());
        dto.setSpecies(c.getSpecies());

        return dto;
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Integer getMass() { return mass; }
    public void setMass(Integer mass) { this.mass = mass; }

    public List<String> getHairColors() { return hairColors; }
    public void setHairColors(List<String> hairColors) { this.hairColors = hairColors; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
}
