package com.jpmc.midascore.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.foundation.Transaction;

@Service
public class TransactionProcessingService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    // Inner class to handle incentive API response
    public static class Incentive {
        private float amount;
        
        public Incentive() {}
        
        public float getAmount() {
            return amount;
        }
        
        public void setAmount(float amount) {
            this.amount = amount;
        }
    }
    
    @Transactional
    public void processTransaction(Transaction transaction) {
        // 1. Validate sender exists
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) return;
        
        // 2. Validate recipient exists  
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) return;
        
        // Get transaction amount
        float transactionAmount = transaction.getAmount();
        
        // 3. Check sender balance
        if (sender.getBalance() < transactionAmount) {
            return;
        }
        
        // 4. Call incentive API to get incentive amount
        float incentiveAmount = 0;
        try {
            Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive", 
                transaction, 
                Incentive.class
            );
            if (incentive != null) {
                incentiveAmount = incentive.getAmount();
            }
        } catch (Exception e) {
            System.err.println("Failed to get incentive: " + e.getMessage());
            // Continue processing without incentive
        }
        
        // 5. Update balances
        sender.setBalance(sender.getBalance() - transactionAmount);
        recipient.setBalance(recipient.getBalance() + transactionAmount + incentiveAmount);
        
        // 6. Save users
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // 7. Record transaction with incentive
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(transaction.getAmount());
        record.setIncentive(incentiveAmount); // You'll need to add this field to TransactionRecord
        record.setTimestamp(LocalDateTime.now());
        
        transactionRecordRepository.save(record);
        
    
            
        
        
        // Debug: Check Waldorf's balance after any transaction involving Wilbur
        if ("wilbur".equals(sender.getName()) || "wilbur".equals(recipient.getName())) {
            UserRecord wilbur = "wilbur".equals(sender.getName()) ? sender : recipient;
            System.out.println("WILBUR BALANCE UPDATE: " + wilbur.getBalance());
        }
    }
}