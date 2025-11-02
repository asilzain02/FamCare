package com.famcare.controller;

import com.famcare.model.User;
import com.famcare.model.Doctor;
import com.famcare.service.AuthService;
import com.famcare.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private DoctorService doctorService;

    /**
     * Show home page
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Show login page
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Handle login form submission - supports Parent, Child, and Doctor login
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // First, try to authenticate as regular user (Parent/Child)
        User user = authService.authenticateUser(username, password);

        if (user != null) {
            // Store user in session
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("userName", user.getFullName());

            // Redirect based on role
            if ("PARENT".equalsIgnoreCase(user.getRole())) {
                return "redirect:/parent/dashboard";
            } else if ("CHILD".equalsIgnoreCase(user.getRole())) {
                return "redirect:/child/dashboard";
            }
        }

        // If not a regular user, try to authenticate as doctor
        Doctor doctor = doctorService.authenticateDoctor(username, password);

        if (doctor != null) {
            // Store doctor in session
            session.setAttribute("loggedInUser", doctor);
            session.setAttribute("userId", doctor.getId());
            session.setAttribute("userRole", "DOCTOR");
            session.setAttribute("userName", doctor.getFullName());

            return "redirect:/doctor/dashboard";
        }

        // Login failed for all user types
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    /**
     * Show registration page (for demonstration)
     * In real app, you'd need separate pages for parent/child registration
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // We'll create this template later
    }

    /**
     * Handle parent registration
     */
    @PostMapping("/register/parent")
    public String registerParent(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String fullName,
            Model model) {

        boolean success = authService.registerUser(username, password, email, "PARENT", fullName, null);

        if (!success) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        model.addAttribute("success", "Parent account created! Please login.");
        return "login";
    }

    /**
     * Handle child registration
     */
    @PostMapping("/register/child")
    public String registerChild(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam Integer parentId,
            Model model) {

        // Verify parent exists
        if (authService.getUserById(parentId).isEmpty()) {
            model.addAttribute("error", "Parent ID not found");
            return "register";
        }

        boolean success = authService.registerUser(username, password, email, "CHILD", fullName, parentId);

        if (!success) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        model.addAttribute("success", "Child account created! Please login.");
        return "login";
    }

    /**
     * Handle logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear session
        return "redirect:/";
    }

    /**
     * Check if user is logged in (helper method)
     */
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }
}