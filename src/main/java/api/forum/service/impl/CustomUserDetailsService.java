package api.forum.service.impl;

import api.forum.model.users.Admin;
import api.forum.model.users.Member;
import api.forum.model.users.Role;
import api.forum.repository.users.AdminRepository;
import api.forum.repository.users.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        Optional<Member> optionalMember = memberRepository.findByUsername(username);
        // Check if user is present
        if (optionalAdmin.isEmpty() && optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else if (optionalAdmin.isPresent() && optionalMember.isEmpty()) {
            // Create a list of authorities (roles)
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();
            for (Role role : optionalAdmin.get().getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
            // Return a Spring Security User with my User username, password and roles
            return new org.springframework.security.core.userdetails.User(
                    optionalAdmin.get().getUsername(),
                    optionalAdmin.get().getPassword(),
                    authorities);
        } else if (optionalAdmin.isEmpty() && optionalMember.isPresent()) {
            // Create a list of authorities (roles)
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();
            for (Role role : optionalAdmin.get().getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
            // Return a Spring Security User with my User username, password and roles
            return new org.springframework.security.core.userdetails.User(
                    optionalAdmin.get().getUsername(),
                    optionalAdmin.get().getPassword(),
                    authorities);
        }
        return null;
    }

}

