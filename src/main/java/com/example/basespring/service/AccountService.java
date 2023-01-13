package com.example.basespring.service;

import com.example.basespring.dto.CredentialDto;
import com.example.basespring.dto.request.LoginRequest;
import com.example.basespring.entities.Accounts;
import com.example.basespring.entities.Credential;
import com.example.basespring.entities.Roles;
import com.example.basespring.enums.Enums;
import com.example.basespring.repositories.AccountRepository;
import com.example.basespring.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    final AccountRepository accountRepository;
    final PasswordEncoder passwordEncoder;

    public static final String ACCESS_TOKEN_KEY = "accessToken";
    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public boolean checkPasswordMatch(String rawPassword, Accounts account) {
        return passwordEncoder.matches(rawPassword, account.getPassword());
    }

    public Credential login(LoginRequest loginRequest) {
        Optional<Accounts> optionalAccount
                = accountRepository.findByUsername(loginRequest.getUsername());
        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("Email is not found");
        }
        Accounts accounts = optionalAccount.get();
        boolean isMatch = passwordEncoder.matches(loginRequest.getPassword(), accounts.getPassword());
        if (isMatch) {
            int expiredAfterDay = 7;
            String accessToken =
                    JwtUtils.generateTokenByAccount(accounts, expiredAfterDay = 24 * 60 * 60 * 1000);
            String refreshToken =
                    JwtUtils.generateTokenByAccount(accounts, 14 * 24 * 60 * 60 * 1000);
            Credential credential = new Credential();
            credential.setAccessToken(accessToken);
            credential.setRefreshToken(refreshToken);
            credential.setExpiredAt(expiredAfterDay);
            credential.setScope("basic_information");
            credential.setAccountId(accounts.getId());
            return credential;
        } else {
            throw new UsernameNotFoundException("Password is not match");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Accounts accounts = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Optional<Accounts> optionalAccount = accountRepository.findByUsername(username);
        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("Username is not found");
        }
        Accounts account = optionalAccount.get();
        List<GrantedAuthority> authorities = accounts.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new Accounts(account.getUsername(), account.getPassword(), authorities);

    }
}
