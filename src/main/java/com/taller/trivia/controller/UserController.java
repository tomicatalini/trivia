package com.taller.trivia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.trivia.dto.LoginDTO;
import com.taller.trivia.dto.UserDTO;
import com.taller.trivia.exception.BusinessException;
import com.taller.trivia.service.UserService;
import com.taller.trivia.util.ErrorMessageLoader;
import com.taller.trivia.util.ResponseHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAll();
            return ResponseHandler.handleResponse(users);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        ErrorMessageLoader.getMessage("SERVER_ERROR"),
                                                        e.getMessage());
        }
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return userService.getById(id)
                             .<ResponseEntity<?>>map(ResponseHandler::handleResponse)
                             .orElseGet(() -> ResponseHandler.handleErrorResponse(
                                    HttpStatus.NOT_FOUND, 
                                    ErrorMessageLoader.getMessage("USER_NOT_FOUND", "ID", id), 
                                    ""
                                ));
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessageLoader.getMessage("SERVER_ERROR"),
                                    e.getMessage());
        }
    }

    // Obtener usuario por nombre
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        try {
            List<UserDTO> users = userService.getByName(name);
            if (users.isEmpty()) {
                return ResponseHandler.handleErrorResponse(
                    HttpStatus.NOT_FOUND, 
                    ErrorMessageLoader.getMessage("USER_NOT_FOUND", "NOMBRE", name),
                    "" 
                );
            }
            return ResponseHandler.handleResponse(users);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessageLoader.getMessage("SERVER_ERROR"),
                                    e.getMessage());
        }
    }

    // Obtener usuario por email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            List<UserDTO> users = userService.getByEmail(email);
            if (users.isEmpty()) {
                return ResponseHandler.handleErrorResponse(
                    HttpStatus.NOT_FOUND, 
                    ErrorMessageLoader.getMessage("USER_NOT_FOUND", "MAIL", email),
                    "" 
                );
            }
            return ResponseHandler.handleResponse(users);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessageLoader.getMessage("SERVER_ERROR"),
                                    e.getMessage());
        }
    }

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDto) {
        try {
            UserDTO user = userService.save(userDto, userDto.getPassword());
            return ResponseHandler.handleResponse(user);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"),
                                    e.getMessage());
        }
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO userDto) {
        try {
            UserDTO user = userService.update(id, userDto);
            return ResponseHandler.handleResponse(user);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"),
                                    e.getMessage());
        }
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            boolean result = userService.delete(id);
            return ResponseHandler.handleResponse(result);
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"),
                                    e.getMessage());
        }
    }

    // Validar credenciales de usuario
    @PostMapping("/validate")
    public ResponseEntity<?> validateUser(@RequestBody LoginDTO login) {
        try {
            boolean isValid = userService.validateUserPass(login.getUsername(), login.getPassword());
            return isValid 
                ? ResponseHandler.handleResponse(isValid)
                : ResponseHandler.handleErrorResponse(HttpStatus.UNAUTHORIZED,
                                    ErrorMessageLoader.getMessage("AUTH_BAD_CREDENTIALS"),
                                    "");
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessageLoader.getMessage("SERVER_ERROR"),
                                    e.getMessage());
        }
    }

    // Actualizar contraseña
    @PutMapping("/pass")
    public ResponseEntity<?> updatePassword(@RequestBody LoginDTO login) {        
        try {
            boolean result = userService.updatePass(login.getUsername(), login.getOldPassword(), login.getPassword());
            return ResponseHandler.handleResponse(result);
        } catch (BusinessException e) {
            return ResponseHandler.handleErrorResponse(
                                    HttpStatus.BAD_REQUEST,
                                    ErrorMessageLoader.getMessage("USER_PASSWORD_MISMATCH"),
                                    e.getMessage());
        } catch (Exception e) {
            return ResponseHandler.handleErrorResponse(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"),
                                    e.getMessage());
        }
    }
}