package it.uniroma3.siw.service.storage.exception;

public class FileTooLargeException extends FileValidationException {
    public FileTooLargeException(long maxSize) {
        super("File troppo grande. Max consentito: " + (maxSize / 1_048_576) + " MB");
    }
}
