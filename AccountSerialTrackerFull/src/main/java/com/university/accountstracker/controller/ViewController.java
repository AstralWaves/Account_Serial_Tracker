package com.university.accountstracker.controller;

import com.university.accountstracker.model.QueueSerials;
import com.university.accountstracker.model.User;
import com.university.accountstracker.model.SerialHistoryRepository;
import com.university.accountstracker.service.InMemoryStudentStorageService;
import com.university.accountstracker.service.SerialService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViewController {
    private final SerialHistoryRepository serialHistoryRepository;
    private final SerialService serialService;
    private final InMemoryStudentStorageService studentStorageService;

    @Autowired
    public ViewController(SerialHistoryRepository serialHistoryRepository, SerialService ss, InMemoryStudentStorageService isss) {
        this.serialHistoryRepository = serialHistoryRepository;
        this.serialService = ss;
        this.studentStorageService = isss;
    }

    @GetMapping("/")
    public String studentHomePage(Model model) {
        model.addAttribute("currentSerials", serialService.getCurrentQueueSerials());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("serialHistory", serialHistoryRepository.findByUsernameOrderByTimestampDesc(auth.getName()));
        } else {
            model.addAttribute("serialHistory", java.util.Collections.emptyList());
        }
        return "student";
    }
    
    @GetMapping("/student") public String studentRedirect() { return "redirect:/"; }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("currentSerials", serialService.getCurrentQueueSerials());
        model.addAttribute("queueTypes", QueueSerials.QueueType.values());
        return "admin";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required=false) String error, @RequestParam(required=false) String logout, Model model) {
        if (error != null) model.addAttribute("loginError", "Invalid username or password.");
        if (logout != null) model.addAttribute("logoutMessage", "Logged out successfully.");
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User("", "", "")); 
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@RequestParam String email, @RequestParam String password, @RequestParam String confirmPassword, RedirectAttributes ra, Model model) {
        model.addAttribute("user", new User(email, "", email)); 
        if (!email.toLowerCase().endsWith("@diu.edu.bd")) {
            model.addAttribute("signupError", "Only @diu.edu.bd emails allowed."); return "signup";
        }
        if (studentStorageService.emailExists(email)) {
            model.addAttribute("signupError", "Email already registered. Please login."); return "signup";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("signupError", "Passwords do not match."); return "signup";
        }
        if (password.length() < 6) { 
             model.addAttribute("signupError", "Password must be at least 6 characters."); return "signup";
        }
        try {
            studentStorageService.saveStudent(email, password);
            System.out.println("Signup successful for: " + email);
            ra.addFlashAttribute("signupSuccess", "Signup successful! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("signupError", e.getMessage()); return "signup";
        }
    }
}
