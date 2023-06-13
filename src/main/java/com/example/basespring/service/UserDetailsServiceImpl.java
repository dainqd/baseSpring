package com.example.basespring.service;

import com.example.basespring.dto.AccountDto;
import com.example.basespring.entities.Accounts;
import com.example.basespring.enums.Enums;
import com.example.basespring.repositories.AccountRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    public static final String ACCESS_TOKEN_KEY = "accessToken";
    @Autowired
    AccountRepository userRepository;

    @Autowired
    MessageResourceService messageResourceService;

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Accounts> findAll() {
        return userRepository.findAll();
    }

    public Accounts findById(Long id) {
        Optional<Accounts> optionalAccounts = userRepository.findById(id);
        if (!optionalAccounts.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, messageResourceService.getMessage("account.not.found"));
        }
        return optionalAccounts.get();
    }

    public Optional<Accounts> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<Accounts> findByEmail(String emai) {
        return userRepository.findByEmail(emai);
    }

    public void save(Accounts user) {
        userRepository.save(user);
    }

    public void create(AccountDto accountDto) {
        Accounts accounts = new Accounts();
        BeanUtils.copyProperties(accountDto, accounts);
        userRepository.save(accounts);
    }

    public void update(AccountDto accountDto){
        Optional<Accounts> optionalAccounts = userRepository.findById(accountDto.getId());
        if (!optionalAccounts.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR");
        }
        Accounts accounts = optionalAccounts.get();

        BeanUtils.copyProperties(accountDto, accounts);

        accounts.setDeletedAt(LocalDateTime.now());
        accounts.setStatus(Enums.AccountStatus.DELETED);
        userRepository.save(accounts);
    }

    public void deleteById(Long id) {
        Optional<Accounts> optionalAccounts = userRepository.findById(id);
        if (!optionalAccounts.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR");
        }
        Accounts accounts = optionalAccounts.get();

        accounts.setDeletedAt(LocalDateTime.now());
        accounts.setStatus(Enums.AccountStatus.DELETED);
        userRepository.save(accounts);
    }

    public void responseCookieToEverySubdomain(HttpServletResponse response, String accessToken) {
        saveAccessCookie(response, accessToken);
    }

    public void saveAccessCookie(HttpServletResponse response, String accessToken) {
        Cookie accessCookie = new Cookie(ACCESS_TOKEN_KEY, accessToken);
        accessCookie.setSecure(true);
        response.addCookie(accessCookie);
    }

    public void active(Accounts account) {
        account.setVerified(true);
        account.setVerifyCode(null);
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy(account.getId());
        this.save(account);
    }

    public boolean checkVerifyCode(Accounts account, String verifyCode) {
        return account.getVerifyCode().equals(verifyCode);
    }

    public boolean checkPasswordMatch(String rawPassword, Accounts user) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Accounts user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserDetailsIpmpl.build(user);
    }
}
