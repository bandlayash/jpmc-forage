package com.jpmc.midascore.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        
        // 4. Update balances
        sender.setBalance(sender.getBalance() - transactionAmount);
        recipient.setBalance(recipient.getBalance() + transactionAmount);
        
        // 5. Save users
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // 6. Record transaction
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(transaction.getAmount());
        record.setTimestamp(LocalDateTime.now());
        
        transactionRecordRepository.save(record);

        // Add this at the end of processTransaction method
        // Debug: Check Waldorf's balance after transaction
        if ("waldorf".equals(sender.getName())) {
            System.out.println("WALDORF BALANCE UPDATE: " + sender.getBalance());
        } else if ("waldorf".equals(recipient.getName())) {
            System.out.println("WALDORF BALANCE UPDATE: " + recipient.getBalance());
        }
    }
}