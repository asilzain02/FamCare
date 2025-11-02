package com.famcare.model;

import java.time.LocalDateTime;

public class PersonalMessage {
    private Integer id;
    private Integer senderId;
    private String senderUsername;
    private String senderFullName;
    private Integer receiverId;
    private String receiverUsername;
    private String receiverFullName;
    private String subject;
    private String messageBody;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Integer parentMessageId; // For threading/replies

    // Constructors
    public PersonalMessage() {
        this.isRead = false;
    }

    public PersonalMessage(Integer senderId, String senderUsername, String senderFullName,
                          Integer receiverId, String receiverUsername, String receiverFullName,
                          String subject, String messageBody) {
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.senderFullName = senderFullName;
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
        this.receiverFullName = receiverFullName;
        this.subject = subject;
        this.messageBody = messageBody;
        this.isRead = false;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public void setSenderFullName(String senderFullName) {
        this.senderFullName = senderFullName;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getReceiverFullName() {
        return receiverFullName;
    }

    public void setReceiverFullName(String receiverFullName) {
        this.receiverFullName = receiverFullName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Integer getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(Integer parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    @Override
    public String toString() {
        return "PersonalMessage{" +
                "id=" + id +
                ", from='" + senderFullName + '\'' +
                ", to='" + receiverFullName + '\'' +
                ", subject='" + subject + '\'' +
                ", isRead=" + isRead +
                ", sentAt=" + sentAt +
                '}';
    }
}