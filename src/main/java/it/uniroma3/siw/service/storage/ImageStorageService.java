package it.uniroma3.siw.service.storage;

import it.uniroma3.siw.configuration.UploadProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
public class ImageStorageService {

    private final Path baseDir;

    public ImageStorageService(UploadProperties props) {
        this.baseDir = Paths.get(props.getBaseDir());
    }

    /**
     * Salva un file in una sottocartella della directory base (es. "authors", "books", ecc.)
     * e restituisce il path relativo da memorizzare in entity.
     *
     * @param file   il MultipartFile da salvare
     * @param subdir sottocartella all'interno di baseDir
     * @return path relativo (subdir/filename) o null se il file è vuoto
     * @throws IOException in caso di errori I/O
     */
    public String store(MultipartFile file, String subdir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String original = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + original;
        Path targetDir = baseDir.resolve(subdir);

        if (Files.notExists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        try (InputStream in = file.getInputStream()) {
            Path targetFile = targetDir.resolve(filename);
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return subdir + "/" + filename;
    }
}
