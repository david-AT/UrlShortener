package urlshortener.repository.impl;

import org.springframework.stereotype.Repository;
import urlshortener.repository.UserAgentsRepository;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserAgentsRepositoryImpl implements UserAgentsRepository {

  private static final ConcurrentHashMap<String, Integer> resumenInformativo = new ConcurrentHashMap<>();

  public UserAgentsRepositoryImpl() {
    resumenInformativo.put("Chrome", 0);
    resumenInformativo.put("Firefox", 0);
    resumenInformativo.put("Internet", 0);
    resumenInformativo.put("Opera", 0);

    resumenInformativo.put("iOS", 0);
    resumenInformativo.put("Android", 0);
    resumenInformativo.put("Windows", 0);
    resumenInformativo.put("Linux", 0);
  }

  // Función encargada de actualizar la información de los navegadores y sistemas operativos que han usado la
  // funcionalidad userAgents
  public void updateInformation (String[] info) {
    String check1 = info[0];
    boolean esValido1 = false;
    if (check1.contains("Chrome")) {
      esValido1 = true;
    }
    else if (check1.contains("Firefox")) {
      esValido1 = true;
    }
    else if (check1.contains("Internet")) {
      esValido1 = true;
    }
    else if (check1.contains("Opera")) {
      esValido1 = true;
    }

    StringBuilder browser = new StringBuilder();
    String aux = info[0];
    for ( int i = 0; esValido1 && !Character.toString(aux.charAt(i)).equals(" "); i++) {
      browser.append(aux.charAt(i));
    }

    String check2 = info[1];
    boolean esValido2 = false;
    if (check2.contains("iOS")) {
      esValido2 = true;
    }
    else if (check2.contains("Android")) {
      esValido2 = true;
    }
    else if (check2.contains("Windows")) {
      esValido2 = true;
    }
    else if (check2.contains("Linux")) {
      esValido2 = true;
    }

    StringBuilder so = new StringBuilder();
    aux = info[1];
    for (int i = 0; esValido2 && !Character.toString(aux.charAt(i)).equals(" "); i++) {
      so.append(aux.charAt(i));
    }

    resumenInformativo.computeIfPresent(browser.toString(), (key , val)  -> val + 1);
    resumenInformativo.computeIfPresent(so.toString(), (key , val)  -> val + 1);
  }

  public ConcurrentHashMap<String, Integer> devolverInformacion () {
    return resumenInformativo;
  }

}
