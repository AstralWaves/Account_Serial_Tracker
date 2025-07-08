package com.university.accountstracker.service;

import com.university.accountstracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Lazy
public class CustomUserDetailsService implements UserDetailsService {

    private final InMemoryStudentStorageService studentStorageService;
    private final PasswordEncoder passwordEncoder; 

    @Autowired
    public CustomUserDetailsService(InMemoryStudentStorageService studentStorageService, PasswordEncoder passwordEncoder) { 
        this.studentStorageService = studentStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("CustomUserDetailsService: Loading user: " + username);
        if ("admin".equalsIgnoreCase(username)) {
            return org.springframework.security.core.userdetails.User.withUsername("admin")
                    .password(passwordEncoder.encode("adminpass"))
                    .roles("ADMIN", "USER") 
                    .build();
        }
        User student = studentStorageService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        Set<GrantedAuthority> authorities = student.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                student.getUsername(), student.getPassword(), student.isEnabled(),
                true, true, true, authorities);
    }
}
