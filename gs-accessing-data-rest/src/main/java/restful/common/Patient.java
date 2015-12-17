package restful.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Patient extends AccountBase {

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
}