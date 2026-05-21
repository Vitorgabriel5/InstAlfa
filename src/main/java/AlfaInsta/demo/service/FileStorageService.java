package AlfaInsta.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${upload.dir}")
    private String uploadDir;

    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

    public String saveFile(MultipartFile file) {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = path.resolve(fileName);

            // redimensiona e comprime a imagem
            BufferedImage original = ImageIO.read(file.getInputStream());

            if (original != null) {
                BufferedImage resized = resizeImage(original);
                ImageIO.write(resized, "jpg", filePath.toFile());
            } else {
                // se não for imagem, salva o arquivo direto
                Files.copy(file.getInputStream(), filePath);
            }

            return "/api/files/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    private BufferedImage resizeImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        // se já é menor que o máximo, mantém
        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return convertToRGB(original);
        }

        // calcula nova dimensão mantendo proporção
        double ratio = Math.min(
                (double) MAX_WIDTH / width,
                (double) MAX_HEIGHT / height
        );

        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resized;
    }

    private BufferedImage convertToRGB(BufferedImage image) {
        if (image.getType() == BufferedImage.TYPE_INT_RGB) {
            return image;
        }
        BufferedImage rgb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.drawImage(image, 0, 0, Color.WHITE, null);
        g.dispose();
        return rgb;
    }
}