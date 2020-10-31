package urlshortener.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.domain.ShortURL;
import urlshortener.repository.AccesibleURLRepository;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import javax.servlet.http.HttpServletRequest;
import java.net.*;


@RestController
public class UrlShortenerController {

  private final ShortURLService shortUrlService;
  private final ClickService clickService;
  private final AccesibleURLRepository accesibleURLRepository;

  //--------------------------------CONSTRUCTOR--------------------------------

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, AccesibleURLRepository accesibleURLRepository) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.accesibleURLRepository = accesibleURLRepository;
  }

  //----------------------------FUNCIONES-PRIVADAS-----------------------------

  private String extractIP(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
    HttpHeaders h = new HttpHeaders();
    h.setLocation(URI.create(l.getTarget()));
    return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
  }

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

  // FUnción encargada de hacer la redirección (GET)
  @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
  public ResponseEntity<?> redirectTo(@PathVariable String id,
                                      HttpServletRequest request) {
    ShortURL l = shortUrlService.findByKey(id);
    if (l != null) {
      clickService.saveClick(id, extractIP(request));
      return createSuccessfulRedirectToResponse(l);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  // Función encargada de coger la URL introducida por el usuario y acortarla
  @RequestMapping(value = "/link", method = RequestMethod.POST)
  public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                            @RequestParam(value = "sponsor", required = false)
                                                String sponsor,
                                            HttpServletRequest request) {
    // Comprobar que la URL tiene una sintaxis correcta y que es también accesible (StatusCode_200)
    boolean esAccesible = accesibleURLRepository.esAccesibleURL(url);
    if (esAccesible) {
      ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
      HttpHeaders h = new HttpHeaders();
      h.setLocation(su.getUri());
      return new ResponseEntity<>(su, h, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

}
