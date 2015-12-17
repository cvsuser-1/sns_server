package restful.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import restful.common.AccountBase;
import restful.common.Patient;
import restful.repositories.AccountBaseRepository;
import restful.repositories.CaregiversRepository;
import restful.repositories.ClinicianRepository;
import restful.repositories.EHRRepository;
import restful.repositories.PatientsRepository;

@RestController
@RequestMapping("/user")
public class UserRestControllers {

	private final AccountBaseRepository accountBaseRepository;
	private final PatientsRepository patientsRepository;
	private final EHRRepository eHRRepository;
	private final ClinicianRepository clinicianRepository;
	private final CaregiversRepository caregivesRepository;

	@Autowired
	public UserRestControllers(PatientsRepository patientsRepository, EHRRepository eHRRepository,
			ClinicianRepository clinicianRepository, CaregiversRepository caregivesRepository,
			AccountBaseRepository accountBaseRepository) {
		super();
		this.patientsRepository = patientsRepository;
		this.eHRRepository = eHRRepository;
		this.clinicianRepository = clinicianRepository;
		this.caregivesRepository = caregivesRepository;
		this.accountBaseRepository = accountBaseRepository;
	}

	@RequestMapping(path = "precheck", consumes = "text/json")
	String checkUserValid(@RequestBody String userId) {
		return "Ok";
	}

	@RequestMapping(path = "register", method = RequestMethod.POST, params = "usertype=patient", produces = "text/json")
	ResponseEntity<?> addPatient(@PathVariable String userId, @RequestBody Patient input) {

		/*
		 * curl test cmd: 
		 */
		// curl -i -H "Content-Type: text/json;charset=Unicode" -X POST --data '{"uri":"http://news.sina.com.cn","description":""}' http://127.0.0.1:8080/user/register?usertype=patient
		
		this.isExsit(userId);
		Patient result = this.patientsRepository.save(input);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(
				ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri());
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

	}

	private void isExsit(String userId) {
		if (!this.accountBaseRepository.findByUsername(userId).isPresent())
			throw new UserHasExsitException(userId);
	}

}

@ResponseStatus(HttpStatus.CONFLICT)
class UserHasExsitException extends RuntimeException {

	public UserHasExsitException(String userId) {
		super("could not find user '" + userId + "'.");
	}
}
