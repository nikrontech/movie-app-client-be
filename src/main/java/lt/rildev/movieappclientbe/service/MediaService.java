package lt.rildev.movieappclientbe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MediaService {

    private final Path imagesDirectory;

    public MediaService(@Value("${media.images-directory}") String imagesDirectory) {
        this.imagesDirectory = Path.of(imagesDirectory).toAbsolutePath().normalize();
    }

    public ResponseEntity<Resource> getImage(String filename) throws IOException {
        Path image = imagesDirectory.resolve(filename).normalize();
        if (!image.getParent().equals(imagesDirectory)
                || !Files.isRegularFile(image)
                || !Files.isReadable(image)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(image);
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(new FileSystemResource(image));
    }
}
