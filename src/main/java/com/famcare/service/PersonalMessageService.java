package com.famcare.service;

import com.famcare.model.PersonalMessage;
import com.famcare.model.User;
import com.famcare.repository.PersonalMessageRepository;
import com.famcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalMessageService {

    @Autowired
    private PersonalMessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Send a personal message
     */
    public void sendMessage(Integer senderId, Integer receiverId, String subject, String messageBody) {
        sendMessage(senderId, receiverId, subject, messageBody, null);
    }

    /**
     * Send a personal message (with optional parent for replies)
     */
    public void sendMessage(Integer senderId, Integer receiverId, String subject, String messageBody, Integer parentMessageId) {
        // Validate input
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
        if (messageBody == null || messageBody.trim().isEmpty()) {
            throw new IllegalArgumentException("Message body cannot be empty");
        }

        // Get sender details
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty()) {
            throw new IllegalArgumentException("Sender not found");
        }
        User sender = senderOpt.get();

        // Get receiver details
        Optional<User> receiverOpt = userRepository.findById(receiverId);
        if (receiverOpt.isEmpty()) {
            throw new IllegalArgumentException("Receiver not found");
        }
        User receiver = receiverOpt.get();

        // Create message
        PersonalMessage message = new PersonalMessage(
            senderId,
            sender.getUsername(),
            sender.getFullName(),
            receiverId,
            receiver.getUsername(),
            receiver.getFullName(),
            subject.trim(),
            messageBody.trim()
        );

        message.setParentMessageId(parentMessageId);

        messageRepository.save(message);
    }

    /**
     * Get inbox messages for a user
     */
    public List<PersonalMessage> getInboxMessages(Integer userId) {
        return messageRepository.findInboxByUserId(userId);
    }

    /**
     * Get sent messages for a user
     */
    public List<PersonalMessage> getSentMessages(Integer userId) {
        return messageRepository.findSentByUserId(userId);
    }

    /**
     * Get a specific message by ID
     */
    public Optional<PersonalMessage> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    /**
     * Mark message as read
     */
    public void markAsRead(Integer messageId, Integer userId) {
        Optional<PersonalMessage> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        PersonalMessage message = messageOpt.get();
        
        // Only receiver can mark as read
        if (!message.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        if (!message.getIsRead()) {
            messageRepository.markAsRead(messageId);
        }
    }

    /**
     * Delete a message
     */
    public void deleteMessage(Integer messageId, Integer userId) {
        Optional<PersonalMessage> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        PersonalMessage message = messageOpt.get();
        
        // User can delete if they're sender or receiver
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        messageRepository.deleteById(messageId);
    }

    /**
     * Get unread message count
     */
    public int getUnreadCount(Integer userId) {
        return messageRepository.countUnreadByUserId(userId);
    }

    /**
     * Get conversation thread
     */
    public List<PersonalMessage> getConversationThread(Integer messageId) {
        return messageRepository.findConversationThread(messageId);
    }

    /**
     * Get conversation between two users
     */
    public List<PersonalMessage> getConversationBetweenUsers(Integer user1Id, Integer user2Id) {
        return messageRepository.findConversationBetweenUsers(user1Id, user2Id);
    }

    /**
     * Get message statistics
     */
    public MessageStats getMessageStats(Integer userId) {
        MessageStats stats = new MessageStats();
        stats.totalSent = messageRepository.countSentByUserId(userId);
        stats.totalReceived = messageRepository.countReceivedByUserId(userId);
        stats.unreadCount = messageRepository.countUnreadByUserId(userId);
        return stats;
    }

    /**
     * Check if user can access message
     */
    public boolean canAccessMessage(Integer messageId, Integer userId) {
        Optional<PersonalMessage> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            return false;
        }

        PersonalMessage message = messageOpt.get();
        return message.getSenderId().equals(userId) || message.getReceiverId().equals(userId);
    }

    /**
     * Inner class for message statistics
     */
    public static class MessageStats {
        public int totalSent;
        public int totalReceived;
        public int unreadCount;

        public int getTotalSent() { return totalSent; }
        public int getTotalReceived() { return totalReceived; }
        public int getUnreadCount() { return unreadCount; }
    }
}