package urlshortener.web;

import java.net.URI;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import com.opencsv.CSVWriter;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;

@RestController
public class UrlShortenerController {

  private final ShortURLService shortUrlService;
  private final ClickService clickService;

  //--------------------------------CONSTRUCTOR--------------------------------

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
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

  // Función auxiliar que comprueba si se puede establecer conexión con una URL.
  private boolean esAccesibleURL(String urlDir){
    try {
      UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
      boolean sintaxOK = urlValidator.isValid(urlDir);
      URL url = new URL(urlDir);
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      int statusCode = http.getResponseCode();
      if (sintaxOK && (statusCode == 200)) {
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

  // Función que comprueba si una lista de URLs es accesible
  private List<Boolean> sonAccesiblesURLs(String[] urlsDir){
    List<Boolean> list = new ArrayList<Boolean>();
    for (String url: urlsDir) {
      list.add(esAccesibleURL(url));
    }
    return list;
  }

  // Función que acorta una lista de URLs 
  private List<String> acortarURLs(String[] urlsDir,String sponsor,HttpServletRequest request){
    List<String> list = new ArrayList<String>();
    for (String url: urlsDir) {
      list.add(((shortUrlService.save(url, sponsor, request.getRemoteAddr())).getUri()).toString());
    }
    return list;
  }

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

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

  @RequestMapping(value = "/link", method = RequestMethod.POST)
  public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                            @RequestParam(value = "sponsor", required = false)
                                                String sponsor,
                                            HttpServletRequest request) {
    // Comprobar que la URL tiene una sintaxis correcta y que es también accesible (StatusCode_200)
    boolean esAccesible = esAccesibleURL(url);
    if (esAccesible) {
      ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
      HttpHeaders h = new HttpHeaders();
      h.setLocation(su.getUri());
      return new ResponseEntity<>(su, h, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/linkCSV", method = RequestMethod.POST)
  public ResponseEntity<String> shortenerCSV(@RequestParam("csv") MultipartFile csv,
                                            @RequestParam(value = "sponsor", required = false) String sponsor,
                                            HttpServletRequest request) 
                                            throws MultipartException, IllegalStateException, IOException{
    // Tratar las URLs
    String URLsExtracted = new String(csv.getBytes());
    String[] URLs= URLsExtracted.split("\n");
    List<Boolean> accesibles = sonAccesiblesURLs(URLs);
    List<String> acortadas = acortarURLs(URLs,sponsor,request);

    // Crear el CSV nuevo
    StringWriter strW = new StringWriter();
    CSVWriter writeCSV = new CSVWriter(strW);
    for (int i = 0; i < URLs.length; i++){
      String[] newLine = {URLs[i],acortadas.get(i)};
      writeCSV.writeNext(newLine);
    }
    writeCSV.close();
    String result = strW.toString();

    // Devolver el CSV
    HttpHeaders h = new HttpHeaders();
    h.add(HttpHeaders.CONTENT_TYPE, "text/csv");
    h.add(HttpHeaders.CONTENT_LENGTH, Integer.toString(result.length()));
    h.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.csv");
    return new ResponseEntity<>(result, h, HttpStatus.CREATED);
  }

}
