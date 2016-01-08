package restful;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

import restful.hello.bookmarks.Account;
import restful.hello.bookmarks.AccountRepository;
import restful.hello.bookmarks.Bookmark;
import restful.hello.bookmarks.BookmarkRepository;

@SpringBootApplication
public class PLMApplication /*extends SpringBootServletInitializer*/ {
//  @Override
//  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//    return super.configure(builder);
//  }

  public static void main(String[] args) {
    SpringApplication.run(PLMApplication.class, args);
  }

  @Bean
  CommandLineRunner init(AccountRepository accountRepository, BookmarkRepository bookmarkRepository) {
    return (evt) -> Arrays.asList("jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
        .forEach(a -> {
          Account account = accountRepository.save(new Account(a, "password"));
          bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + a, "A description"));
          bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + a, "A description"));
        });
  }

}