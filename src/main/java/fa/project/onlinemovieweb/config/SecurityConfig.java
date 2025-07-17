package fa.project.onlinemovieweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import fa.project.onlinemovieweb.security.CustomOAuth2SuccessHandler;

@Configuration
public class SecurityConfig {
	
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2SuccessHandler customOAuth2SuccessHandler) throws Exception {
        http
        	.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/home", "/complete-profile", "/register",
                        "/profile", "/media/***", "/genres", "/lastestrelease", "/movies", "/series", "/mediaVideo/***",
                        "/login", "/css/**", "/js/**", "/img/**", "/assets/**").permitAll()
                .anyRequest().authenticated()
            )
            
//            .formLogin(form -> form
//                .loginPage("/login1")
//                .loginProcessingUrl("/login1")
//                .defaultSuccessUrl("/home", true)
//                .failureUrl("/login1?error=true")
//                .permitAll()
//            )
            
            .formLogin(form -> form.disable())
            
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler(customOAuth2SuccessHandler)
            );
        return http.build();
    }
}

