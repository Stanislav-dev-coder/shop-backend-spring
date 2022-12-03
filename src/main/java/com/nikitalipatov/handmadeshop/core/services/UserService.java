package com.nikitalipatov.handmadeshop.core.services;
import com.nikitalipatov.handmadeshop.core.models.User;
import com.nikitalipatov.handmadeshop.core.repositories.UserRepository;
import com.nikitalipatov.handmadeshop.dto.UserDTO;
import com.nikitalipatov.handmadeshop.helpers.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUser(HttpServletRequest request) {
        String username = AuthHelper.getUsernameFromToken(request);
        return userRepository.findByUsername(username);
    }

    public Optional<User> modifyUser(HttpServletRequest request, UserDTO userDTO) {
        Optional<User> result = userRepository.findByUsername(AuthHelper.getUsernameFromToken(request));
        return result
                .map(e -> {
                    e.setName(userDTO.getName());
                    e.setSurname(userDTO.getSurname());
                    e.setAddress(userDTO.getAddress());
                    e.setPhoneNumber(userDTO.getPhoneNumber());
                    return userRepository.save(e);
                });
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
}

