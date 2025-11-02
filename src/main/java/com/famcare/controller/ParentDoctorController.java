package com.famcare.controller;

import com.famcare.model.Doctor;
import com.famcare.model.DoctorMessage;
import com.famcare.model.User;
import com.famcare.service.AuthService;
import com.famcare.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/parent/doctors")
public class ParentDoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AuthService authService;

    private boolean isParentLoggedIn(HttpSession session) {
        return "PARENT".equalsIgnoreCase(String.valueOf(session.getAttribute("userRole")));
    }

    private Integer getLoggedInUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId != null ? (Integer) userId : null;
    }

    @GetMapping
    public String viewDoctors(HttpSession session, Model model) {
        if (!isParentLoggedIn(session)) {
            return "redirect:/login";
        }

        List<Doctor> doctors = doctorService.getAvailableDoctors();
        model.addAttribute("doctors", doctors);

        Integer userId = getLoggedInUserId(session);
        int unreadCount = doctorService.getUnreadMessageCount(userId);
        model.addAttribute("unreadCount", unreadCount);

        return "parent/doctors-list";
    }

    @GetMapping("/{id}")
    public String viewDoctorProfile(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isParentLoggedIn(session)) {
            return "redirect:/login";
        }

        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        
        if (doctor.isEmpty()) {
            model.addAttribute("error", "Doctor not found");
            return "redirect:/parent/doctors";
        }

        model.addAttribute("doctor", doctor.get());
        return "parent/doctor-profile";
    }

    @GetMapping("/{id}/message")
    public String messageForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isParentLoggedIn(session)) {
            return "redirect:/login";
        }

        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        
        if (doctor.isEmpty()) {
            model.addAttribute("error", "Doctor not found");
            return "redirect:/parent/doctors";
        }

        model.addAttribute("doctor", doctor.get());
        
        Integer parentId = getLoggedInUserId(session);
        List<User> children = authService.findChildrenByParentId(parentId);
        model.addAttribute("children", children);

        return "parent/message-doctor";
    }

    @PostMapping("/{id}/message")
    public String sendMessage(
            @PathVariable Integer id,
            @RequestParam String subject,
            @RequestParam String message,
            @RequestParam(required = false) Integer childId,
            HttpSession session,
            Model model) {

        if (!isParentLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer parentId = getLoggedInUserId(session);
        String parentName = (String) session.getAttribute("userName");

        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        
        if (doctor.isEmpty()) {
            model.addAttribute("error", "Doctor not found");
            return "redirect:/parent/doctors";
        }

        Doctor doc = doctor.get();

        doctorService.sendMessage(
                parentId,
                parentName,
                "PARENT",
                doc.getId(),
                doc.getFullName(),
                "DOCTOR",
                subject,
                message,
                parentId,
                childId
        );

        model.addAttribute("success", "Message sent successfully!");
        return "redirect:/parent/doctors/messages";
    }

    @GetMapping("/messages")
    public String viewMessages(HttpSession session, Model model) {
        if (!isParentLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer userId = getLoggedInUserId(session);

        List<DoctorMessage> receivedMessages = doctorService.getMessagesForUser(userId);
        List<DoctorMessage> sentMessages = doctorService.getSentMessages(userId);

        model.addAttribute("receivedMessages", receivedMessages);
        model.addAttribute("sentMessages", sentMessages);
        model.addAttribute("unreadCount", doctorService.getUnreadMessageCount(userId));

        return "parent/doctor-messages";
    }

    @GetMapping("/messages/{messageId}")
    public String viewMessage(@PathVariable Integer messageId, HttpSession session, Model model) {
        if (!isParentLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer userId = getLoggedInUserId(session);
        Optional<DoctorMessage> message = doctorService.getMessageById(messageId);

        if (message.isEmpty()) {
            model.addAttribute("error", "Message not found");
            return "redirect:/parent/doctors/messages";
        }

        DoctorMessage msg = message.get();

        if (msg.getReceiverId().equals(userId) && !msg.getIsRead()) {
            doctorService.markMessageAsRead(messageId);
        }

        model.addAttribute("message", msg);
        return "parent/doctor-message-view";
    }
}