package urlshortener.web;

import java.util.*;

import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.Info;

import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.ClickRepository;
import urlshortener.domain.ShortURL;

@Component
// Clase que personaliza en endpoint "/actuator/info"
public class DBInfoEndpoint implements InfoContributor{

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

  // Método @Override encargado de poner la info. de la BD (URL -> numClicks)
  // en el endpoint denominado "localhost:8080/actuator/info".
  @Override
  public void contribute(Info.Builder builder) {
    // Leer info de la BD y combinarla
    List<String> data = consultarBase();
    builder.withDetail("Information about App Data Base", data);
  }

}
