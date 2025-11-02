package com.famcare.controller;

import com.famcare.model.Doctor;
import com.famcare.model.DoctorMessage;
import com.famcare.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    private boolean isDoctorLoggedIn(HttpSession session) {
        return "DOCTOR".equalsIgnoreCase(String.valueOf(session.getAttribute("userRole")));
    }

    private Integer getLoggedInDoctorId(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId != null ? (Integer) userId : null;
    }

    @GetMapping("/dashboard")
    public String doctorDashboard(HttpSession session, Model model) {
        if (!isDoctorLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer doctorId = getLoggedInDoctorId(session);
        Optional<Doctor> doctor = doctorService.getDoctorById(doctorId);

        if (doctor.isPresent()) {
            model.addAttribute("doctor", doctor.get());
        }

        int unreadCount = doctorService.getUnreadMessageCount(doctorId);
        model.addAttribute("unreadMessageCount", unreadCount);

        List<DoctorMessage> recentMessages = doctorService.getMessagesForUser(doctorId);
        if (recentMessages.size() > 5) {
            recentMessages = recentMessages.subList(0, 5);
        }
        model.addAttribute("recentMessages", recentMessages);

        return "doctor/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        if (!isDoctorLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer doctorId = getLoggedInDoctorId(session);
        Optional<Doctor> doctor = doctorService.getDoctorById(doctorId);

        if (doctor.isEmpty()) {
            model.addAttribute("error", "Doctor profile not found");
            return "redirect:/doctor/dashboard";
        }

        model.addAttribute("doctor", doctor.get());
        return "doctor/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {
        if (!isDoctorLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer doctorId = getLoggedInDoctorId(session);
        Optional<Doctor> doctor = doctorService.getDoctorById(doctorId);

        if (doctor.isEmpty()) {
            model.addAttribute("error", "Doctor profile not found");
            return "redirect:/doctor/dashboard";
        }

        model.addAttribute("doctor", doctor.get());
        return "doctor/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @RequestParam String email,
            @RequestParam String phoneNumber,
            @RequestParam String bio,
            @RequestParam Boolean isAvailable,
            HttpSession session,
            Model model) {

        if (!isDoctorLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer doctorId = getLoggedInDoctorId(session);
        Optional<Doctor> optionalDoctor = doctorService.getDoctorById(doctorId);

        if (optionalDoctor.isEmpty()) {
            model.addAttribute("error", "Doctor not found");
            return "redirect:/doctor/dashboard";
        }

        Doctor doctor = optionalDoctor.get();
        doctor.setEmail(email);
        doctor.setPhoneNumber(phoneNumber);
        doctor.setBio(bio);
        doctor.setIsAvailable(isAvailable);

        doctorService.updateDoctor(doctor);

        model.addAttribute("success", "Profile updated successfully!");
        model.addAttribute("doctor", doctor);
        return "doctor/profile";
    }

    @GetMapping("/messages")
    public String viewMessages(HttpSession session, Model model) {
        if (!isDoctorLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer doctorId = getLoggedInDoctorId(session);

        List<DoctorMessage> receivedMessages = doctorService.getMessagesForUser(doctorId);
        List<DoctorMessage> sentMessages = doctorService.getSentMessages(doctorId);

        model.addAttribute("receivedMessages", receivedMessages);
        model.addAttribute("sentMessages", sentMessages);
        model.addAttribute("unreadCount", doctorService.getUnreadMessageCount(doctorId));

        return "doctor/messages";
    }

    @GetMapping("/messages/{id}")
    public String viewMessage(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isDoctorLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer doctorId = getLoggedInDoctorId(session);
        Optional<DoctorMessage> message = doctorService.getMessageById(id);

        if (message.isEmpty()) {
            model.addAttribute("error", "Message not found");
            return "redirect:/doctor/messages";
        }

        DoctorMessage msg = message.get();

        if (msg.getReceiverId().equals(doctorId) && !msg.getIsRead()) {
            doctorService.markMessageAsRead(id);
        }

        model.addAttribute("message", msg);
        return "doctor/message-view";
    }

    @GetMapping("/messages/reply/{id}")
    public String replyForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isDoctorLoggedIn(session)) {
            return "redirect:/login";
        }

        Optional<DoctorMessage> originalMessage = doctorService.getMessageById(id);

        if (originalMessage.isEmpty()) {
            model.addAttribute("error", "Message not found");
            return "redirect:/doctor/messages";
        }

        model.addAttribute("originalMessage", originalMessage.get());
        return "doctor/reply-message";
    }

    @PostMapping("/messages/reply/{id}")
    public String sendReply(
            @PathVariable Integer id,
            @RequestParam String subject,
            @RequestParam String message,
            HttpSession session,
            Model model) {

        if (!isDoctorLoggedIn(session)) {
            return "redirect:/login";
        }

        Integer doctorId = getLoggedInDoctorId(session);
        String doctorName = (String) session.getAttribute("userName");

        Optional<DoctorMessage> originalMessage = doctorService.getMessageById(id);

        if (originalMessage.isEmpty()) {
            model.addAttribute("error", "Original message not found");
            return "redirect:/doctor/messages";
        }

        DoctorMessage original = originalMessage.get();

        doctorService.sendMessage(
                doctorId,
                doctorName,
                "DOCTOR",
                original.getSenderId(),
                original.getSenderName(),
                original.getSenderRole(),
                subject,
                message,
                original.getParentId(),
                original.getChildId()
        );

        model.addAttribute("success", "Reply sent successfully!");
        return "redirect:/doctor/messages";
    }
}