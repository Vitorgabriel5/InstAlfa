package tinterPJ.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${upload.dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID()+ "."+file.getOriginalFilename();

            Path path = Paths.get(uploadDir);

            if(!Files.exists(path)){
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/"+fileName;
        }
        catch (Exception e) {
            throw new RuntimeException("Error saving file");
        }
    }
}
