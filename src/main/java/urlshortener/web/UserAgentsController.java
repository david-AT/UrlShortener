package urlshortener.web;

import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
public class UserAgentsController {
    private String browser;
    private String operativeSystem;
    //--------------------------------CONSTRUCTOR--------------------------------

    public UserAgentsController() { }

    //----------------------------FUNCIONES-PRIVADAS-----------------------------
    private void setAttributes(String browser, String operativeSystem) {
        this.browser = browser;
        this.operativeSystem = operativeSystem;
    }


    //----------------------------FUNCIONES-PÚBLICAS-----------------------------

    // Función encargada de devolver los agentes de usuario
    @RequestMapping(value = "/userAgents", method = RequestMethod.POST)
    public ResponseEntity<String[]> showUserAgents (@RequestHeader("User-Agent") String agents, HttpServletRequest request) {
        if ( agents != null ) {
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            String[] datos = {userAgent.getBrowser().getName(), userAgent.getOperatingSystem().getName()};
            return new ResponseEntity<>(datos, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
