package urlshortener.repository.impl;

import java.io.File;
import java.net.URI;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Paths;
import java.util.*;
import java.io.StringWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import urlshortener.repository.AccesibleURLRepository;

@Repository
public class AccesibleURLRepositoryImpl implements AccesibleURLRepository {

  private static final Logger log = LoggerFactory
      .getLogger(AccesibleURLRepositoryImpl.class);

  private final JdbcTemplate jdbc;

  public AccesibleURLRepositoryImpl(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  // Función auxiliar que comprueba si se puede establecer conexión con una URL.
  public boolean esAccesibleURL(String urlDir){
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

  @Override
  // Función auxiliar que comprueba si se puede establecer conexión con una URL (v2).
  public boolean esAccesibleURLv2(String urlDir){
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

  @Override
  // Función que comprueba si una lista de URLs es accesible
  public List<Boolean> sonAccesiblesURLs(String[] urlsDir){
    List<Boolean> list = new ArrayList<Boolean>();
    for (String url: urlsDir) {
      list.add(esAccesibleURL(url));
    }
    return list;
  }
  
}
