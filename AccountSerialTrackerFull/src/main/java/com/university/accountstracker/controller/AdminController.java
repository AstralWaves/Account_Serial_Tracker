package com.university.accountstracker.controller;

import com.university.accountstracker.model.QueueSerials;
import com.university.accountstracker.service.SerialService;
import com.university.accountstracker.model.UserRepository;
import com.university.accountstracker.model.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SerialService serialService;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(SerialService serialService, UserRepository userRepository) {
        this.serialService = serialService;
        this.userRepository = userRepository;
    }

    @PostMapping("/serial/next")
    public String nextSerial(@RequestParam("queueType") String queueTypeStr, RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            QueueSerials.QueueType queueType = QueueSerials.QueueType.valueOf(queueTypeStr.toUpperCase());
            serialService.incrementSerial(queueType, authentication.getName());
            redirectAttributes.addFlashAttribute("message", formatQueueName(queueType) + " serial incremented!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid queue type specified.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/serial/set")
    public String setSerial(@RequestParam("queueType") String queueTypeStr,
                            @RequestParam("serialValue") int serial,
                            RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            QueueSerials.QueueType queueType = QueueSerials.QueueType.valueOf(queueTypeStr.toUpperCase());
            if (serial >= 0) {
                serialService.setSerial(queueType, serial, authentication.getName());
                redirectAttributes.addFlashAttribute("message", formatQueueName(queueType) + " serial set to " + serial + "!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid serial number for " + formatQueueName(queueType) + ".");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid queue type specified for setting value.");
        }
        return "redirect:/admin";
    }
    
    @PostMapping("/serial/reset-all")
    public String resetAllSerials(RedirectAttributes redirectAttributes) {
        serialService.resetAllSerials();
        redirectAttributes.addFlashAttribute("message", "All serial numbers have been reset to 0.");
        return "redirect:/admin";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam(defaultValue = "STUDENT") String role, RedirectAttributes redirectAttributes) {
        if (userRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Username already exists.");
            return "redirect:/admin/users";
        }
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_" + role.toUpperCase());
        User user = new User(username, password, email, roles);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "User added successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_" + role.toUpperCase());
            user.setRoles(roles);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "User role updated.");
        }
        return "redirect:/admin/users";
    }

    private String formatQueueName(QueueSerials.QueueType queueType) {
        switch (queueType) {
            case TUITION_FEE: return "Tuition Fee";
            case HALL_FEE: return "Hall Fee";
            case CLEARANCE: return "Clearance";
            default: return "Unknown Queue";
        }
    }
}
