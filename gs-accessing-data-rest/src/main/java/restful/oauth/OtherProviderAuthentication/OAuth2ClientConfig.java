package restful.oauth.OtherProviderAuthentication;

import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.filter.CompositeFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;

@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfig {
  final static Logger logger = Logger.getLogger(OAuth2ClientConfig.class);

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
  public Filter singleSignOnFilter(OAuth2ClientContext clientContext) {
    CompositeFilter filter = new CompositeFilter();
    List<Filter> filters = new ArrayList<>();
    filters.add(ssoFilter(facebook(), facebookRestTemplate(clientContext), "/login/oauth2/facebook/user"));
    filters.add(ssoFilter(github(), githubRestTemplate(clientContext), "/login/oauth2/github/user"));
    filters.add(ssoFilter(sinaweibo(), sinaweiboRestTemplate(clientContext), "/login/oauth2/sinaweibo/user"));
    filter.setFilters(filters);
    return filter;
  }

  @Bean
  public OAuth2RestTemplate sinaweiboRestTemplate(OAuth2ClientContext clientContext) {
    OAuth2RestTemplate template = new OAuth2RestTemplate(sinaweibo().getClient(), clientContext);
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript")));
    template.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(converter));
    return template;
  }

  @Bean
  public OAuth2RestTemplate facebookRestTemplate(OAuth2ClientContext clientContext) {
    OAuth2RestTemplate template = new OAuth2RestTemplate(facebook().getClient(), clientContext);
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript")));
    template.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(converter));
    return template;
  }

  @Bean
  public OAuth2RestTemplate githubRestTemplate(OAuth2ClientContext clientContext) {
    OAuth2RestTemplate template = new OAuth2RestTemplate(github().getClient(), clientContext);
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript")));
    template.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(converter));
    return template;
  }

  private Filter ssoFilter(ClientResources client, OAuth2RestTemplate restTemplate, String path) {
    OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
    filter.setRestTemplate(restTemplate);
    filter.setTokenServices(
        new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId()));

    logger.info("template=" + restTemplate);
    return filter;
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
}


