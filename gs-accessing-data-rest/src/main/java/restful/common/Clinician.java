package restful.common;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Clinician extends AccountBase {

	public static final String ACCOUNT_TYPE = "Clinician";
    @OneToMany(mappedBy="clinician")
    private Set<Patient> patients = new HashSet<>();

    public Clinician(String name, String password) {
        this.username = name;
        this.password = password;
    }

    Clinician() { // jpa only
    }
}