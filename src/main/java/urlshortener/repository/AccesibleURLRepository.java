package urlshortener.repository;

import java.util.List;

public interface AccesibleURLRepository {

  // Función auxiliar que comprueba si se puede establecer conexión con una URL.
  boolean esAccesibleURL(String urlDir);

  // Función auxiliar que comprueba si se puede establecer conexión con una URL (v2).
  boolean esAccesibleURLv2(String urlDir);

  // Función que comprueba si una lista de URLs es accesible
  List<Boolean> sonAccesiblesURLs(String[] urlsDir);
  
}
