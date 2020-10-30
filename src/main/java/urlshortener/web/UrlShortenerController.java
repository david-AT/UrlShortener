package urlshortener.web;

import com.opencsv.CSVWriter;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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
                                            HttpServletRequest request) throws Exception {
    // Comprobar que la URL tiene una sintaxis correcta y que es también accesible (StatusCode_200)
    boolean esAccesible = esAccesibleURL(url);
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

  // Función encargada de coger el CSV subido por el usuario y acortar el contenido
  @RequestMapping(value = "/linkCSV", method = RequestMethod.POST)
  public ResponseEntity<String> shortenerCSV(@RequestParam("csv") MultipartFile csv,
                                            @RequestParam(value = "sponsor", required = false) String sponsor,
                                            HttpServletRequest request) 
                                            throws MultipartException, IllegalStateException, IOException{
    // Tratar las URLs
    String URLsExtracted = new String(csv.getBytes());
    if (URLsExtracted.equals("")) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    String[] URLs= URLsExtracted.split("\n");
    List<Boolean> accesibles = sonAccesiblesURLs(URLs);
    List<String> acortadas = acortarURLs(URLs,sponsor,request);

    // Crear el CSV nuevo
    StringWriter strW = new StringWriter();
    CSVWriter writeCSV = new CSVWriter(strW);
    for (int i = 0; i < URLs.length; i++){
      // TODO: Cuando se haga escalable, quitar el booleano de "accesibles"
      String[] newLine = {URLs[i],acortadas.get(i),accesibles.get(i).toString()};
      writeCSV.writeNext(newLine);
    }
    writeCSV.close();
    String result = strW.toString();

    // Devolver el CSV
    HttpHeaders h = new HttpHeaders();
    h.add(HttpHeaders.CONTENT_TYPE, "text/csv");
    h.add(HttpHeaders.CONTENT_LENGTH, Integer.toString(result.length()));
    h.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=URLsRecortadas.csv");
    return new ResponseEntity<>(result, h, HttpStatus.CREATED);
  }

}
