package restful.hello.bookmarks;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class Account {

  @JsonIgnore
  public String password;
  public String username;

  @OneToMany(mappedBy = "account")
  private Set<Bookmark> bookmarks = new HashSet<>();

  @Id
  @GeneratedValue
  private Long id;

  public Account(String name, String password) {
    this.username = name;
    this.password = password;
  }

  Account() { // jpa only
  }

  public Set<Bookmark> getBookmarks() {
    return bookmarks;
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