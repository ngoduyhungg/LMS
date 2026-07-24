package com.lms.modules.auth.security;

import com.lms.modules.auth.entity.Permission;
import com.lms.modules.auth.entity.Role;
import com.lms.modules.auth.entity.User;
import com.lms.modules.auth.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities(){
        Set<GrantedAuthority> authorities = new HashSet<>();
        for(Role role : user.getRoles()){
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
            for (Permission permission : role.getPermissions()){
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return authorities;
    }

    @Override
    @NonNull
    public String getUsername(){
        return user.getEmail();
    }
    @Override
    public String getPassword(){
        return user.getPasswordHash();
    }
    @Override
    public boolean isEnabled(){
        return user.getStatus() == UserStatus.ACTIVE;
    }

}
