package it.uniroma3.siw.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class UserService {



    @Value("${upload.base-dir}")
    private String uploadDir;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CredentialsRepository credentialsRepository;

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
            Path userDir = Paths.get(uploadDir, "users", id.toString());
            FileSystemUtils.deleteRecursively(userDir.toFile());

            // 2) elimina manualmente le credentials collegate
            credentialsRepository.deleteByUser_Id(id);

            // 3) elimina l’utente
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}
