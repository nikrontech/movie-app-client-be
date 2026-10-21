package lt.rildev.movieappclientbe.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final Path videosDirectory;
    private final VideoStreamingService videoStreamingService;

    public MediaService(@Value("${media.images-directory}") String imagesDirectory,
                        @Value("${media.videos-directory}") String videosDirectory,
                        VideoStreamingService videoStreamingService) {
        this.imagesDirectory = Path.of(imagesDirectory).toAbsolutePath().normalize();
        this.videosDirectory = Path.of(videosDirectory).toAbsolutePath().normalize();
        this.videoStreamingService = videoStreamingService;
    }

    public ResponseEntity<Resource> getImage(String filename) throws IOException {
        return getMedia(imagesDirectory, filename, "image/");
    }

    public void streamVideo(HttpServletRequest request,
                            HttpServletResponse response,
                            String filename) throws IOException {
        Path video = resolveMedia(videosDirectory, filename);
        if (video == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        videoStreamingService.stream(video, request, response);
    }

    private ResponseEntity<Resource> getMedia(Path directory, String filename, String expectedContentType)
            throws IOException {
        Path media = resolveMedia(directory, filename);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(media);
        if (contentType == null || !contentType.startsWith(expectedContentType)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(new FileSystemResource(media));
    }

    private Path resolveMedia(Path directory, String filename) throws IOException {
        Path media = directory.resolve(filename).normalize();
        if (!media.getParent().equals(directory)
                || !Files.isRegularFile(media)
                || !Files.isReadable(media)) {
            return null;
        }

        String contentType = Files.probeContentType(media);
        return contentType != null && contentType.startsWith("video/") == (directory.equals(videosDirectory))
                ? media
                : null;
    }

}
