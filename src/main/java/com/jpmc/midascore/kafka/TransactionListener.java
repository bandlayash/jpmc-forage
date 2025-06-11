package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    @Autowired
    private TransactionProcessingService transactionProcessingService;
    
    public void init() {
        logger.info("TransactionListener initialized and ready to receive messages");
    }

    @KafkaListener(topics = "${kafka.topic.transaction}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        logger.info("========== TRANSACTION RECEIVED ==========");
        logger.info("Received transaction: {}", transaction);
        logger.info("Amount: {}", transaction.getAmount());
        System.out.println("DEBUG: Received transaction: " + transaction);
        
        // Process the transaction through our service
        try {
            transactionProcessingService.processTransaction(transaction);
            logger.info("Transaction processed successfully");
        } catch (Exception e) {
            logger.error("Failed to process transaction: {}", e.getMessage(), e);
        }
        
        logger.info("========== TRANSACTION PROCESSING COMPLETE ==========");

        
    }
}