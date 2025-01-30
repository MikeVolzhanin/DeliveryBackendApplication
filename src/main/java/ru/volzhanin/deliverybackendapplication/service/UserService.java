package ru.volzhanin.deliverybackendapplication.service;

import lombok.AllArgsConstructor;
import ru.volzhanin.deliverybackendapplication.entity.User;
import org.springframework.stereotype.Service;
import ru.volzhanin.deliverybackendapplication.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> allUsers() {
        return new ArrayList<>(userRepository.findAll());
    }
}
