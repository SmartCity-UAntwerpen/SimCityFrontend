package be.uantwerpen.sc.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import org.springframework.context.annotation.Configuration;

/**
 * Created by Thomas on 27/02/2016.
 */
// Security configuration
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Autowired
    private Environment environment;

    // Security configuration mappings
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.authorizeRequests().antMatchers("/webjars/**")
                                    .permitAll();
        http.authorizeRequests().antMatchers("/Robot/**","/Drone/**","/F1/**","/worker/**","/droneworker/**")
                .permitAll();

        http.authorizeRequests().antMatchers("/login","/about")
                                    .permitAll()
                                    .anyRequest()
                                    .fullyAuthenticated()
                                    .and()
                                .formLogin()
                                    .loginPage("/login")
                                    .failureUrl("/login?error")
                                    .and()
                                .logout()
                                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                    .and()
                                .exceptionHandling()
                                    .accessDeniedPage("/access?accessdenied");

        devConfiguration(http);
    }

    // Security configuration h2 console in development configuration
    protected void devConfiguration(HttpSecurity http) throws Exception
    {
        for(String profile : environment.getActiveProfiles())
        {
            if(profile.equals("dev"))
            {
                //Permit access to H2 console --Development only
                http.authorizeRequests().antMatchers("/h2console/**")
                        .permitAll();

                http.csrf().disable();
                http.headers().frameOptions().disable();

                return;
            }
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Configuration
    protected static class AuthenticationSecurity extends GlobalAuthenticationConfigurerAdapter
    {
        @Autowired
        private UserDetailsService securityService;

        @Override
        public void init(AuthenticationManagerBuilder authBuilder) throws Exception
        {
            authBuilder.userDetailsService(securityService);
        }
    }
}
