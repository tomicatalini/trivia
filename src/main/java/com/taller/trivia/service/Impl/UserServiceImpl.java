package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.taller.trivia.dao.UserDao;
import com.taller.trivia.dto.UserDTO;
import com.taller.trivia.model.User;
import com.taller.trivia.service.UserService;

public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao repository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<UserDTO> getAll() {
        return repository.findAll().stream()
                         .map(this::userToDTO)
                         .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getById(Long id) {
        return repository.findById(id)
                         .map(this::userToDTO);
    }

    @Override
    public Optional<UserDTO> getByName(String name) {
        return repository.findByName(name)
                         .map(this::userToDTO);
    }

    @Override
    public Optional<UserDTO> getByEmail(String mail) {
        return repository.findByEmail(mail)
                         .map(this::userToDTO);
    }

    @Override
    public UserDTO save(UserDTO userDto) {
        
        if (userDto == null) {
            throw new IllegalArgumentException("El objeto userDto no puede ser nulo");
        }
        
        if (userDto.getName() == null || userDto.getEmail() == null || userDto.getPassword() == null) {
            throw new IllegalArgumentException("Los campos 'name', 'email' y 'password' son obligatorios");
        }

        User user = this.DTOToUser(userDto);
        String encodedPass = passwordEncoder.encode(userDto.getPassword());

        user.setPassword(encodedPass);
        user = repository.save(user);
        return this.userToDTO(user);
    }

    @Override
    public UserDTO update(Long userId, UserDTO userDto) {
        User user = this.repository.update(userId, this.DTOToUser(userDto));        
        return this.userToDTO(user);
    };

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public UserDTO updatePass(UserDTO userDto, String oldPass, String newPass) {
        if (!oldPass.isBlank() && !newPass.isBlank()) {
            
            if (this.validateUserPass(userDto, newPass)) {
                User user = this.DTOToUser(userDto);
                String encodedPass = passwordEncoder.encode(userDto.getPassword());

                user.setPassword(encodedPass);
                user = repository.save(user);
                return this.userToDTO(user);
            } else {
                throw new RuntimeException("Invalid user");
            }

        } else {
            throw new RuntimeException("Both or any password are blank");
        }
    };

    @Override
    public Boolean validateUserPass(UserDTO userDto, String pass) {
        User user;

        if (!userDto.getEmail().isEmpty()) {
            user = this.repository.findByEmail(userDto.getEmail()).orElseThrow( () -> new RuntimeException("User not found"));
        } else if(!userDto.getName().isEmpty()) {
            user = this.repository.findByName(userDto.getName()).orElseThrow( () -> new RuntimeException("User not found"));
        } else {
            throw new RuntimeException("User not found");
        }
        
        return this.passwordEncoder.matches(pass, user.getPassword());
    };

    //Metodos de soporte
    public UserDTO userToDTO(User user) {
        UserDTO userDto = new UserDTO();
        
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRol(user.getRol());
        
        return userDto;
    }

    public User DTOToUser(UserDTO userDto) {
        User user = new User();
        
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRol(userDto.getRol());
        
        return user;
    }
}
