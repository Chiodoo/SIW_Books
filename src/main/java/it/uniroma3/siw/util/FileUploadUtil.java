package it.uniroma3.siw.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

/**
 * Classe di utilità per la gestione dell'upload dei file.
 * <p>
 * Fornisce un metodo per salvare un file caricato tramite {@link MultipartFile} in una directory specificata.
 * </p>
 */
public class FileUploadUtil {
    /**
    /**
     * Salva un file nella directory specificata con il nome file fornito.
     * <p>
     * Se la directory di upload non esiste, viene creata automaticamente.
     * </p>
     *
     * @param uploadDir   la directory di destinazione
     * @param fileName    il nome del file da salvare
     * @param multipartFile il file caricato tramite MultipartFile
     * @throws IOException se si verifica un errore durante il salvataggio del file
     */
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Errore nel salvataggio file: " + fileName, e);
        }
    }
}

//Un MultiPart file è un file caricato tramite un form HTML attraverso una  richiesta HTTP POST.
