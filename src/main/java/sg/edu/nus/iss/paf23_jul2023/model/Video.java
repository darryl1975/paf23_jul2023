package sg.edu.nus.iss.paf23_jul2023.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    
    private Integer id;

    private String title;

    private String synopsis;

    private Integer availableCount;
}
