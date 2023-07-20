package com.example.basespring.controller;

import com.example.basespring.dto.reponse.JwtResponse;
import com.example.basespring.dto.request.LoginRequest;
import com.example.basespring.entities.Accounts;
import com.example.basespring.enums.Enums;
import com.example.basespring.service.MessageResourceService;
import com.example.basespring.service.UserDetailsIpmpl;
import com.example.basespring.service.UserDetailsServiceImpl;
import com.example.basespring.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
@Slf4j
public class AuthController {
    final UserDetailsServiceImpl userDetailsService;
    final MessageResourceService messageResourceService;
    final HttpServletRequest request;
    final AuthenticationManager authenticationManager;
    final JwtUtils jwtUtils;

    @GetMapping("login")
    public String getList(Model model, @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                          @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        try {
            LoginRequest loginRequest = new LoginRequest();
            model.addAttribute("loginRequest", loginRequest);
            return "/v1/auth/login";
        } catch (Exception e) {
            return "v1/fail";
        }
    }

    @PostMapping("login")
    public String processServiceLogin(
            @Valid @ModelAttribute LoginRequest loginRequest,
            BindingResult result,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        Optional<Accounts> optionalUser = userDetailsService.findByUsername(loginRequest.getUsername());
        if (!optionalUser.isPresent()) {
            result.rejectValue("username", "400", messageResourceService.getMessage("account.not.found"));
            model.addAttribute("loginRequest", loginRequest);
            return "v1/fail";
        }
        Accounts account = optionalUser.get();
        if (!account.isVerified()) {
            result.rejectValue("username", "400", messageResourceService.getMessage("account.not.verified"));
            model.addAttribute("loginRequest", loginRequest);
            return "v1/fail";
        }
        if (account.getStatus() == Enums.AccountStatus.DEACTIVE) {
            result.rejectValue("username", "400", messageResourceService.getMessage("account.not.active"));
            model.addAttribute("loginRequest", loginRequest);
            return "v1/fail";
        }
        if (account.getStatus() == Enums.AccountStatus.BLOCKED) {
            result.rejectValue("username", "400", messageResourceService.getMessage("account.banned"));
            model.addAttribute("loginRequest", loginRequest);
            return "v1/fail";
        }
        if (account.getStatus() == Enums.AccountStatus.DELETED) {
            result.rejectValue("username", "400", messageResourceService.getMessage("account.deleted"));
            model.addAttribute("loginRequest", loginRequest);
            return "v1/fail";
        }
        // Xử lý check mật khẩu, add login history, update last login.
        boolean isMatch = userDetailsService.checkPasswordMatch(loginRequest.getPassword(), account);
        if (isMatch) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
                            , loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateToken(authentication);
            UserDetailsIpmpl userDetails = (UserDetailsIpmpl) authentication.getPrincipal();

            Cookie cookie = new Cookie("username", userDetails.getUsername());
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);

            model.addAttribute("message", "Login success. Welcome ");
            model.addAttribute("user", optionalUser.get().getUsername());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            response.setStatus(HttpServletResponse.SC_OK);
            userDetailsService.saveAccessCookie(response, jwt);
            return "v1/success";
        } else {
            result.rejectValue("password", "400", messageResourceService.getMessage("account.password.incorrect"));
            model.addAttribute("loginRequest", loginRequest);
            return "redirect:/";
        }
    }
}
