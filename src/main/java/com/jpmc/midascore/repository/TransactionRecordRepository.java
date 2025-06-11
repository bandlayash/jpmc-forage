package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface TransactionRecordRepository extends CrudRepository<TransactionRecord, Long> {
    
    // Find all transactions where a user is the sender
    List<TransactionRecord> findBySender(UserRecord sender);
    
    // Find all transactions where a user is the recipient
    List<TransactionRecord> findByRecipient(UserRecord recipient);
    
    // Find all transactions involving a specific user (either as sender or recipient)
    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.sender = :user OR tr.recipient = :user")
    List<TransactionRecord> findByUser(@Param("user") UserRecord user);
    
    // Find transactions by sender ID (useful for quick lookups)
    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.sender.id = :senderId")
    List<TransactionRecord> findBySenderId(@Param("senderId") long senderId);
    
    // Find transactions by recipient ID
    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.recipient.id = :recipientId")
    List<TransactionRecord> findByRecipientId(@Param("recipientId") long recipientId);
    
    // Count total transactions for a user
    @Query("SELECT COUNT(tr) FROM TransactionRecord tr WHERE tr.sender = :user OR tr.recipient = :user")
    Long countTransactionsByUser(@Param("user") UserRecord user);
}