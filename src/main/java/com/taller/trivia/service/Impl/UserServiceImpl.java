package com.taller.trivia.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.trivia.dao.UserDao;
import com.taller.trivia.dto.UserDTO;
import com.taller.trivia.exception.BusinessException;
import com.taller.trivia.exception.DatabaseException;
import com.taller.trivia.exception.ServiceException;
import com.taller.trivia.model.User;
import com.taller.trivia.service.UserService;
import com.taller.trivia.util.ErrorMessageLoader;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAll() {
        try {
            return repository.findAll().stream()
                             .map(this::userToDTO)
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Override
    public Optional<UserDTO> getById(Long id) {
        try {
            return repository.findById(id)
                             .map(this::userToDTO);
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Override
    public List<UserDTO> getByName(String name) {
        try {
            return repository.findByName(name)
                             .stream()
                             .map(this::userToDTO)
                             .collect(Collectors.toList());
        } catch (DatabaseException e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<UserDTO> getByEmail(String mail) {
        try {
            return repository.findByEmail(mail)
                             .stream()
                             .map(this::userToDTO)
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Transactional
    @Override
    public UserDTO save(UserDTO userDto, String password) {
        try {
            if (userDto == null || userDto.getName() == null || userDto.getEmail() == null) {
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED_MULT", "nombre, email"));
            }
            User user = this.DTOToUser(userDto);
            String encodedPass = passwordEncoder.encode(password);
            user.setPassword(encodedPass);
            user = repository.save(user);
            return this.userToDTO(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Transactional
    @Override
    public UserDTO update(Long userId, UserDTO userDto) {
        try {
            User user = this.repository.update(userId, this.DTOToUser(userDto));
            return this.userToDTO(user);
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        try {
            if (!repository.delete(id)) {
                throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Transactional
    @Override
    public boolean updatePass(String username, String oldPass, String newPass) {
        System.out.println("username: " + username);
        System.out.println("oldPass: " + oldPass);
        System.out.println("newPass: " + newPass);
        try {
            if (username.isBlank()) {
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED", "USUARIO"));
            }

            User user = this.repository.findByEmail(username)
                           .orElseGet(() -> 
                                this.repository.findByName(username)
                                               .orElseThrow( () -> new BusinessException(ErrorMessageLoader.getMessage("USER_NOT_FOUND", username))
                                               )
                           );

            if (!this.passwordEncoder.matches(oldPass, user.getPassword())) {
                throw new BusinessException(ErrorMessageLoader.getMessage("USER_PASSWORD_MISMATCH"));
            }
            
            user.setPassword(passwordEncoder.encode(newPass));
            user = repository.save(user);
            return user != null ? true : false;

        } catch (BusinessException e) {
            throw e; // La excepción de validación se lanza tal cual
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("SERVER_ERROR"));
        }
    }

    @Override
    public boolean validateUserPass(String username, String password) {
        try {
            if (username.isBlank()) {
                throw new BusinessException(ErrorMessageLoader.getMessage("USER_INVALID_CREDENTIALS"));
            }

            String userPass = this.repository.findByEmail(username)
                           .map(user -> user.getPassword())
                           .orElseGet(() -> 
                                this.repository.findByName(username)
                                               .map(user -> user.getPassword())
                                               .orElseThrow( () -> new BusinessException(ErrorMessageLoader.getMessage("USER_NOT_FOUND", "EMAL o NOMBRE", username))
                                               )
                           );

            return this.passwordEncoder.matches(password, userPass);
        } catch (BusinessException e) {
            throw e; // La excepción de validación se lanza tal cual
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    // Métodos de soporte
    public UserDTO userToDTO(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRol(user.getRol());
        return userDto;
    }

    public User DTOToUser(UserDTO userDto) {
        User user = new User();
        user.setId(user.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRol(userDto.getRol());
        user.setPassword(userDto.getPassword());
        return user;
    }
}