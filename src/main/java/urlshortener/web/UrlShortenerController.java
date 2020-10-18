package urlshortener.web;

import java.net.URI;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.MalformedURLException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

@RestController
public class UrlShortenerController {
  private final ShortURLService shortUrlService;

  private final ClickService clickService;

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
  }

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

  // Función auxiliar que comprueba si se puede establecer conexión con una URL.
  private boolean esAccesibleURL(String urlDir){
    try {
      URL url = new URL(urlDir);
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      int statusCode = http.getResponseCode();
      if (statusCode == 200) {
        return true;
      } else {
        return false;
      }
    } catch (IOException e) {
      return false;
    }
  }

  // Función auxiliar que comprueba si se puede establecer conexión con una URL (v2).
  private boolean esAccesibleURLv2(String urlDir){
    try {
      URL url = new URL(urlDir);
      URLConnection conn = url.openConnection();
      conn.connect();
      return true;
    } catch (MalformedURLException e) {
      // La URL está mal formada (error sintáctico)
      return false;
    } catch (IOException e) {
      // La URL no es accesible
      return false;
    }
  }

  @RequestMapping(value = "/link", method = RequestMethod.POST)
  public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                            @RequestParam(value = "sponsor", required = false)
                                                String sponsor,
                                            HttpServletRequest request) {
    // Comprobar que la URL tiene una sintaxis correcta
    UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
    // Comprobar que la URL es también accesible (Status Code 200)
    boolean esAccesible = esAccesibleURL(url);
    
    if (urlValidator.isValid(url) && esAccesible) {
      ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
      HttpHeaders h = new HttpHeaders();
      h.setLocation(su.getUri());
      return new ResponseEntity<>(su, h, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  private String extractIP(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
    HttpHeaders h = new HttpHeaders();
    h.setLocation(URI.create(l.getTarget()));
    return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
  }
}
