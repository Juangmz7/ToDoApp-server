package com.juangomez.todoapp.serviceimpl.user;

import com.juangomez.todoapp.model.User;
import com.juangomez.todoapp.model.UserPrincipal;
import com.juangomez.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if ( user == null ) {
            throw new UsernameNotFoundException("User 404");
        }

        Collection<? extends GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map( role -> new SimpleGrantedAuthority(role.getName()) )
                .collect(Collectors.toSet()); // Stream to set

        return new UserPrincipal(user, authorities);
    }
}
