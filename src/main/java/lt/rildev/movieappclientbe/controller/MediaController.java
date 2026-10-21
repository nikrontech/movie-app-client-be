package lt.rildev.movieappclientbe.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import lt.rildev.movieappclientbe.service.MediaService;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping(value = "/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        return mediaService.getImage(filename);
    }

    @GetMapping(value = "/video/{filename:.+}")
    public void streamVideo(HttpServletRequest request,
                            HttpServletResponse response,
                            @PathVariable String filename) throws IOException {
        mediaService.streamVideo(request, response, filename);
    }
}
