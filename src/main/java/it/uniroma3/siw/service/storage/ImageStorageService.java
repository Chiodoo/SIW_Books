package it.uniroma3.siw.service.storage;

import it.uniroma3.siw.configuration.UploadProperties;
import it.uniroma3.siw.service.storage.exception.FileTooLargeException;
import it.uniroma3.siw.service.storage.exception.UnsupportedFileTypeException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final long MAX_SIZE = 30 * 1_048_576; // 30 MB
    // aggiunto image/webp
    private static final List<String> ALLOWED_MIME = List.of(
        "image/png",
        "image/jpeg",
        "image/webp"
    );

    private final Path baseDir;
    private final Tika tika;

    public ImageStorageService(UploadProperties props) {
        this.baseDir = Paths.get(props.getBaseDir());
        this.tika = new Tika();
    }

    /**
     * Valida e salva l’immagine, restituendo il path relativo.
     */
    public String store(MultipartFile file, String subdir) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // 1) dimensione
        if (file.getSize() > MAX_SIZE) {
            throw new FileTooLargeException(MAX_SIZE);
        }

        // 2) tipo MIME reale
        String realMime;
        try (InputStream is = file.getInputStream()) {
            realMime = tika.detect(is);
        }
        if (!ALLOWED_MIME.contains(realMime)) {
            throw new UnsupportedFileTypeException(realMime);
        }

        // 3) genera filename con estensione basata sul MIME
        String ext;
        switch (realMime) {
            case "image/png":  ext = ".png";  break;
            case "image/jpeg": ext = ".jpg";  break;
            case "image/webp": ext = ".webp"; break;
            default:           ext = "";      break; // non dovrebbe capitare
        }
        String filename = UUID.randomUUID().toString() + ext;

        // 4) crea directory se non esiste
        Path targetDir = baseDir.resolve(subdir);
        if (Files.notExists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 5) salva il file
        try (InputStream in = file.getInputStream()) {
            Path targetFile = targetDir.resolve(filename);
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return subdir + "/" + filename;
    }
}
