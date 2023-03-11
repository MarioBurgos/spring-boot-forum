package api.forum.security;

import api.forum.security.filters.CustomAuthenticationFilter;
import api.forum.security.filters.CustomAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
    // UserDetailsService is an interface provided by Spring Security that defines a way to retrieve user information
    // Implementation is in CustomUserDetailsService
    @Autowired
    private UserDetailsService userDetailsService;

    // Autowired instance of the AuthenticationManagerBuilder
    @Autowired
    private AuthenticationManagerBuilder authManagerBuilder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CustomAuthenticationFilter instance created
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authManagerBuilder.getOrBuild());
        // set the URL that the filter should process
        customAuthenticationFilter.setFilterProcessesUrl("/login");
        // disable CSRF protection.  Remember, this must be active if the project works with its own .jsp views.
        http.csrf().disable();
        // set the session creation policy to stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // set up authorization for different request matchers and user roles
        http.authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/admins").hasRole("SUPERADMIN") // new Admin
                .requestMatchers(HttpMethod.GET, "/admins").hasRole("SUPERADMIN") // list all admins
                .requestMatchers(HttpMethod.GET, "/admins/last-login").hasRole("SUPERADMIN") // find by last-login between 2 dates
                .requestMatchers(HttpMethod.GET, "/admins/status/*").hasRole("SUPERADMIN") // find by status
                .requestMatchers(HttpMethod.GET, "/admins/shift/*").hasRole("SUPERADMIN") // find by shift
                .requestMatchers(HttpMethod.GET, "/admins/location/*").hasRole("SUPERADMIN") // find by location
                .requestMatchers(HttpMethod.PATCH, "/admins/*/username").authenticated() // update/patch email
                .requestMatchers(HttpMethod.PATCH, "/admins/*/email").authenticated() // update/patch email
                .requestMatchers(HttpMethod.PATCH, "/admins/*/password").authenticated() // update/patch password
                .requestMatchers(HttpMethod.PATCH, "/admins/*/status").authenticated() // update/patch status
                .requestMatchers(HttpMethod.PATCH, "/admins/*/location").authenticated() // update/patch location
                .requestMatchers(HttpMethod.PATCH, "/admins/*/shift").hasRole("SUPERADMIN") // update/patch shift
                .requestMatchers(HttpMethod.GET, "/admins/*").authenticated()  // find by id
                .requestMatchers(HttpMethod.PUT, "/admins/*").hasRole("SUPERADMIN") // update admin
                .requestMatchers(HttpMethod.DELETE, "/admins/*").hasRole("SUPERADMIN") // delete admin
                .requestMatchers(HttpMethod.GET, "/posts/*").permitAll() // list all posts
                .requestMatchers(HttpMethod.GET, "/posts").permitAll() // list all posts
                .anyRequest().permitAll();

        // add the custom authentication filter to the http security object
        http.addFilter(customAuthenticationFilter);
        // Add the custom authorization filter before the standard authentication filter.
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Build the security filter chain to be returned.
        return http.build();

    }
}
