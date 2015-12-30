package restful.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfig {
  final static Logger logger = Logger.getLogger(OAuth2ClientConfig.class);
  @Autowired
  private OAuth2ClientContext oauth2ClientContext;

  @Bean
  @ConfigurationProperties("sinaweibo")
  ClientResources sinaweibo() {
    return new ClientResources();
  }

  @Bean
  @ConfigurationProperties("facebook")
  ClientResources facebook() {
    return new ClientResources();
  }

  @Bean
  @ConfigurationProperties("github")
  ClientResources github() {
    return new ClientResources();
  }

  @Bean
  public Filter singleSignOnFilter() {
    CompositeFilter filter = new CompositeFilter();
    List<Filter> filters = new ArrayList<>();
    filters.add(ssoFilter(facebook(), "/login/facebook"));
    filters.add(ssoFilter(github(), "/login/github"));
    filters.add(ssoFilter(sinaweibo(), "/login/sinaweibo"));
    filter.setFilters(filters);
    return filter;
  }

  private Filter ssoFilter(ClientResources client, String path) {
    OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
    OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
    filter.setRestTemplate(template);
    filter.setTokenServices(
        new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId()));

    logger.info("template=" + template);
    return filter;
  }
}

class ClientResources {
  private OAuth2ProtectedResourceDetails client = new AuthorizationCodeResourceDetails();
  private ResourceServerProperties resource = new ResourceServerProperties();

  public OAuth2ProtectedResourceDetails getClient() {
    return client;
  }

  public ResourceServerProperties getResource() {
    return resource;
  }
}
