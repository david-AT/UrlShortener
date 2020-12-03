package urlshortener.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import java.util.Random;

import urlshortener.domain.Reply;
import urlshortener.domain.URLMessage;

import urlshortener.service.ShortURLService;
import urlshortener.repository.AccesibleURLRepository;

@Controller
public class WebSocketController {

  private final ShortURLService shortUrlService;
  private final AccesibleURLRepository accesibleURLRepository;

  //--------------------------------CONSTRUCTOR--------------------------------

  public WebSocketController(ShortURLService shortUrlService,
                                AccesibleURLRepository accesibleURLRepository) {
    this.shortUrlService = shortUrlService;
    this.accesibleURLRepository = accesibleURLRepository;
  }

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

  @MessageMapping("/websocketServer")
  @SendTo("/topic/websocketClient")
  public Reply shortURL(URLMessage message) throws Exception {
    
    // Delay simulado entre 2 y 5 segundos.
    // Random r = new Random();
    // int valorDado = r.nextInt(3000)+2000;
    // Thread.sleep(valorDado);

    // Test URL accesible
    boolean accesible = accesibleURLRepository.esAccesibleURL(message.getName());

    // Short the URL
    String shorted = "";
    if (accesible) {
      shorted = ((shortUrlService.save(message.getName(), "", "")).getUri()).toString() + ", VALID";
    }else{
      shorted = message.getName() + ", INVALID";
    }

    // Return result
    return new Reply(shorted);
  }
}