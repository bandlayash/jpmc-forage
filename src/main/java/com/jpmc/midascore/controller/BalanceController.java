package com.jpmc.midascore.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.foundation.Balance;

@RestController
public class BalanceController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        Optional<UserRecord> userOptional = userRepository.findById(userId);
        
        if (userOptional.isPresent()) {
            UserRecord user = userOptional.get();
            return new Balance(user.getBalance());
        } else {
            // Return balance of 0 if user doesn't exist
            return new Balance(0.0f);
        }
    }
}