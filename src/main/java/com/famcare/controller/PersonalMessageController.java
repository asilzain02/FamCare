package com.famcare.controller;

import com.famcare.model.PersonalMessage;
import com.famcare.model.User;
import com.famcare.repository.UserRepository;
import com.famcare.service.PersonalMessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class PersonalMessageController {

    @Autowired
    private PersonalMessageService messageService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get logged-in user ID from session
     */
    private Integer getLoggedInUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId != null ? (Integer) userId : null;
    }

    /**
     * Check if user is logged in
     */
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    /**
     * Show inbox (received messages)
     */
    @GetMapping("/inbox")
    public String showInbox(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer userId = getLoggedInUserId(session);
        List<PersonalMessage> messages = messageService.getInboxMessages(userId);
        PersonalMessageService.MessageStats stats = messageService.getMessageStats(userId);

        model.addAttribute("messages", messages);
        model.addAttribute("stats", stats);
        model.addAttribute("currentView", "inbox");

        return "messages/inbox";
    }

    /**
     * Show sent messages
     */
    @GetMapping("/sent")
    public String showSent(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer userId = getLoggedInUserId(session);
        List<PersonalMessage> messages = messageService.getSentMessages(userId);
        PersonalMessageService.MessageStats stats = messageService.getMessageStats(userId);

        model.addAttribute("messages", messages);
        model.addAttribute("stats", stats);
        model.addAttribute("currentView", "sent");

        return "messages/sent";
    }

    /**
     * Show compose message form
     */
    @GetMapping("/compose")
    public String showCompose(
            @RequestParam(required = false) Integer replyTo,
            HttpSession session, 
            Model model) {
        
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer userId = getLoggedInUserId(session);

        // Get family members for recipient selection
        List<User> familyMembers = getFamilyMembers(userId);
        model.addAttribute("familyMembers", familyMembers);

        // If replying to a message, pre-fill data
        if (replyTo != null) {
            Optional<PersonalMessage> originalMessage = messageService.getMessageById(replyTo);
            if (originalMessage.isPresent() && messageService.canAccessMessage(replyTo, userId)) {
                PersonalMessage msg = originalMessage.get();
                model.addAttribute("replyTo", replyTo);
                model.addAttribute("recipientId", msg.getSenderId());
                model.addAttribute("subject", "Re: " + msg.getSubject());
            }
        }

        return "messages/compose";
    }

    /**
     * Send a message
     */
    @PostMapping("/send")
    public String sendMessage(
            @RequestParam Integer receiverId,
            @RequestParam String subject,
            @RequestParam String messageBody,
            @RequestParam(required = false) Integer replyTo,
            HttpSession session,
            Model model) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer userId = getLoggedInUserId(session);

        try {
            messageService.sendMessage(userId, receiverId, subject, messageBody, replyTo);
            return "redirect:/messages/sent?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Error sending message: " + e.getMessage());
            model.addAttribute("familyMembers", getFamilyMembers(userId));
            return "messages/compose";
        }
    }

    /**
     * View a specific message
     */
    @GetMapping("/view/{id}")
    public String viewMessage(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer userId = getLoggedInUserId(session);

        Optional<PersonalMessage> messageOpt = messageService.getMessageById(id);
        
        if (messageOpt.isEmpty() || !messageService.canAccessMessage(id, userId)) {
            model.addAttribute("error", "Message not found or access denied");
            return "redirect:/messages/inbox";
        }

        PersonalMessage message = messageOpt.get();

        // Mark as read if user is receiver and message is unread
        if (message.getReceiverId().equals(userId) && !message.getIsRead()) {
            try {
                messageService.markAsRead(id, userId);
                message.setIsRead(true); // Update local object
            } catch (Exception e) {
                // Ignore marking errors
            }
        }

        // Get conversation thread if this is a reply
        List<PersonalMessage> thread = null;
        if (message.getParentMessageId() != null) {
            thread = messageService.getConversationThread(message.getParentMessageId());
        }

        model.addAttribute("message", message);
        model.addAttribute("thread", thread);
        model.addAttribute("currentUserId", userId);

        return "messages/view";
    }

    /**
     * Delete a message
     */
    @PostMapping("/delete/{id}")
    public String deleteMessage(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "inbox") String returnTo,
            HttpSession session,
            Model model) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer userId = getLoggedInUserId(session);

        try {
            messageService.deleteMessage(id, userId);
            return "redirect:/messages/" + returnTo + "?deleted=true";
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting message: " + e.getMessage());
            return "redirect:/messages/" + returnTo;
        }
    }

    /**
     * API endpoint to get unread count
     */
    @GetMapping("/api/unread-count")
    @ResponseBody
    public UnreadCountResponse getUnreadCount(HttpSession session) {
        if (!isLoggedIn(session)) {
            return new UnreadCountResponse(0);
        }

        Integer userId = getLoggedInUserId(session);
        int count = messageService.getUnreadCount(userId);
        return new UnreadCountResponse(count);
    }

    /**
     * Get family members for recipient selection
     */
    private List<User> getFamilyMembers(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }

        User user = userOpt.get();
        List<User> familyMembers = new java.util.ArrayList<>();

        if ("PARENT".equalsIgnoreCase(user.getRole())) {
            // Parent can message their children
            familyMembers.addAll(userRepository.findChildrenByParentId(userId));
        } else if ("CHILD".equalsIgnoreCase(user.getRole())) {
            // Child can message parent and siblings
            Integer parentId = user.getParentId();
            if (parentId != null) {
                // Add parent
                userRepository.findById(parentId).ifPresent(familyMembers::add);
                // Add siblings
                List<User> siblings = userRepository.findChildrenByParentId(parentId);
                siblings.stream()
                    .filter(sibling -> !sibling.getId().equals(userId))
                    .forEach(familyMembers::add);
            }
        }

        return familyMembers;
    }

    /**
     * Helper class for JSON response
     */
    public static class UnreadCountResponse {
        private int count;

        public UnreadCountResponse(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }
}