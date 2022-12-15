package com.nikitalipatov.handmadeshop.security;


import com.nikitalipatov.handmadeshop.security.jwt.AuthEntryPointJwt;
import com.nikitalipatov.handmadeshop.security.jwt.AuthTokenFilter;
import com.nikitalipatov.handmadeshop.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors()
				.and()
				.csrf()
				.disable()
				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers("/api/products/**").permitAll()

//				.antMatchers("api/category/add").hasRole("2")

				.antMatchers("/files/**").permitAll()
				.antMatchers("/api/sale/**").permitAll()
				.antMatchers("/api/sale/add").permitAll()
				.antMatchers("/api/cart/products").permitAll()

				.antMatchers("/api/animal/**").permitAll()
				.antMatchers("/api/category/**").permitAll()

				.antMatchers("/api/sale/get").permitAll()
				.antMatchers("/api/sale/get/**").permitAll()

				.antMatchers("api/user/**").permitAll()
//				.antMatchers("api/user/promote/**").hasAuthority("3")
//				.antMatchers("api/user/demote/**").hasAuthority("3")

				.antMatchers("/file/upload").permitAll()

				.antMatchers("/api/auth/**").permitAll()
				.antMatchers("/upload").permitAll()
				.antMatchers("/upload/***").permitAll()
				.antMatchers("/promotions/**").permitAll()
				.antMatchers("/files/**").permitAll()
				.antMatchers("/catalog/**").permitAll()
				.antMatchers("/catalog/animals").permitAll()
				.antMatchers("/catalog/categories").permitAll()
				.antMatchers("/catalog/user_filters/***").permitAll()
				.antMatchers("/comments/**").permitAll()
				.antMatchers("/comments/add/***").permitAll()

				.anyRequest()
				.authenticated();

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
