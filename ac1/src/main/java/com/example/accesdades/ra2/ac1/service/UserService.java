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

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public int addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findUserById(long id) {
        return userRepository.findUserById(id);
    }

    public int update(long id, User user) {
        return userRepository.update(id, user);
    }

    public int patch(long id, String newName) {
        return userRepository.patch(id, newName);
    }

    public int delete(long id) {
        return userRepository.delete(id);
    }

    public String saveUserImage(long userId, MultipartFile imageFile) throws Exception {

        List<User> user = userRepository.findUserById(userId);
        if (user.isEmpty()) throw new Exception("Usuari amb id " + userId + " no trobat");

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

        return imagePath;
    }

    public int saveUserCsv(MultipartFile csvFile) {

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

        } catch (Exception e) {
            return -1;
        }

        return counter;
    }

    public int saveUserJson(MultipartFile jsonFile) {

        int counter = 0;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            JsonNode root = mapper.readTree(jsonFile.getInputStream());
            JsonNode dataNode = root.path("data");
            JsonNode controlNode = dataNode.path("control");

            if (!controlNode.asText().equals("OK")) return -1;

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

        } catch (Exception e) {
            return -2;
        }

        return counter;
    }
}
