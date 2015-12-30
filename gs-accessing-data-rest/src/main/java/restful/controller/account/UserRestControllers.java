package restful.controller.account;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import restful.dao.CaregiversRepository;
import restful.dao.CliniciansRepository;
import restful.dao.EHRRepository;
import restful.dao.PatientsRepository;
import restful.hello.bookmarks.AccountRepository;
import restful.model.common.AccountBase;
import restful.model.common.Caregiver;
import restful.model.common.Clinician;
import restful.model.common.Patient;

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

  @Autowired
  AccountRepository accountRepository;

  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService());
  }

  @Bean
  UserDetailsService userDetailsService() {
    return (username) -> accountRepository.findByUsername(username)
        .map(a -> new User(a.username, a.password, true, true, true, true,
            AuthorityUtils.createAuthorityList("USER", "write")))
        .orElseThrow(() -> new UsernameNotFoundException("could not find the user '" + username + "'"));
  }
}

@RestController
@RequestMapping("/account")
public class UserRestControllers {

  final static Logger logger = Logger.getLogger(UserRestControllers.class);
  private final PatientsRepository patientsRepository;
  private final EHRRepository eHRRepository;
  private final CliniciansRepository cliniciansRepository;
  private final CaregiversRepository caregiversRepository;

  @Autowired
  public UserRestControllers(PatientsRepository patientsRepository, EHRRepository eHRRepository,
                             CliniciansRepository cliniciansRepository, CaregiversRepository caregiversRepository) {
    super();
    this.patientsRepository = patientsRepository;
    this.eHRRepository = eHRRepository;
    this.cliniciansRepository = cliniciansRepository;
    this.caregiversRepository = caregiversRepository;
  }

  @RequestMapping("/user")
  public Principal user(Principal principal) {
    return principal;
  }

  @RequestMapping(path = "precheck", consumes = "text/json")
  String checkUserValid(@RequestBody String userId) {
    return "Ok";
  }

  @RequestMapping(path = "add", method = RequestMethod.POST, consumes = "application/json")
  ResponseEntity<?> addAccount(@RequestParam Map<String, String> params, @RequestBody String input)
      throws JsonParseException, JsonMappingException, IOException {

		/*
     * curl test cmd:
		 */
    // curl -i -H "Content-Type: application/json;charset=UTF-8" -H "Accept:
    // application/json" -X POST --data '{"username":"ghq","id":null}'
    // http://127.0.0.1:8080/user/register?usertype=patient

    String usertype = params.getOrDefault(AccountBase.ACCOUNT_TYPE_PARAM_NAME, AccountBase.ACCOUNT_TYPE_PARAM_NAME);
    AccountBase result = null;
    ObjectMapper mapper = new ObjectMapper();
    logger.info("UserRestControllers: " + "addAccount " + "usertype=" + usertype);
    if (usertype.equals(Patient.ACCOUNT_TYPE)) {
      Patient patient = mapper.readValue(input, Patient.class);
      result = this.patientsRepository.save(patient);
    } else if (usertype.equals(Caregiver.ACCOUNT_TYPE)) {
      Caregiver caregiver = mapper.readValue(input, Caregiver.class);
      result = this.caregiversRepository.save(caregiver);
    } else if (usertype.equals(Clinician.ACCOUNT_TYPE)) {
      Clinician clinician = mapper.readValue(input, Clinician.class);
      result = this.cliniciansRepository.save(clinician);
    } else {
      throw new UnvalidUserTypeParamException(usertype);
    }

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri());
    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

  }

  @Deprecated
  @RequestMapping(path = "register", method = RequestMethod.POST, params = "usertype=Patient", consumes = "application/json")
  ResponseEntity<?> addPatient(@RequestBody Patient input) {

		/*
     * curl test cmd:
		 */
    // curl -i -H "Content-Type: application/json;charset=UTF-8" -H "Accept:
    // application/json" -X POST --data '{"username":"ghq","id":null}'
    // http://127.0.0.1:8080/user/register?usertype=patient

    logger.info("UserRestControllers: " + "addPatient " + "input=" + input.getUsername());
    Patient result = this.patientsRepository.save(input);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri());
    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

  }

  @Deprecated
  @RequestMapping(path = "register", method = RequestMethod.POST, params = "usertype=Clinician", consumes = "application/json")
  ResponseEntity<?> addClinician(@RequestBody Clinician input) {

		/*
		 * curl test cmd:
		 */
    // curl -i -H "Content-Type: application/json;charset=UTF-8" -H "Accept:
    // application/json" -X POST --data '{"username":"ghq","id":null}'
    // http://127.0.0.1:8080/user/register?usertype=patient

    logger.info("UserRestControllers" + "addClinician " + "input=" + input.getUsername());
    Clinician result = this.cliniciansRepository.save(input);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri());
    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

  }

  @Deprecated
  @RequestMapping(path = "register", method = RequestMethod.POST, params = "usertype=Caregiver", consumes = "application/json")
  ResponseEntity<?> addCaregiver(@RequestBody Caregiver input) {

		/*
		 * curl test cmd:
		 */
    // curl -i -H "Content-Type: application/json;charset=UTF-8" -H "Accept:
    // application/json" -X POST --data '{"username":"ghq","id":null}'
    // http://127.0.0.1:8080/user/register?usertype=patient

    logger.info("UserRestControllers" + "addCaregiver " + "input=" + input.getUsername());
    Caregiver result = this.caregiversRepository.save(input);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri());
    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

  }

  @RequestMapping(path = "register", method = RequestMethod.GET, produces = "application/json")
  Patient GetPatients() {

    return new Patient("name", "password");

  }

  private void isExsit(String userId) {
    if (!this.patientsRepository.findByUsername(userId).isPresent())
      throw new UserHasExsitException(userId);
  }
}

@ResponseStatus(HttpStatus.CONFLICT)
class UserHasExsitException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public UserHasExsitException(String userId) {
    super("could not find user '" + userId + "'.");
  }
}

@ResponseStatus(HttpStatus.CONFLICT)
class UnvalidUserTypeParamException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public UnvalidUserTypeParamException(String usertype) {
    super("unvalid user type param '" + usertype + "'.");
  }
}
