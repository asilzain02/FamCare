package com.famcare.model;

import java.time.LocalDateTime;

public class DoctorMessage {
    private Integer id;
    private Integer senderId;
    private String senderName;
    private String senderRole; // "PARENT", "CHILD", "DOCTOR"
    private Integer receiverId;
    private String receiverName;
    private String receiverRole;
    private String subject;
    private String message;
    private Boolean isRead;
    private Integer parentId; // For context - which family
    private Integer childId; // For context - which child (if applicable)
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // Constructors
    public DoctorMessage() {
    }

    public DoctorMessage(Integer senderId, String senderName, String senderRole,
                        Integer receiverId, String receiverName, String receiverRole,
                        String subject, String message, Integer parentId, Integer childId) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverRole = receiverRole;
        this.subject = subject;
        this.message = message;
        this.parentId = parentId;
        this.childId = childId;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getChildId() {
        return childId;
    }

    public void setChildId(Integer childId) {
        this.childId = childId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    @Override
    public String toString() {
        return "DoctorMessage{" +
                "id=" + id +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", subject='" + subject + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}