package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.storage.ImageStorageService;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private ImageStorageService imageStorageService;

    @Transactional
    public User getUserById(Long id) {
        Optional<User> result = this.userRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        Iterable<User> iterable = this.userRepository.findAll();
        for(User user : iterable)
            result.add(user);
        return result;
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public boolean deleteUserWithImage(Long id) {
        return userRepository.findById(id).map(user -> {
            // 1) cancella la cartella dell’utente
            try {
                imageStorageService.deleteDirectory("users/" + id);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image directory for user " + id, e);
            }

            // 2) elimina manualmente le credentials collegate
            credentialsService.deleteCredentialsByUserId(id);

            // 3) elimina l’utente
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }


    
    /**
     * Aggiorna i dati di User + Credentials e l’immagine (se presente).
     */
    @Transactional
    public User updateUserAccount(Long userId,
                                String name,
                                String surname,
                                String email,
                                LocalDate birth,
                                String newUsername,
                                String newPassword,
                                MultipartFile profileImage) throws IOException {
        // 1) carica entità
        User user = this.getUserById(userId);
        Credentials creds = credentialsService.findByUserId(userId)
            .orElseThrow(() -> new IllegalStateException("Credenziali non trovate"));

        // Memorizza il vecchio username prima della modifica (necessario per invalidare la cache)
        String oldUsername = creds.getUsername();

        // 2) aggiorna User
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setBirth(birth);

        // 3) aggiorna Immagine (se presente)
        if (profileImage != null && !profileImage.isEmpty()) {
            String subdir = "users/" + userId;
            imageStorageService.deleteDirectory(subdir);
            String path = imageStorageService.store(profileImage, subdir);
            Immagine img = Optional.ofNullable(user.getImage()).orElse(new Immagine());
            img.setPath(path);
            user.setImage(img);
        }
        userRepository.save(user);

        // 4) aggiorna Credentials via CredentialsService (gestisce hashing + cache-evict)
        creds.setUsername(newUsername);
        if (newPassword != null && !newPassword.isBlank()) {
            creds.setPassword(newPassword);  // verrà codificata dentro updateCredential()
        }

        // Invalida la cache usando sia oldUsername che newUsername
        credentialsService.updateCredential(creds, oldUsername);

        return user;
    }
}
