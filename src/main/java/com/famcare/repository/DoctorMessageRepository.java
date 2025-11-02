package com.famcare.repository;

import com.famcare.model.DoctorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class DoctorMessageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<DoctorMessage> messageRowMapper = (rs, rowNum) -> {
        DoctorMessage message = new DoctorMessage();
        message.setId(rs.getInt("id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setSenderName(rs.getString("sender_name"));
        message.setSenderRole(rs.getString("sender_role"));
        message.setReceiverId(rs.getInt("receiver_id"));
        message.setReceiverName(rs.getString("receiver_name"));
        message.setReceiverRole(rs.getString("receiver_role"));
        message.setSubject(rs.getString("subject"));
        message.setMessage(rs.getString("message"));
        message.setIsRead(rs.getBoolean("is_read"));
        message.setParentId(rs.getInt("parent_id") == 0 ? null : rs.getInt("parent_id"));
        message.setChildId(rs.getInt("child_id") == 0 ? null : rs.getInt("child_id"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            message.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp readTs = rs.getTimestamp("read_at");
        if (readTs != null) {
            message.setReadAt(readTs.toLocalDateTime());
        }
        
        return message;
    };

    public void save(DoctorMessage message) {
        String sql = "INSERT INTO doctor_messages (sender_id, sender_name, sender_role, " +
                    "receiver_id, receiver_name, receiver_role, subject, message, parent_id, child_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                message.getSenderId(),
                message.getSenderName(),
                message.getSenderRole(),
                message.getReceiverId(),
                message.getReceiverName(),
                message.getReceiverRole(),
                message.getSubject(),
                message.getMessage(),
                message.getParentId(),
                message.getChildId()
        );
    }

    public Optional<DoctorMessage> findById(Integer id) {
        String sql = "SELECT * FROM doctor_messages WHERE id = ?";
        try {
            DoctorMessage message = jdbcTemplate.queryForObject(sql, messageRowMapper, id);
            return Optional.of(message);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<DoctorMessage> findByReceiverId(Integer receiverId) {
        String sql = "SELECT * FROM doctor_messages WHERE receiver_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, messageRowMapper, receiverId);
    }

    public List<DoctorMessage> findBySenderId(Integer senderId) {
        String sql = "SELECT * FROM doctor_messages WHERE sender_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, messageRowMapper, senderId);
    }

    public List<DoctorMessage> findConversation(Integer userId1, Integer userId2) {
        String sql = "SELECT * FROM doctor_messages WHERE " +
                    "(sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                    "ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, messageRowMapper, userId1, userId2, userId2, userId1);
    }

    public void markAsRead(Integer messageId) {
        String sql = "UPDATE doctor_messages SET is_read = true, read_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, messageId);
    }

    public int countUnreadByReceiverId(Integer receiverId) {
        String sql = "SELECT COUNT(*) FROM doctor_messages WHERE receiver_id = ? AND is_read = false";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, receiverId);
        return count != null ? count : 0;
    }

    public List<DoctorMessage> findUnreadByReceiverId(Integer receiverId) {
        String sql = "SELECT * FROM doctor_messages WHERE receiver_id = ? AND is_read = false ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, messageRowMapper, receiverId);
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM doctor_messages WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}