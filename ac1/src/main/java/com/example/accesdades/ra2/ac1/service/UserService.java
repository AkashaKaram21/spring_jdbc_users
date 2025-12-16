package com.example.accesdades.ra2.ac1.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.accesdades.ra2.ac1.logging.CustomLogging;
import com.example.accesdades.ra2.ac1.model.User;
import com.example.accesdades.ra2.ac1.repository.UserRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CustomLogging customLogging;

    public List<User> findAll() {
        customLogging.logInfo("UserService", "findAll", "Obtenint tots els usuaris");
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            customLogging.logError("UserService", "findAll", "Error obtenint tots els usuaris", e);
            throw e;
        }
    }

    public int save(User user) {
        customLogging.logInfo("UserService", "save", "Guardant nou usuari: " + user.getName());
        try {
            int result = userRepository.save(user);
            customLogging.logInfo("UserService", "save", "Usuari guardat correctament amb ID");
            return result;
        } catch (Exception e) {
            customLogging.logError("UserService", "save", "Error guardant l'usuari: " + user.getName(), e);
            throw e;
        }
    }

    public List<User> findUserById(long id) {
        customLogging.logInfo("UserService", "findUserById", "Buscant usuari amb ID: " + id);
        try {
            List<User> users = userRepository.findUserById(id);
            customLogging.logInfo("UserService", "findUserById", 
                users.isEmpty() ? "Usuari no trobat amb ID: " + id : "Usuari trobat amb ID: " + id);
            return users;
        } catch (Exception e) {
            customLogging.logError("UserService", "findUserById", "Error buscant usuari amb ID: " + id, e);
            throw e;
        }
    }

    public int update(long id, User user) {
        customLogging.logInfo("UserService", "update", "Actualitzant usuari amb ID: " + id);
        try {
            int result = userRepository.update(id, user);
            if (result > 0) {
                customLogging.logInfo("UserService", "update", "Usuari actualitzat correctament amb ID: " + id);
            } else {
                customLogging.logWarning("UserService", "update", "Usuari no trobat per actualitzar amb ID: " + id);
            }
            return result;
        } catch (Exception e) {
            customLogging.logError("UserService", "update", "Error actualitzant usuari amb ID: " + id, e);
            throw e;
        }
    }

    public int patch(long id, String newName) {
        customLogging.logInfo("UserService", "patch", "Actualitzant nom d'usuari amb ID: " + id + " a: " + newName);
        try {
            int result = userRepository.patch(id, newName);
            if (result > 0) {
                customLogging.logInfo("UserService", "patch", "Nom d'usuari actualitzat correctament per ID: " + id);
            } else {
                customLogging.logWarning("UserService", "patch", "Usuari no trobat per actualitzar nom amb ID: " + id);
            }
            return result;
        } catch (Exception e) {
            customLogging.logError("UserService", "patch", "Error actualitzant nom d'usuari amb ID: " + id, e);
            throw e;
        }
    }

    public int delete(long id) {
        customLogging.logInfo("UserService", "delete", "Eliminant usuari amb ID: " + id);
        try {
            int result = userRepository.delete(id);
            if (result > 0) {
                customLogging.logInfo("UserService", "delete", "Usuari eliminat correctament amb ID: " + id);
            } else {
                customLogging.logWarning("UserService", "delete", "Usuari no trobat per eliminar amb ID: " + id);
            }
            return result;
        } catch (Exception e) {
            customLogging.logError("UserService", "delete", "Error eliminant usuari amb ID: " + id, e);
            throw e;
        }
    }

    public String saveUserImage(long userId, MultipartFile imageFile) throws Exception {
        customLogging.logInfo("UserService", "saveUserImage", 
            "Guardant imatge per usuari ID: " + userId + ", fitxer: " + imageFile.getOriginalFilename());
        try {
            List<User> user = userRepository.findUserById(userId);
            if (user.isEmpty()) {
                customLogging.logWarning("UserService", "saveUserImage", 
                    "Usuari no trobat amb ID: " + userId + " per guardar imatge");
                throw new Exception("Usuario con id " + userId + " no encontrado");
            }

            Path imagesFolder = Paths.get("src/main/resources/public/images");
            Files.createDirectories(imagesFolder);

            String originalFileName = imageFile.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = "User_" + userId + extension;

            Path filePath = imagesFolder.resolve(newFileName);

            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String imagePath = "/images/" + newFileName;
            userRepository.updateImagePath(userId, imagePath);

            customLogging.logInfo("UserService", "saveUserImage", 
                "Imatge guardada correctament per usuari ID: " + userId + ", ruta: " + imagePath);
            
            return imagePath;
        } catch (Exception e) {
            customLogging.logError("UserService", "saveUserImage", 
                "Error guardant imatge per usuari ID: " + userId, e);
            throw e;
        }
    }

    public int saveUserCsv(MultipartFile csvFile) {
        customLogging.logInfo("UserService", "saveUserCsv", 
            "Processant fitxer CSV: " + csvFile.getOriginalFilename());
        
        int counter = 0;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {

            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {

                if (first) {
                    first = false;
                    continue;
                }

                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");

                if (data.length < 4) continue;

                User user = new User();
                user.setName(data[0]);
                user.setDescription(data[1]);
                user.setEmail(data[2]);
                user.setPassword(data[3]);
                user.setDataCreated(now);
                user.setDataUpdated(now);

                userRepository.save(user);
                counter++;
            }

            Path folder = Paths.get("src/main/resources/public/csv_processed");
            Files.createDirectories(folder);
            Files.copy(csvFile.getInputStream(),
                    folder.resolve(csvFile.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);

            customLogging.logInfo("UserService", "saveUserCsv", 
                "CSV processat correctament. Usuaris afegits: " + counter);
            
        } catch (Exception e) {
            customLogging.logError("UserService", "saveUserCsv", 
                "Error processant fitxer CSV: " + csvFile.getOriginalFilename(), e);
            return -1;
        }

        return counter;
    }

    public int saveUserJson(MultipartFile jsonFile) {
        customLogging.logInfo("UserService", "saveUserJson", 
            "Processant fitxer JSON: " + jsonFile.getOriginalFilename());
        
        int counter = 0;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            JsonNode root = mapper.readTree(jsonFile.getInputStream());
            JsonNode dataNode = root.path("data");
            JsonNode controlNode = dataNode.path("control");

            if (!controlNode.asText().equals("OK")) {
                customLogging.logWarning("UserService", "saveUserJson", 
                    "Control node no és OK en fitxer JSON: " + jsonFile.getOriginalFilename());
                return -1;
            }

            JsonNode usersNode = dataNode.path("users");
            for (JsonNode userNode : usersNode) {

                User user = new User();
                user.setName(userNode.path("name").asText());
                user.setDescription(userNode.path("description").asText());
                user.setEmail(userNode.path("email").asText());
                user.setPassword(userNode.path("password").asText());
                user.setDataCreated(now);
                user.setDataUpdated(now);

                userRepository.save(user);
                counter++;
            }

            Path folder = Paths.get("src/main/resources/public/json_processed");
            Files.createDirectories(folder);
            Files.copy(jsonFile.getInputStream(),
                    folder.resolve(jsonFile.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);

            customLogging.logInfo("UserService", "saveUserJson", 
                "JSON processat correctament. Usuaris afegits: " + counter);
            
        } catch (Exception e) {
            customLogging.logError("UserService", "saveUserJson", 
                "Error processant fitxer JSON: " + jsonFile.getOriginalFilename(), e);
            return -2;
        }

        return counter;
    }
}