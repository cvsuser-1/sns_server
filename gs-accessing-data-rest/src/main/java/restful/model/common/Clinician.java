package restful.model.common;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;


@Entity
public class Clinician extends AccountBase {

  public static final String ACCOUNT_TYPE = "Clinician";
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  @OneToMany(mappedBy = "clinician")
  private Set<Patient> patients = new HashSet<>();

  public Clinician(String name, String password) {
    this.username = name;
    this.password = password;
  }

  Clinician() { // jpa only
  }
}