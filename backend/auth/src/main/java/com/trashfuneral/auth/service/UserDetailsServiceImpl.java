package com.trashfuneral.auth.service;

import com.trashfuneral.auth.repo.UserRepository;
import com.trashfuneral.common.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository users;

    public UserDetailsServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByUsername(username)
                .map(u -> new UserPrincipal(u.getId(), u.getUsername(), u.getPasswordHash(), u.getDisplayName()))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
