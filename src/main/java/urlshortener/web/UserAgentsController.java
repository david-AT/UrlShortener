package urlshortener.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import urlshortener.service.UserAgentsService;

import java.util.concurrent.ConcurrentHashMap;

@RestController
public class UserAgentsController {

    private final UserAgentsService userAgentsService;

    //--------------------------------CONSTRUCTOR--------------------------------

    public UserAgentsController( UserAgentsService userAgentsService ) { this.userAgentsService = userAgentsService; }

    //----------------------------FUNCIONES-PRIVADAS-----------------------------


    //----------------------------FUNCIONES-PÚBLICAS-----------------------------

    // Función encargada de devolver los agentes de usuario
    @RequestMapping(value = "/userAgents", method = RequestMethod.GET)
    public ResponseEntity<String[]> showUserAgents (@RequestHeader("User-Agent") String agents, HttpServletRequest request) {

        ConcurrentHashMap<String, Integer> resumenInformativo = this.userAgentsService.devolverInformacion();

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
}
