package urlshortener.web;

import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;


@RestController
public class UserAgentsController {
    private final HashMap<String, Integer> resumenInformativo;
    //--------------------------------CONSTRUCTOR--------------------------------

    public UserAgentsController() {
        resumenInformativo = new HashMap<>();

        resumenInformativo.put("Chrome", 0);
        resumenInformativo.put("Firefox", 0);
        resumenInformativo.put("Internet", 0);
        resumenInformativo.put("Opera", 0);

        resumenInformativo.put("iOS", 0);
        resumenInformativo.put("Android", 0);
        resumenInformativo.put("Windows", 0);
        resumenInformativo.put("Linux", 0);
    }

    //----------------------------FUNCIONES-PRIVADAS-----------------------------

    // Función encargada de actualizar la información de los navegadores y sistemas operativos que han usado la
    // funcionalidad userAgents
    private void updateInformation (String[] info) {
        StringBuilder browser = new StringBuilder();
        String aux = info[0];
        for ( int i = 0; !Character.toString(aux.charAt(i)).equals(" "); i++) {
            browser.append(aux.charAt(i));
        }

        StringBuilder so = new StringBuilder();
        aux = info[1];
        for (int i = 0; !Character.toString(aux.charAt(i)).equals(" "); i++) {
            so.append(aux.charAt(i));
        }

        int num = resumenInformativo.get(browser.toString());
        num++;
        resumenInformativo.replace(browser.toString(), num);

        num = resumenInformativo.get(so.toString());;
        num++;
        resumenInformativo.replace(so.toString(), num);
    }

    //----------------------------FUNCIONES-PÚBLICAS-----------------------------

    // Función encargada de devolver los agentes de usuario
    @RequestMapping(value = "/userAgents", method = RequestMethod.POST)
    public ResponseEntity<String[]> showUserAgents (@RequestHeader("User-Agent") String agents, HttpServletRequest request) {

        if ( agents != null ) {
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            String[] datos = {userAgent.getBrowser().getName(), userAgent.getOperatingSystem().getName()};
            updateInformation(datos);

            String[] info = {"Chrome: " + resumenInformativo.get("Chrome").toString(),
                             "Firefox: " + resumenInformativo.get("Firefox").toString(),
                             "Internet Explorer: " + resumenInformativo.get("Internet").toString(),
                             "Opera: " + resumenInformativo.get("Opera").toString(),
                             "iOS: " + resumenInformativo.get("iOS").toString(),
                             "Android: " + resumenInformativo.get("Android").toString(),
                             "Windows: " + resumenInformativo.get("Windows").toString(),
                             "Linux: " + resumenInformativo.get("Linux").toString()};

            return new ResponseEntity<>(info, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
