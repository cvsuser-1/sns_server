package restful.hello.restcontroller;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import restful.debug.exception.UserNotFoundException;
import restful.hello.bookmarks.AccountRepository;
import restful.hello.bookmarks.Bookmark;
import restful.hello.bookmarks.BookmarkRepository;

@RestController
@RequestMapping("/hello/restful/{userId}/bookmarks")
public class BookmarkRestController {

	private final BookmarkRepository bookmarkRepository;

	private final AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> add(@PathVariable String userId, @RequestBody Bookmark input) {
		this.validateUser(userId);
		/*
		 * curl test cmd: curl -i -H
		 * "Content-Type: application/json;charset=Unicode" -X POST --data
		 * '{"uri":"http://news.sina.com.cn","description":"新浪网新闻频道首页"}'
		 * http://127.0.0.1:8080/dsyer/bookmarks
		 * 
		 */

		return this.accountRepository.findByUsername(userId).map(account -> {
			Bookmark result = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(result.getId()).toUri());
			return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
		}).get();
	}

	@RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
	Bookmark readBookmark(@PathVariable String userId, @PathVariable Long bookmarkId) {
		this.validateUser(userId);
		return this.bookmarkRepository.findOne(bookmarkId);
	}

	@RequestMapping(method = RequestMethod.GET)
	Collection<Bookmark> readBookmarks(@PathVariable String userId) {
		this.validateUser(userId);
		return this.bookmarkRepository.findByAccountUsername(userId);
	}

	@Autowired
	BookmarkRestController(BookmarkRepository bookmarkRepository, AccountRepository accountRepository) {
		this.bookmarkRepository = bookmarkRepository;
		this.accountRepository = accountRepository;
	}

	private void validateUser(String userId) {

		this.accountRepository.findByUsername(userId).orElseThrow(() -> new UserNotFoundException(userId));
		//
		// boolean isEmpty =
		// this.accountRepository.findByUsername(userId).isEmpty();
		// if (isEmpty)
		// throw new UserNotFoundException(userId);
	}
}
