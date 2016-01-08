package restful.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.webjars.RequireJS;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import restful.model.common.PhotoInfo;
import restful.model.common.PhotoService;
import restful.controller.AdminController;
import restful.controller.PhotoController;
import restful.controller.PhotoServiceUserController;
import restful.controller.account.AccessConfirmationController;
import restful.impl.PhotoServiceImpl;
import restful.oauth.SparklrUserApprovalHandler;

@Configuration
@RestController
//@EnableWebMvc
public class WebMvcConfig {

  @RequestMapping(value = "/webjarsjs", produces = "application/javascript")
  public String webjarjs() {
    return RequireJS.getSetupJavaScript("/webjars/");
  }
  @Bean
  public WebMvcConfigurerAdapter myWebMvcConfigAdapter(){
    return new MyWebMvcConfigAdapter();
  }

  @Configuration
  protected static class MyWebMvcConfigAdapter extends WebMvcConfigurerAdapter {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ContentNegotiatingViewResolver contentViewResolver() throws Exception {
      ContentNegotiationManagerFactoryBean contentNegotiationManager = new ContentNegotiationManagerFactoryBean();
      contentNegotiationManager.addMediaType("json", MediaType.APPLICATION_JSON);

      InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
      viewResolver.setPrefix("/WEB-INF/jsp/");
      viewResolver.setSuffix(".jsp");

      MappingJackson2JsonView defaultView = new MappingJackson2JsonView();
      defaultView.setExtractValueFromSingleKeyModel(true);

      ContentNegotiatingViewResolver contentViewResolver = new ContentNegotiatingViewResolver();
      contentViewResolver.setContentNegotiationManager(contentNegotiationManager.getObject());
      contentViewResolver.setViewResolvers(Arrays.<ViewResolver>asList(viewResolver));
      contentViewResolver.setDefaultViews(Arrays.<View>asList(defaultView));
      return contentViewResolver;
    }

    @Bean
    public PhotoServiceUserController photoServiceUserController(PhotoService photoService) {
      PhotoServiceUserController photoServiceUserController = new PhotoServiceUserController();
      return photoServiceUserController;
    }

    @Bean
    public PhotoController photoController(PhotoService photoService) {
      PhotoController photoController = new PhotoController();
      photoController.setPhotoService(photoService);
      return photoController;
    }

    @Bean
    public AccessConfirmationController accessConfirmationController(ClientDetailsService clientDetailsService,
                                                                     ApprovalStore approvalStore) {
      AccessConfirmationController accessConfirmationController = new AccessConfirmationController();
      accessConfirmationController.setClientDetailsService(clientDetailsService);
      accessConfirmationController.setApprovalStore(approvalStore);
      return accessConfirmationController;
    }

    @Bean
    public PhotoServiceImpl photoServices() {
      List<PhotoInfo> photos = new ArrayList<PhotoInfo>();
      photos.add(createPhoto("1", "marissa"));
      photos.add(createPhoto("2", "paul"));
      photos.add(createPhoto("3", "marissa"));
      photos.add(createPhoto("4", "paul"));
      photos.add(createPhoto("5", "marissa"));
      photos.add(createPhoto("6", "paul"));

      PhotoServiceImpl photoServices = new PhotoServiceImpl();
      photoServices.setPhotos(photos);
      return photoServices;
    }

    // N.B. the @Qualifier here should not be necessary (gh-298) but lots of users report needing it.
    @Bean
    public AdminController adminController(TokenStore tokenStore,
                                           @Qualifier("consumerTokenServices") ConsumerTokenServices tokenServices,
                                           SparklrUserApprovalHandler userApprovalHandler) {
      AdminController adminController = new AdminController();
      adminController.setTokenStore(tokenStore);
      adminController.setTokenServices(tokenServices);
      adminController.setUserApprovalHandler(userApprovalHandler);
      return adminController;
    }

    private PhotoInfo createPhoto(String id, String userId) {
      PhotoInfo photo = new PhotoInfo();
      photo.setId(id);
      photo.setName("photo" + id + ".jpg");
      photo.setUserId(userId);
      photo.setResourceURL("/org/springframework/security/oauth/examples/sparklr/impl/resources/" + photo.getName());
      return photo;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
      configurer.enable();
    }

    protected static class dumpRegistrationController implements Controller {

      DispatcherServlet dispatcherServlet;

      @Autowired(required = false)
      void setDispatcherServlet(DispatcherServlet dispatcherServlet) {
        final String str = DispatcherServlet.EXCEPTION_ATTRIBUTE;
        this.dispatcherServlet = dispatcherServlet;
      }

      @Override
      public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return null;
      }
    }
  }
}
