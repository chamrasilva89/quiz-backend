package com.sasip.quizz.security;

import com.sasip.quizz.model.User;
import com.sasip.quizz.model.Staff;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.repository.StaffRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find the user in the "users" table
        User user = userRepository.findByUsername(username)
                .orElse(null);

        // If user not found, try to find the staff in the "staff" table
        if (user == null) {
            Staff staff = staffRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            
            // You can create a custom userDetails for staff similar to UserDetails
            return new org.springframework.security.core.userdetails.User(
                staff.getUsername(), 
                staff.getPasswordHash(), 
                List.of(new SimpleGrantedAuthority("ROLE_" + staff.getRole()))  // Assuming roles are stored as strings
            );
        }

        // Return a UserDetails object for the found user
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPasswordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}

