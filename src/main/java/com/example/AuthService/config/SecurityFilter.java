package com.example.AuthService.config;

import com.example.AuthService.jwt.TokenService;
import com.example.AuthService.model.auth.UserAuthentication;
import com.example.AuthService.model.entity.User;
import com.example.AuthService.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(token != null){
            var email = tokenService.validateToken(token);
            //UserDetails user = userRepository.findByEmail(email); // Possível erro depois,
            User user = userRepository.findUserByEmail(email);
            UserAuthentication userAuthentication = new UserAuthentication(user);
            var authentication = new UsernamePasswordAuthenticationToken(user, null, userAuthentication.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
