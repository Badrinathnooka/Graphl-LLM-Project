package demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "species")
public class Species {
    @Id
    private String id;
    private String name;
    private String classification;
    private String designation;
    private Integer averageHeight;   // changed to Integer
    private List<String> hairColors;
    private List<String> skinColors;
    private List<String> eyeColors;
    private Integer averageLifespan; // changed to Integer
    private String language;
    private String homeworld;

    // --- getters and setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Integer getAverageHeight() { return averageHeight; }
    public void setAverageHeight(Integer averageHeight) { this.averageHeight = averageHeight; }

    public List<String> getHairColors() { return hairColors; }
    public void setHairColors(List<String> hairColors) { this.hairColors = hairColors; }

    public List<String> getSkinColors() { return skinColors; }
    public void setSkinColors(List<String> skinColors) { this.skinColors = skinColors; }

    public List<String> getEyeColors() { return eyeColors; }
    public void setEyeColors(List<String> eyeColors) { this.eyeColors = eyeColors; }

    public Integer getAverageLifespan() { return averageLifespan; }
    public void setAverageLifespan(Integer averageLifespan) { this.averageLifespan = averageLifespan; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getHomeworld() { return homeworld; }
    public void setHomeworld(String homeworld) { this.homeworld = homeworld; }
}
