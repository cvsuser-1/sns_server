package restful.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class Patient extends AccountBase {

  public static final String ACCOUNT_TYPE = "Patient";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @OneToMany(mappedBy = "patient")
  private Set<ElectricHealthRecord> eHR = new HashSet<>();

  @ManyToOne
  private Clinician clinician;

  @ManyToOne
  private Caregiver caregiver;

  public Patient(String name, String password) {
    this.username = name;
    this.password = password;
  }

  Patient() { // jpa only
  }


  public Long getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public String toString() {
    return "Patient [eHR=" + eHR + ", clinician=" + clinician + ", caregiver=" + caregiver + ", password="
        + password + ", username=" + username + ", id=" + id + "]";
  }


}