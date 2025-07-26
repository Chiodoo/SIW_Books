package it.uniroma3.siw.service.storage.exception;

public class UnsupportedFileTypeException extends FileValidationException {
    public UnsupportedFileTypeException(String type) {
        super("Formato non supportato: " + type);
    }
}
