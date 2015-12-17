package restful.common;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Caregiver extends AccountBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@OneToMany(mappedBy = "caregiver")
    private Set<Patient> patients = new HashSet<>();
    
    public Caregiver(String name, String password) {
        this.username = name;
        this.password = password;
    }

    Caregiver() { // jpa only
    }

	@Override
	public String toString() {
		return "Caregiver [patients=" + patients + ", password=" + password + ", username=" + username + ", id=" + id
				+ "]";
	}
    
}