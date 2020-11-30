package urlshortener.web;

import eu.bitwalker.useragentutils.UserAgent;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.domain.ShortURL;
import urlshortener.repository.AccesibleURLRepository;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.UserAgentsService;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.util.concurrent.CompletableFuture;


@RestController
public class UrlShortenerController {

  private final ShortURLService shortUrlService;
  private final ClickService clickService;
  private final AccesibleURLRepository accesibleURLRepository;
  private final UserAgentsService userAgentsService;

  //--------------------------------CONSTRUCTOR--------------------------------

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, AccesibleURLRepository accesibleURLRepository,
                                UserAgentsService userAgentsService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.userAgentsService = userAgentsService;
    this.accesibleURLRepository = accesibleURLRepository;
  }

  //----------------------------FUNCIONES-PRIVADAS-----------------------------

  private String extractIP(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
    HttpHeaders h = new HttpHeaders();
    h.setLocation(URI.create(l.getTarget()));
    return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
  }

  // Función que genera el código QR a través de una URL
  private byte[] generateQRCodeImage(String barcodeText) throws Exception {
    ByteArrayOutputStream stream = QRCode
            .from(barcodeText)
            .withSize(200, 200)
            .stream();
    ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write( ImageIO.read(bis), "png", baos );
    baos.flush();
    byte[] imageInByte = baos.toByteArray();
    baos.close();
    return imageInByte;
  }

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

  // FUnción encargada de hacer la redirección (GET)
  @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
  public ResponseEntity<?> redirectTo(@PathVariable String id, @RequestHeader("User-Agent") String agents,
                                      HttpServletRequest request) {
    ShortURL l = shortUrlService.findByKey(id);
    if (l != null) {
      clickService.saveClick(id, extractIP(request));
      if ( agents != null ) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String[] datos = {userAgent.getBrowser().getName(), userAgent.getOperatingSystem().getName()};
        this.userAgentsService.updateInformation(datos);
      }
      return createSuccessfulRedirectToResponse(l);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  // Función encargada de coger la URL introducida por el usuario, acortarla y generar, si lo pide, un QR con su url.
  @RequestMapping(value = "/link", method = RequestMethod.POST)
  public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                            @RequestParam(value = "sponsor", required = false)
                                                String sponsor,
                                            @RequestParam(value = "wantQR", required = false)
                                                String quiereQR,
                                            HttpServletRequest request) throws Exception {
    // Comprobar que la URL tiene una sintaxis correcta y que es también accesible (StatusCode_200)
    boolean esAccesible = accesibleURLRepository.esAccesibleURL(url);
    if (esAccesible) {
      ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
      HttpHeaders h = new HttpHeaders();
      h.setLocation(su.getUri());
      if (quiereQR != null) {
        if (su.getQR() == null) {

          CompletableFuture<byte[]> completableFuture
                  = CompletableFuture.supplyAsync(() -> {
            try {
              return generateQRCodeImage(su.getUri().toString());
            }
            catch (Exception e) {
              e.printStackTrace();
            }
            return null;
          });

          CompletableFuture<byte[]> future = completableFuture
                  .thenApply(s -> su.setQR(s));

          Thread generarQRAsync = new Thread(() -> {
            try {
              su.setQR(generateQRCodeImage(su.getUri().toString()));
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          });
          generarQRAsync.start();
        }
      }
      return new ResponseEntity<>(su, h, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

}
