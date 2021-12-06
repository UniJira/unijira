package it.unical.unijira;

import it.unical.unijira.services.auth.AuthUserDetailsService;
import it.unical.unijira.utils.DtoMapper;
import it.unical.unijira.utils.filters.JwtAuthenticationFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import javax.persistence.EntityManager;

@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UniJiraConfig extends WebSecurityConfigurerAdapter {

    private final AuthUserDetailsService userDetailsService;
    private final EntityManager entityManager;

    @Autowired
    public UniJiraConfig(AuthUserDetailsService userDetailsService, EntityManager entityManager) {
        this.userDetailsService = userDetailsService;
        this.entityManager = entityManager;
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf().disable()
                .cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/authenticate").permitAll()
                .antMatchers("/auth/register").permitAll()
                .antMatchers("/auth/active").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);

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

}