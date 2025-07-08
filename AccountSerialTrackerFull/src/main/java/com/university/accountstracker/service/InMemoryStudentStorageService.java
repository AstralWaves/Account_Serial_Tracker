// DEPRECATED: This service is now replaced by a database-backed implementation using Spring Data JPA.
package com.university.accountstracker.service;

import com.university.accountstracker.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

@Service
public class InMemoryStudentStorageService {
    private final Map<String, User> students = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InMemoryStudentStorageService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email) {
        return students.containsKey(email.toLowerCase());
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(students.get(email.toLowerCase()));
    }

    public User saveStudent(String email, String rawPassword) {
        if (!email.toLowerCase().endsWith("@diu.edu.bd")) {
            throw new IllegalArgumentException("Only @diu.edu.bd emails are allowed for signup.");
        }
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_STUDENT");
        User newStudent = new User(email.toLowerCase(), rawPassword, email.toLowerCase(), roles);
        newStudent.setPassword(passwordEncoder.encode(rawPassword));
        students.put(newStudent.getUsername(), newStudent);
        System.out.println("Registered student: " + email);
        return newStudent;
    }
    
    public List<User> getAllStudents() {
        return students.values().stream().collect(Collectors.toList());
    }
}
