package urlshortener.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import java.util.Random;

import urlshortener.domain.Reply;
import urlshortener.domain.URLMessage;

@Controller
public class WebSocketController {


  @MessageMapping("/websocketServer")
  @SendTo("/topic/websocketClient")
  public Reply shortURL(URLMessage message) throws Exception {
    // Delay simulado entre 2 y 5 segundos.
    // Random r = new Random();
    // int valorDado = r.nextInt(3000)+2000;
    // Thread.sleep(valorDado);
    String aux = message.getName() + ", VUELTA";
    return new Reply(aux);
  }
}