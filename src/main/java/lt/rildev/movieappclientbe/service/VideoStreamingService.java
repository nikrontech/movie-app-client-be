package lt.rildev.movieappclientbe.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class VideoStreamingService {

    public void stream(Path video,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        long fileLength = Files.size(video);
        long start = 0;
        long end = fileLength - 1;
        String range = request.getHeader("Range");

        if (range != null && range.startsWith("bytes=")) {
            String[] values = range.substring("bytes=".length()).split("-", 2);
            try {
                start = Long.parseLong(values[0]);
                if (values.length > 1 && !values[1].isBlank()) {
                    end = Long.parseLong(values[1]);
                }
            } catch (NumberFormatException exception) {
                sendInvalidRange(response, fileLength);
                return;
            }

            if (start < 0 || start >= fileLength || end < start) {
                sendInvalidRange(response, fileLength);
                return;
            }

            end = Math.min(end, fileLength - 1);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }

        long contentLength = end - start + 1;
        response.setBufferSize(8192);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        if (range != null) {
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        }
        response.setContentType(Files.probeContentType(video));

        try (InputStream input = Files.newInputStream(video);
             OutputStream output = response.getOutputStream()) {
            input.skipNBytes(start);
            copy(input, output, contentLength);
        }
    }

    private void sendInvalidRange(HttpServletResponse response, long fileLength) throws IOException {
        response.setHeader("Content-Range", "bytes */" + fileLength);
        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    private void copy(InputStream input, OutputStream output, long length) throws IOException {
        byte[] buffer = new byte[8192];
        long remaining = length;
        while (remaining > 0) {
            int read = input.read(buffer, 0, (int) Math.min(buffer.length, remaining));
            if (read == -1) break;
            output.write(buffer, 0, read);
            remaining -= read;
        }
    }
}
