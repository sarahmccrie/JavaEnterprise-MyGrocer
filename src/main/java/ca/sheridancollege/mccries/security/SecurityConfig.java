package ca.sheridancollege.mccries.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/* Name: Sarah McCrie 991405606
* Assignment: Assignment #3
* Date: November 23, 2023
* Program: A3_mccries
*/

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private LoggingAccessDeniedHandler accessDeniedHandler;

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
		http.csrf(c -> c.disable()).headers(header -> header.frameOptions(frame -> frame.disable()))
		.authorizeHttpRequests(requests -> requests
			    .requestMatchers(mvc.pattern("/adminSecure/**")).hasRole("ADMIN")
			    .requestMatchers(mvc.pattern("/secure/**")).hasAnyRole("USER", "ADMIN")
			    .requestMatchers(mvc.pattern("/"), 
			                     mvc.pattern("/js/**"), 
			                     mvc.pattern("/css/**"),
			                     mvc.pattern("/images/**"), 
			                     mvc.pattern("/permission-denied"),
			                     antMatcher("/h2-console/**"), 
			                     antMatcher("/register"), 
			                     antMatcher("/pass"))
			    .permitAll()
			    .requestMatchers(mvc.pattern("/**")).denyAll()
			)

				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
						.defaultSuccessUrl("/secure", true).permitAll())
				.exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler))
				.logout(logout -> logout.invalidateHttpSession(true).clearAuthentication(true).logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout").permitAll());
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

}
