package restful.controller.account;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller for retrieving the model for and displaying the confirmation page for access to a protected resource.
 *
 * @author Ryan Heaton
 */
@Controller
//@RequestMapping("/account")
@SessionAttributes("authorizationRequest")
public class AccessConfirmationController {
  final static Logger logger = Logger.getLogger(AccessConfirmationController.class);

  @Autowired
  @Qualifier("githubRestTemplate")
  private OAuth2RestTemplate githubRestTemplate;

  @Autowired
  @Qualifier("facebookRestTemplate")
  private OAuth2RestTemplate facebookRestTemplate;

  @Autowired
  @Qualifier("sinaweiboRestTemplate")
  private OAuth2RestTemplate sinaweiboRestTemplate;

  private ClientDetailsService clientDetailsService;

  private ApprovalStore approvalStore;

  @RequestMapping("/oauth/check_token")
  public ModelAndView getGitHubAccessConfirmation(Map<String, Object> model, Principal principal) throws Exception {
    AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
    ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
    logger.info("login user " + principal.getName());
    model.put("auth_request", clientAuth);
    model.put("client", client);
    Map<String, String> scopes = new LinkedHashMap<String, String>();
    for (String scope : clientAuth.getScope()) {
      scopes.put(OAuth2Utils.SCOPE_PREFIX + scope, "false");
    }
    for (Approval approval : approvalStore.getApprovals(principal.getName(), client.getClientId())) {
      if (clientAuth.getScope().contains(approval.getScope())) {
        scopes.put(OAuth2Utils.SCOPE_PREFIX + approval.getScope(),
            approval.getStatus() == ApprovalStatus.APPROVED ? "true" : "false");
      }
    }
    model.put("scopes", scopes);
    return new ModelAndView("access_confirmation", model);
  }
  @RequestMapping("/github/oauth/confirm_access")
  public ModelAndView getFacebookAccessConfirmation(Map<String, Object> model, Principal principal) throws Exception {
    AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
    ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
    logger.info("login user " + principal.getName());
    model.put("auth_request", clientAuth);
    model.put("client", client);
    Map<String, String> scopes = new LinkedHashMap<String, String>();
    for (String scope : clientAuth.getScope()) {
      scopes.put(OAuth2Utils.SCOPE_PREFIX + scope, "false");
    }
    for (Approval approval : approvalStore.getApprovals(principal.getName(), client.getClientId())) {
      if (clientAuth.getScope().contains(approval.getScope())) {
        scopes.put(OAuth2Utils.SCOPE_PREFIX + approval.getScope(),
            approval.getStatus() == ApprovalStatus.APPROVED ? "true" : "false");
      }
    }
    model.put("scopes", scopes);
    return new ModelAndView("access_confirmation", model);
  }
  @RequestMapping("/sinaweibo/oauth/confirm_access")
  public ModelAndView getSinaWeiboAccessConfirmation(Map<String, Object> model, Principal principal) throws Exception {
    AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
    ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
    model.put("auth_request", clientAuth);
    model.put("client", client);
    Map<String, String> scopes = new LinkedHashMap<String, String>();
    for (String scope : clientAuth.getScope()) {
      scopes.put(OAuth2Utils.SCOPE_PREFIX + scope, "false");
    }
    for (Approval approval : approvalStore.getApprovals(principal.getName(), client.getClientId())) {
      if (clientAuth.getScope().contains(approval.getScope())) {
        scopes.put(OAuth2Utils.SCOPE_PREFIX + approval.getScope(),
            approval.getStatus() == ApprovalStatus.APPROVED ? "true" : "false");
      }
    }
    model.put("scopes", scopes);
    return new ModelAndView("access_confirmation", model);
  }
  @RequestMapping("/oauth/error")
  public String handleError(Map<String, Object> model) throws Exception {
    // We can add more stuff to the model here for JSP rendering. If the client was a machine then
    // the JSON will already have been rendered.
    model.put("message", "There was a problem with the OAuth2 protocol");
    return "oauth_error";
  }

  public void setClientDetailsService(ClientDetailsService clientDetailsService) {
    this.clientDetailsService = clientDetailsService;
  }

  public void setApprovalStore(ApprovalStore approvalStore) {
    this.approvalStore = approvalStore;
  }

}
