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

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

  // Función encargada de devolver la información de la Base de Datos
  @RequestMapping(value = "/DBInfo", method = RequestMethod.POST)
  public ResponseEntity<List<Click>> getDBInfo (@RequestParam("num") int num, HttpServletRequest request) {
    try {
      List<Click> listClick = clickRepository.list();
      List<ShortURL> listUrl = shortURLRepository.list();
      for (ShortURL s: listUrl){
        System.out.println(s.getHash());
      }
      System.out.println("--------");
      for (Click c: listClick){
        System.out.println(c.getHash());
      }
      return new ResponseEntity<>(listClick, HttpStatus.CREATED);
    } catch (NullPointerException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

}
