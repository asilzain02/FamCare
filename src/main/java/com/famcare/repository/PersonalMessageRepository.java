package com.famcare.repository;

import com.famcare.model.PersonalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonalMessageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper to convert database row to PersonalMessage object
    private RowMapper<PersonalMessage> messageRowMapper = (rs, rowNum) -> {
        PersonalMessage message = new PersonalMessage();
        message.setId(rs.getInt("id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setSenderUsername(rs.getString("sender_username"));
        message.setSenderFullName(rs.getString("sender_full_name"));
        message.setReceiverId(rs.getInt("receiver_id"));
        message.setReceiverUsername(rs.getString("receiver_username"));
        message.setReceiverFullName(rs.getString("receiver_full_name"));
        message.setSubject(rs.getString("subject"));
        message.setMessageBody(rs.getString("message_body"));
        message.setIsRead(rs.getBoolean("is_read"));
        
        Timestamp sentTs = rs.getTimestamp("sent_at");
        if (sentTs != null) {
            message.setSentAt(sentTs.toLocalDateTime());
        }
        
        Timestamp readTs = rs.getTimestamp("read_at");
        if (readTs != null) {
            message.setReadAt(readTs.toLocalDateTime());
        }

        Integer parentId = rs.getInt("parent_message_id");
        message.setParentMessageId(rs.wasNull() ? null : parentId);
        
        return message;
    };

    /**
     * Save a new personal message
     */
    public void save(PersonalMessage message) {
        String sql = "INSERT INTO personal_messages (sender_id, sender_username, sender_full_name, " +
                    "receiver_id, receiver_username, receiver_full_name, subject, message_body, parent_message_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                message.getSenderId(),
                message.getSenderUsername(),
                message.getSenderFullName(),
                message.getReceiverId(),
                message.getReceiverUsername(),
                message.getReceiverFullName(),
                message.getSubject(),
                message.getMessageBody(),
                message.getParentMessageId()
        );
    }

    /**
     * Find message by ID
     */
    public Optional<PersonalMessage> findById(Integer id) {
        String sql = "SELECT * FROM personal_messages WHERE id = ?";
        try {
            PersonalMessage message = jdbcTemplate.queryForObject(sql, messageRowMapper, id);
            return Optional.of(message);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get inbox messages for a user (received messages, newest first)
     */
    public List<PersonalMessage> findInboxByUserId(Integer userId) {
        String sql = "SELECT * FROM personal_messages WHERE receiver_id = ? ORDER BY sent_at DESC";
        return jdbcTemplate.query(sql, messageRowMapper, userId);
    }

    /**
     * Get sent messages for a user (newest first)
     */
    public List<PersonalMessage> findSentByUserId(Integer userId) {
        String sql = "SELECT * FROM personal_messages WHERE sender_id = ? ORDER BY sent_at DESC";
        return jdbcTemplate.query(sql, messageRowMapper, userId);
    }

    /**
     * Get unread message count for a user
     */
    public int countUnreadByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) FROM personal_messages WHERE receiver_id = ? AND is_read = false";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    /**
     * Mark message as read
     */
    public void markAsRead(Integer messageId) {
        String sql = "UPDATE personal_messages SET is_read = true, read_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, messageId);
    }

    /**
     * Delete a message
     */
    public void deleteById(Integer id) {
        String sql = "DELETE FROM personal_messages WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    /**
     * Get conversation thread (message and its replies)
     */
    public List<PersonalMessage> findConversationThread(Integer messageId) {
        String sql = "SELECT * FROM personal_messages WHERE id = ? OR parent_message_id = ? ORDER BY sent_at ASC";
        return jdbcTemplate.query(sql, messageRowMapper, messageId, messageId);
    }

    /**
     * Get messages between two users (conversation history)
     */
    public List<PersonalMessage> findConversationBetweenUsers(Integer user1Id, Integer user2Id) {
        String sql = "SELECT * FROM personal_messages " +
                    "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                    "ORDER BY sent_at ASC";
        return jdbcTemplate.query(sql, messageRowMapper, user1Id, user2Id, user2Id, user1Id);
    }

    /**
     * Count total sent messages
     */
    public int countSentByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) FROM personal_messages WHERE sender_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    /**
     * Count total received messages
     */
    public int countReceivedByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) FROM personal_messages WHERE receiver_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }
}