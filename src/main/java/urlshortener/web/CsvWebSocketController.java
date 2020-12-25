package urlshortener.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import java.util.Random;

import urlshortener.domain.Reply;
import urlshortener.domain.URLMessage;

import urlshortener.service.ShortURLService;
import urlshortener.repository.AccesibleURLRepository;

@Controller
public class CsvWebSocketController {

  private final ShortURLService shortUrlService;
  private final AccesibleURLRepository accesibleURLRepository;

  //--------------------------------CONSTRUCTOR--------------------------------

  public CsvWebSocketController(ShortURLService shortUrlService,
                                AccesibleURLRepository accesibleURLRepository) {
    this.shortUrlService = shortUrlService;
    this.accesibleURLRepository = accesibleURLRepository;
  }

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

  @MessageMapping("/websocketServer")
  @SendToUser("/topic/websocketClient")
  public Reply shortURL(URLMessage message) throws Exception {

    // Test URL accesible
    boolean accesible = accesibleURLRepository.esAccesibleURL(message.getName());

    // Short the URL
    String shorted = "";
    if (accesible) {
      shorted = ((shortUrlService.save(message.getName(), "", "")).getUri()).toString() + "," + message.getName() + ",VALID";
    }else{
      shorted = "NULL," + message.getName() + ",INVALID";
    }

    // Return result
    return new Reply(shorted);
  }
}