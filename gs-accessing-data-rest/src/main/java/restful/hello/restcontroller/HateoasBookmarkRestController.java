package restful.hello.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import restful.debug.exception.UserNotFoundException;
import restful.hello.bookmarks.AccountRepository;
import restful.hello.bookmarks.Bookmark;
import restful.hello.bookmarks.BookmarkRepository;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


class BookmarkResource extends ResourceSupport {

  private final Bookmark bookmark;

  public BookmarkResource(Bookmark bookmark) {
    String username = bookmark.getAccount().getUsername();
    this.bookmark = bookmark;
    this.add(new Link(bookmark.getUri(), "bookmark-uri"));
    this.add(linkTo(HateoasBookmarkRestController.class, username).withRel("bookmarks"));
    this.add(linkTo(methodOn(HateoasBookmarkRestController.class, username).readBookmark(username, bookmark.getId())).withSelfRel());
  }

  public Bookmark getBookmark() {
    return bookmark;
  }
}

@RestController
@RequestMapping("/hello/hateoas/{userId}/bookmarks")
public class HateoasBookmarkRestController {

  private final BookmarkRepository bookmarkRepository;

  private final AccountRepository accountRepository;

  @Autowired
  HateoasBookmarkRestController(BookmarkRepository bookmarkRepository,
                                AccountRepository accountRepository) {
    this.bookmarkRepository = bookmarkRepository;
    this.accountRepository = accountRepository;
  }

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<?> add(@PathVariable String userId, @RequestBody Bookmark input) {

    this.validateUser(userId);

    return accountRepository.findByUsername(userId)
        .map(account -> {
              Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

              HttpHeaders httpHeaders = new HttpHeaders();

              Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
              httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

              return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
            }
        ).get();
  }

  @RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
  BookmarkResource readBookmark(@PathVariable String userId, @PathVariable Long bookmarkId) {
    this.validateUser(userId);
    return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId));
  }

  @RequestMapping(method = RequestMethod.GET)
  Resources<BookmarkResource> readBookmarks(@PathVariable String userId) {

    this.validateUser(userId);

    List<BookmarkResource> bookmarkResourceList = bookmarkRepository.findByAccountUsername(userId)
        .stream()
        .map(BookmarkResource::new)
        .collect(Collectors.toList());
    return new Resources<BookmarkResource>(bookmarkResourceList);
  }

  private void validateUser(String userId) {
    this.accountRepository.findByUsername(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }
}

@ControllerAdvice
class BookmarkControllerAdvice {

  @ResponseBody
  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  VndErrors userNotFoundExceptionHandler(UserNotFoundException ex) {
    return new VndErrors("error", ex.getMessage());
  }
}
