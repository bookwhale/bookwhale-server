package com.teamherb.bookstoreback.security.config;

import com.teamherb.bookstoreback.security.CustomAuthenticationEntryPoint;
import com.teamherb.bookstoreback.security.CustomUserDetailsService;
import com.teamherb.bookstoreback.security.TokenProvider;
import com.teamherb.bookstoreback.security.filter.LoginFilter;
import com.teamherb.bookstoreback.security.filter.TokenAuthenticationFilter;
import com.teamherb.bookstoreback.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String USER = "USER";

    private final CustomUserDetailsService customUserDetailsService;

    private final CorsFilter corsFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenProvider tokenProvider() {
        return new TokenProvider();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
        throws Exception {
        authenticationManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .headers().frameOptions().disable().and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()

            .addFilter(corsFilter)
            .addFilter(loginFilter())
            .addFilter(new TokenAuthenticationFilter(authenticationManager(), tokenProvider(),
                customUserDetailsService))

            .exceptionHandling()
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .and()

            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .authorizeRequests()
            .antMatchers("/api/user/signup")
            .permitAll()
            .antMatchers("/api/**").hasRole(USER);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .requestMatchers(
                PathRequest.toStaticResources().atCommonLocations()
            );
    }

    private LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter(authenticationManager());
        loginFilter.setFilterProcessesUrl("/api/user/login");
        loginFilter.setAuthenticationSuccessHandler(new LoginSuccessHandler(tokenProvider()));
        return loginFilter;
    }
}