package it.unical.unijira;

import it.unical.unijira.services.auth.AuthTokenException;
import it.unical.unijira.services.auth.AuthTokenFilter;
import it.unical.unijira.services.auth.AuthUserDetailsService;
import it.unical.unijira.utils.Config;
import it.unical.unijira.utils.DtoMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UniJiraSecurityConfig extends WebSecurityConfigurerAdapter {


    private final AuthUserDetailsService userDetailsService;
    private final EntityManager entityManager;
    private final Config config;

    @Autowired
    public UniJiraSecurityConfig(Config config, AuthUserDetailsService userDetailsService, EntityManager entityManager) {
        this.config = config;
        this.userDetailsService = userDetailsService;
        this.entityManager = entityManager;
    }



    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf().disable()
                .cors().configurationSource(r -> new CorsConfiguration().applyPermitDefaultValues()).and()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((r, s, e) -> this.unauthorizedEntryPoint(s, r.getAttribute("auth-token-exception")));


        for(String url : config.getPublicUrls())
            httpSecurity.authorizeRequests().antMatchers(url).permitAll();

        httpSecurity
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new AuthTokenFilter(authenticationManager(), config), UsernamePasswordAuthenticationFilter.class);

    }


    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ModelMapper modelMapper() {
        return new DtoMapper(entityManager);
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @ResponseBody
    @ExceptionHandler(AuthTokenException.class)
    public void handleAuthTokenException(AuthTokenException e, HttpServletResponse response) {
        response.setStatus(e.getHttpStatus().value());
    }


    private void unauthorizedEntryPoint(HttpServletResponse response, Object exception) throws IOException {

        if(exception instanceof AuthTokenException e) {

            response.sendError(e.getHttpStatus().value(), e.getMessage());
            return;

        }

        response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());

    }

}