package demo.model;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "characters")
public class CharacterD {
    @Id
    private String id;
    private String name;
    private Integer height;   // changed to Integer
    private Integer mass;     // changed to Integer
    private List<String> hairColors;
    private List<String> skinColors;
    private List<String> eyeColors;
    private String birthYear;
    private String gender;
    private String homeworld;
    private String species;

    // --- getters and setters ---
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

    public List<String> getSkinColors() { return skinColors; }
    public void setSkinColors(List<String> skinColors) { this.skinColors = skinColors; }

    public List<String> getEyeColors() { return eyeColors; }
    public void setEyeColors(List<String> eyeColors) { this.eyeColors = eyeColors; }

    public String getBirthYear() { return birthYear; }
    public void setBirthYear(String birthYear) { this.birthYear = birthYear; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getHomeworld() { return homeworld; }
    public void setHomeworld(String homeworld) { this.homeworld = homeworld; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
}
