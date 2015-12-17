package restful.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;



/**
 * @author teddy
 * 
 * account class both for client and server side.
 *
 */
@Entity
@Table(name = "PERSISTENCE_ACCOUNT")
public abstract class AccountBase implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonIgnore
    protected String password;
    protected String username;    
    
  
    @Id
    @GeneratedValue
    protected Long id;

    public AccountBase(String name, String password) {
        this.username = name;
        this.password = password;
    }

    AccountBase() { // jpa only
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
		// TODO Auto-generated method stub
		return super.toString();
	}
}