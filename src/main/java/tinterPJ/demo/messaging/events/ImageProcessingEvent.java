package tinterPJ.demo.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageProcessingEvent implements Serializable {

    private Long userId;
    private String imageUrl;
    private String imagePath;
    private String imageType;
}
