package com.example.accesdades.ra2.ac1.controler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.accesdades.ra2.ac1.model.User;
import com.example.accesdades.ra2.ac1.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.ok("Usuario creado correctamente");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        if(users.isEmpty()){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<List<User>> getUserById(@PathVariable long user_id) {
        List<User> users = userService.findUserById(user_id);
        if(users.isEmpty()){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable long user_id, @RequestBody User user) {
        int updated = userService.update(user_id, user);
        if(updated == 0){
            return ResponseEntity.ok("Usuari amb id " + user_id + " no trobat");
        }
        return ResponseEntity.ok("Información de usuario actualizada correctamente");
    }

    @PatchMapping("/users/{user_id}/name")
    public ResponseEntity<List<User>> patchUserName(@PathVariable long user_id, @RequestParam String name) {
        int patched = userService.patch(user_id, name);
        if(patched == 0){
            return ResponseEntity.ok(null);
        }
        List<User> updatedUser = userService.findUserById(user_id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable long user_id) {
        int deleted = userService.delete(user_id);
        if(deleted == 0){
            return ResponseEntity.ok("Usuari amb id " + user_id + " no trobat");
        }
        return ResponseEntity.ok("Usuari eliminat correctament");
    }

    @PostMapping("/users/{user_id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable long user_id, @RequestParam("imageFile") MultipartFile imageFile) {
        try{
            String imgUrl = userService.saveUserImage(user_id, imageFile);
            return ResponseEntity.ok(imgUrl);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/users/upload-csv")
    public ResponseEntity<String> postCsv(@RequestParam MultipartFile csvFile ) {
        if(csvFile.isEmpty()){
            return ResponseEntity.badRequest().body("El archivo CSV está vacío");
        }
        try{
            int totalReg = userService.saveUserCsv(csvFile);
            return ResponseEntity.ok("Número de usuarios insertados: "+ totalReg);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    @PostMapping("/users/upload-json")
    public ResponseEntity<String> postJson(@RequestParam MultipartFile jsonFile) {
        if(jsonFile.isEmpty()){
            return ResponseEntity.badRequest().body("El archivo JSON está vacío");
        }
        try{
            int result = userService.saveUserJson(jsonFile);

            if(result == -1){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Control incorrecto en el JSON (control != \"OK\")");
            } else if(result == -2){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error leyendo o parseando el JSON");
            } else if(result == -3){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El campo count no coincide con el número de usuarios en el JSON");
            } else {
                return ResponseEntity.ok("Registros añadidos: " + result);
            }

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al procesar el archivo JSON");
        }
    }
}