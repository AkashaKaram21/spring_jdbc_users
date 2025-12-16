package com.example.accesdades.ra2.ac1.controler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.accesdades.ra2.ac1.model.User;
import com.example.accesdades.ra2.ac1.repository.UserRepository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setDataCreated(now);
        user.setDataUpdated(now);

        userRepository.save(user);
        return ResponseEntity.ok("Usuario creado correctamente");
    }

    @GetMapping("users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users == null || users.isEmpty()) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(users);
        }
    }

    @GetMapping("users/{user_id}")
    public ResponseEntity<List<User>> getUserById(@PathVariable long user_id) {
        List<User> users = userRepository.findUserById(user_id);

        if (users == null || users.isEmpty()) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(users);
        }
    }

    @PutMapping("users/{user_id}")
    public ResponseEntity<String> updateUser(
            @PathVariable long user_id,
            @RequestBody User user) {

        int rowsUpdated = userRepository.update(user_id, user);

        if (rowsUpdated == 0) {
            return ResponseEntity.ok("Usuari amb id " + user_id + " no trobat");
        }

        return ResponseEntity.ok("Informació de l'usuari actualitzada correctament");
    }

    @PatchMapping("users/{user_id}/name")
    public ResponseEntity<List<User>> patchUserName(
            @PathVariable long user_id,
            @RequestParam String name) {

        int rows = userRepository.patch(user_id, name);

        if (rows == 0) {
            return ResponseEntity.ok(null);
        }

        List<User> updatedUser = userRepository.findUserById(user_id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("users/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable long user_id) {

        int rows = userRepository.delete(user_id);

        if (rows == 0) {
            return ResponseEntity.ok("Usuari amb id " + user_id + " no trobat");
        }

        return ResponseEntity.ok("Usuari eliminat correctament");
    }
}
