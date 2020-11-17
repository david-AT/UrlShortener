package urlshortener.web;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.StringWriter;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// ---------------
import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import javax.xml.ws.Endpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.ResponseBody;
// ---------------

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;

import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.ClickRepository;
import urlshortener.domain.Click;
import urlshortener.domain.ShortURL;

@Component
@RestControllerEndpoint(id = "dbinfo-endpoint")
public class DBInfoEndpoint {

  private final ShortURLRepository shortURLRepository;
  private final ClickRepository clickRepository;

  //--------------------------------CONSTRUCTOR--------------------------------

  public DBInfoEndpoint(ShortURLRepository shortURLRepository, ClickRepository clickRepository) {
    this.clickRepository = clickRepository;
    this.shortURLRepository = shortURLRepository;
  }

  //----------------------------FUNCIONES-PRIVADAS-----------------------------

  // Función que asocia a cada URL almacenada el nuemro de clicks asociados a ella
  private List<String> consultarBase() {
    List<String> data = new ArrayList<String>();
    List<ShortURL> listUrl = shortURLRepository.list();
    String aux = "";
    for (ShortURL s: listUrl){
      Long count = clickRepository.clicksByHash(s.getHash());
      aux = "(" + Long.toString(count) + " clicks) " + s.getTarget();
      data.add(aux);
    }
    Collections.sort(data, Collections.reverseOrder());
    return data;
  }

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

  // Función encargada de devolver la información de la Base de Datos (URL -> numClicks)
  @GetMapping("/dbinfo")
  public ResponseEntity<List<String>> getDBInfo() {
    // Leer info de la BD y combinarla
    List<String> data = consultarBase();
    return new ResponseEntity<List<String>>(data, HttpStatus.CREATED);
  }

}
