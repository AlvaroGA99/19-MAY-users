package com.ironhack.users_micro.service;

import com.ironhack.users_micro.dto.AccountDTO;
import com.ironhack.users_micro.dto.UserResponseDTO;
import com.ironhack.users_micro.exception.UserNotFoundException;
import com.ironhack.users_micro.model.User;
import com.ironhack.users_micro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO getUserById(long id){
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isPresent()){
            AccountDTO accountDTO = restTemplate.getForObject("http://accounts-micro/api/account/" + optionalUser.get().getAccountID(), AccountDTO.class);

            UserResponseDTO response = new UserResponseDTO();
            response.setId(optionalUser.get().getId());
            response.setAccountID(optionalUser.get().getAccountID());
            response.setUsername(optionalUser.get().getUsername());
            response.setAccountDTO(accountDTO);

            return response;
        }else{
            throw new UserNotFoundException("The user was not found");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User patchAccountId(Long userId, Long accountId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User foundUser;
        if (optionalUser.isPresent()) {
            foundUser = optionalUser.get();

            foundUser.setAccountID(accountId);
            userRepository.save(foundUser);
            return  foundUser;

        }else{
            throw new UserNotFoundException("Couldn't find the user");
        }

    }
}
