package com.example.fetescience.config;

import com.example.fetescience.model.Personne; // ✅ ADDED
import com.example.fetescience.repository.PersonneRepository; // ✅ ADDED
import com.example.fetescience.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PersonneRepository personneRepository; // ✅ ADDED: Repository injection

    // ✅ MODIFIED: Constructor now takes both services
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          PersonneRepository personneRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.personneRepository = personneRepository;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {


                // 1. Get the email of the logged-in user
                String email = authentication.getName();
                // 2. Fetch the full Personne object from DB
                Personne personne = personneRepository.findByEmail(email).orElse(null);

                // 3. Inject it into the Session so controllers can see it
                if (personne != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", personne);
                    session.setMaxInactiveInterval(30 * 60); // 30 mins session
                }

                // --- Existing Redirection Logic ---
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                boolean isParticipant = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_PARTICIPANT"));

                boolean isAnimateur = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ANIMATEUR"));

                if (isAdmin) {
                    response.sendRedirect("/admin/inscriptions");
                } else if (isParticipant) {
                    response.sendRedirect("/inscriptions/mes-inscriptions");
                } else if (isAnimateur) {
                    response.sendRedirect("/animateur_page");
                } else {
                    response.sendRedirect("/");
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                        .requestMatchers("/api/ateliers/**", "/creneaux/**", "/ateliers/**", "/animateurs/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/inscriptions/**").hasAnyRole("PARTICIPANT", "ADMIN")
                        .requestMatchers("/animateur_page").hasRole("ANIMATEUR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login") // Matches HTML form action
                        .successHandler(successHandler())  // Uses the bridge handler above
                        .failureUrl("/auth/login?error=true")
                        .usernameParameter("username")     // Matches HTML input name
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // ✅ Changed to match your Navbar link
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true) // Ensure session is cleared
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}