package com.famcare.service;

import com.famcare.model.Doctor;
import com.famcare.model.DoctorMessage;
import com.famcare.repository.DoctorRepository;
import com.famcare.repository.DoctorMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorMessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Doctor Management
    public boolean registerDoctor(String username, String password, String email, String fullName,
                                  String specialization, String qualification, String licenseNumber,
                                  Integer experienceYears, String phoneNumber, String bio) {
        if (doctorRepository.existsByUsername(username)) {
            return false;
        }

        Doctor doctor = new Doctor(username, password, email, fullName, specialization, qualification);
        doctor.setPassword(passwordEncoder.encode(password));
        doctor.setLicenseNumber(licenseNumber);
        doctor.setExperienceYears(experienceYears);
        doctor.setPhoneNumber(phoneNumber);
        doctor.setBio(bio);
        doctor.setIsAvailable(true);

        doctorRepository.save(doctor);
        return true;
    }

    public Doctor authenticateDoctor(String username, String password) {
        Optional<Doctor> optionalDoctor = doctorRepository.findByUsername(username);

        if (optionalDoctor.isEmpty()) {
            return null;
        }

        Doctor doctor = optionalDoctor.get();

        if (passwordEncoder.matches(password, doctor.getPassword())) {
            return doctor;
        }

        return null;
    }

    public Optional<Doctor> getDoctorById(Integer id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findAvailable();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public void updateDoctor(Doctor doctor) {
        doctorRepository.update(doctor);
    }

    // Messaging
    public void sendMessage(Integer senderId, String senderName, String senderRole,
                           Integer receiverId, String receiverName, String receiverRole,
                           String subject, String message, Integer parentId, Integer childId) {
        DoctorMessage msg = new DoctorMessage(senderId, senderName, senderRole,
                                             receiverId, receiverName, receiverRole,
                                             subject, message, parentId, childId);
        messageRepository.save(msg);
    }

    public List<DoctorMessage> getMessagesForUser(Integer userId) {
        return messageRepository.findByReceiverId(userId);
    }

    public List<DoctorMessage> getSentMessages(Integer userId) {
        return messageRepository.findBySenderId(userId);
    }

    public List<DoctorMessage> getConversation(Integer userId1, Integer userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }

    public void markMessageAsRead(Integer messageId) {
        messageRepository.markAsRead(messageId);
    }

    public int getUnreadMessageCount(Integer userId) {
        return messageRepository.countUnreadByReceiverId(userId);
    }

    public List<DoctorMessage> getUnreadMessages(Integer userId) {
        return messageRepository.findUnreadByReceiverId(userId);
    }

    public Optional<DoctorMessage> getMessageById(Integer id) {
        return messageRepository.findById(id);
    }

    public void deleteMessage(Integer id) {
        messageRepository.deleteById(id);
    }
}