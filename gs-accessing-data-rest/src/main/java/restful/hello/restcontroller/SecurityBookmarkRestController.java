package restful.hello.restcontroller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import restful.debug.exception.UserNotFoundException;
import restful.hello.bookmarks.AccountRepository;
import restful.hello.bookmarks.Bookmark;
import restful.hello.bookmarks.BookmarkRepository;

//
// curl -X POST -vu android-bookmarks:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=password&username=jlong&grant_type=password&scope=write&client_secret=123456&client_id=android-bookmarks"
// curl -v POST http://127.0.0.1:8080/tags --data "tags=cows,dogs"  -H "Authorization: Bearer 66953496-fc5b-44d0-9210-b0521863ffcb"


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
		return (username) -> accountRepository
				.findByUsername(username)
				.map(a -> new User(a.username, a.password, true, true, true, true,
						AuthorityUtils.createAuthorityList("USER", "write")))
				.orElseThrow(
						() -> new UsernameNotFoundException("could not find the user '"
								+ username + "'"));
	}
}

@Configuration
@EnableResourceServer
@EnableAuthorizationServer
class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

	String applicationName = "bookmarks";

	// This is required for password grants, which we specify below as one of the
	// {@literal authorizedGrantTypes()}.
	@Autowired
	AuthenticationManagerBuilder authenticationManager;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws Exception {
		// Workaround for https://github.com/spring-projects/spring-boot/issues/1801
		endpoints.authenticationManager(new AuthenticationManager() {
			@Override
			public Authentication authenticate(Authentication authentication)
					throws AuthenticationException {
				return authenticationManager.getOrBuild().authenticate(authentication);
			}
		});
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		clients.inMemory().withClient("android-" + applicationName)
				.authorizedGrantTypes("password", "authorization_code", "refresh_token")
				.authorities("ROLE_USER").scopes("write").resourceIds(applicationName)
				.secret("123456");
	}
}

class BookmarkResourceS extends ResourceSupport {

	private final Bookmark bookmark;

	public BookmarkResourceS(Bookmark bookmark) {
		String username = bookmark.getAccount().getUsername();
		this.bookmark = bookmark;
		this.add(new Link(bookmark.uri, "bookmark-uri"));
		this.add(linkTo(SecurityBookmarkRestController.class, username).withRel("bookmarks"));
		this.add(linkTo(
				methodOn(SecurityBookmarkRestController.class, username).readBookmark(null,
						bookmark.getId())).withSelfRel());
	}

	public Bookmark getBookmark() {
		return bookmark;
	}
}

@RestController
@RequestMapping("/bookmarks")
public class SecurityBookmarkRestController {

	private final BookmarkRepository bookmarkRepository;

	private final AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> add(Principal principal, @RequestBody Bookmark input) {
		String userId = principal.getName();
		this.validateUser(userId);

		return accountRepository
				.findByUsername(userId)
				.map(account -> {
					Bookmark bookmark = bookmarkRepository.save(new Bookmark(account,
							input.uri, input.description));

					HttpHeaders httpHeaders = new HttpHeaders();

					Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
					httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

					return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
				}).get();
	}

	@RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
	BookmarkResource readBookmark(Principal principal, @PathVariable Long bookmarkId) {
		String userId = principal.getName();
		this.validateUser(userId);
		return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId));
	}

	@RequestMapping(method = RequestMethod.GET)
	Resources<BookmarkResource> readBookmarks(Principal principal) {
		String userId = principal.getName();
		this.validateUser(userId);

		List<BookmarkResource> bookmarkResourceList = bookmarkRepository
				.findByAccountUsername(userId).stream().map(BookmarkResource::new)
				.collect(Collectors.toList());
		return new Resources<BookmarkResource>(bookmarkResourceList);
	}

	@Autowired
	SecurityBookmarkRestController(BookmarkRepository bookmarkRepository,
			AccountRepository accountRepository) {
		this.bookmarkRepository = bookmarkRepository;
		this.accountRepository = accountRepository;
	}

	private void validateUser(String userId) {
		this.accountRepository.findByUsername(userId).orElseThrow(
				() -> new UserNotFoundException(userId));
	}
}

@ControllerAdvice
class BookmarkControllerAdviceS {

	@ResponseBody
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	VndErrors userNotFoundExceptionHandler(UserNotFoundException ex) {
		return new VndErrors("error", ex.getMessage());
	}
}
