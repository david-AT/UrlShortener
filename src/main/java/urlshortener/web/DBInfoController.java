package urlshortener.web;

import java.util.*;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;

import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.ClickRepository;
import urlshortener.domain.Click;
import urlshortener.domain.ShortURL;

@RestController
public class DBInfoController {

  private final ShortURLRepository shortURLRepository;
  private final ClickRepository clickRepository;

  //--------------------------------CONSTRUCTOR--------------------------------

  public DBInfoController(ShortURLRepository shortURLRepository, ClickRepository clickRepository) {
    this.clickRepository = clickRepository;
    this.shortURLRepository = shortURLRepository;
  }

  //----------------------------FUNCIONES-PRIVADAS-----------------------------

  // Función que asocia a cada URL almacenada el nuemro de clicks asociados a ella
  private List<String> consultarBase() {
    List<String> data = new ArrayList<String>();
    List<ShortURL> listUrl = shortURLRepository.list();
    for (ShortURL s: listUrl){
      Long count = clickRepository.clicksByHash(s.getHash());
      data.add(s.getTarget() + " -> " + count);
    }
    return data;
  }

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

  // Función encargada de devolver la información de la Base de Datos (URL -> numClicks)
  @RequestMapping(value = "/DBInfo", method = RequestMethod.POST)
  public ResponseEntity<List<String>> getDBInfo (HttpServletRequest request) {
    // Leer info de la BD y combinarla
    List<String> data = consultarBase();
    return new ResponseEntity<>(data, HttpStatus.CREATED);
  }

}
