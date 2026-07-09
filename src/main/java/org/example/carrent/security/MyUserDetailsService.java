package org.example.carrent.security;

import lombok.RequiredArgsConstructor;
import org.example.carrent.models.User;
import org.example.carrent.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPasswordHash(),
                authorities
        );
    }
}