package demo.model;

public class SpeciesDTO {
    private String id;
    private String name;
    private String classification;
    private String designation;
    private Integer averageHeight;    // changed to Integer
    private Integer averageLifespan;  // changed to Integer

    public static SpeciesDTO fromEntity(Species s) {
        SpeciesDTO dto = new SpeciesDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setClassification(s.getClassification());
        dto.setDesignation(s.getDesignation());
        
        // direct Integer mapping (no String conversion)
        dto.setAverageHeight(s.getAverageHeight());
        dto.setAverageLifespan(s.getAverageLifespan());
        
        return dto;
    }

    // --- Getters & Setters ---
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

    public Integer getAverageLifespan() { return averageLifespan; }
    public void setAverageLifespan(Integer averageLifespan) { this.averageLifespan = averageLifespan; }
}
