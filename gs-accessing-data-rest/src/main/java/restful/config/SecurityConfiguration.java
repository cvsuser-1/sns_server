package restful.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@Import(OAuth2ClientConfig.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Autowired
  @Qualifier("singleSignOnFilter")
  private Filter singleSignOnFilter;

  @Autowired
  public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser("marissa").password("koala").roles("USER").and().withUser("paul")
        .password("emu").roles("USER");
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/login/**", "/webjars/**", "/images/**", "/oauth/uncache_approvals", "/oauth/cache_approvals");
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        .authorizeRequests()
        .antMatchers("/index.html").permitAll()
        //.anyRequest().hasRole("USER").exceptionHandling().accessDeniedPage("/login.jsp?authorization_error=true")
        .anyRequest().authenticated()
        .and()
        // TODO: put CSRF protection back into this endpoint
        .csrf().csrfTokenRepository(csrfTokenRepository()).requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
        .disable()
        .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
        .addFilterBefore(singleSignOnFilter, BasicAuthenticationFilter.class)
        .logout()
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login.jsp")
        .logoutSuccessHandler((request, response, authentication) -> {
        })
        .invalidateHttpSession(true)
        .addLogoutHandler((request, response, authentication) -> {
        })
        .deleteCookies("SessionID")
        .and()
        .formLogin()
        .loginProcessingUrl("/login")
        .failureUrl("/login.jsp?authentication_error=true")
        .loginPage("/index.html");
    // @formatter:on
  }

  private Filter csrfHeaderFilter() {
    return new OncePerRequestFilter() {
      @Override
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                      FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
          Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
          String token = csrf.getToken();
          if (cookie == null || token != null && !token.equals(cookie.getValue())) {
            cookie = new Cookie("XSRF-TOKEN", token);
            cookie.setPath("/");
            response.addCookie(cookie);
          }
        }
        filterChain.doFilter(request, response);
      }
    };
  }

  private CsrfTokenRepository csrfTokenRepository() {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");
    return repository;
  }
}
