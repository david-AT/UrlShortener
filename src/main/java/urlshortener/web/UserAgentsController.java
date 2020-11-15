package urlshortener.web;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import urlshortener.service.UserAgentsService;

import java.util.concurrent.ConcurrentHashMap;

import static com.jayway.jsonpath.internal.function.ParamType.JSON;

@RestController
public class UserAgentsController {

    private final UserAgentsService userAgentsService;

    //--------------------------------CONSTRUCTOR--------------------------------

    public UserAgentsController( UserAgentsService userAgentsService ) { this.userAgentsService = userAgentsService; }

    //----------------------------FUNCIONES-PRIVADAS-----------------------------


    //----------------------------FUNCIONES-PÚBLICAS-----------------------------

    // Función encargada de devolver los agentes de usuario
    @RequestMapping(value = "/userAgents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> showUserAgents () {

        ConcurrentHashMap<String, Integer> resumenInformativo = this.userAgentsService.devolverInformacion();

        if ( resumenInformativo != null ) {
            JSONObject info = new JSONObject();
            info.put("Chrome", "Chrome: " + resumenInformativo.get("Chrome").toString());
            info.put("Firefox", "Firefox: " + resumenInformativo.get("Firefox").toString());
            info.put("InternetExplorer", "Internet Explorer: " + resumenInformativo.get("Internet").toString());
            info.put("Opera", "Opera: " + resumenInformativo.get("Opera").toString());
            info.put("iOS", "iOS: " + resumenInformativo.get("iOS").toString());
            info.put("Android", "Android: " + resumenInformativo.get("Android").toString());
            info.put("Windows", "Windows: " + resumenInformativo.get("Windows").toString());
            info.put("Linux", "Linux: " + resumenInformativo.get("Linux").toString());
            return new ResponseEntity<>(info, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
