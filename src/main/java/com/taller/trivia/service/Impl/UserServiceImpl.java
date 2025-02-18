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
    public Optional<UserDTO> getByName(String name) {
        try {
            return repository.findByName(name)
                             .map(this::userToDTO);
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Override
    public Optional<UserDTO> getByEmail(String mail) {
        try {
            return repository.findByEmail(mail)
                             .map(this::userToDTO);
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
    public UserDTO updatePass(UserDTO userDto, String oldPass, String newPass) {
        try {
            if (!oldPass.isBlank() && !newPass.isBlank()) {
                if (this.validateUserPass(userDto, oldPass)) {
                    User user = this.DTOToUser(userDto);
                    String encodedPass = passwordEncoder.encode(newPass);

                    user.setPassword(encodedPass);
                    user = repository.save(user);
                    return this.userToDTO(user);
                } else {
                    throw new BusinessException(ErrorMessageLoader.getMessage("AUTH_BAD_CREDENTIALS"));
                }
            } else {
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED_MULT", "antigua contraseña y nueva contraseña"));
            }
        } catch (BusinessException e) {
            throw e; // La excepción de validación se lanza tal cual
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    @Override
    public boolean validateUserPass(UserDTO userDto, String pass) {
        try {
            if (userDto.getEmail().isBlank() && userDto.getName().isBlank()) {
                throw new BusinessException(ErrorMessageLoader.getMessage("VALIDATION_REQUIRED", "usuario o email"));
            }

            String userPass = "";
            if (!userDto.getEmail().isBlank()) {
                userPass = this.repository.findByEmail(userDto.getEmail())
                                           .orElseThrow(() -> new ServiceException(ErrorMessageLoader.getMessage("USER_NOT_FOUND")))
                                           .getPassword();
            } else if (!userDto.getName().isBlank()) {
                userPass = this.repository.findByName(userDto.getName())
                                           .orElseThrow(() -> new ServiceException(ErrorMessageLoader.getMessage("USER_NOT_FOUND")))
                                           .getPassword();
            }

            return this.passwordEncoder.matches(pass, userPass);
        } catch (BusinessException e) {
            throw e; // La excepción de validación se lanza tal cual
        } catch (Exception e) {
            throw new ServiceException(ErrorMessageLoader.getMessage("DATABASE_QUERY_ERROR"));
        }
    }

    // Métodos de soporte
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
        user.setPassword(userDto.getPassword());
        return user;
    }
}