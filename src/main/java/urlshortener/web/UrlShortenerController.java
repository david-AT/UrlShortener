package urlshortener.web;

import eu.bitwalker.useragentutils.UserAgent;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import urlshortener.domain.ShortURL;
import urlshortener.repository.AccesibleURLRepository;
import urlshortener.service.ClickService;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import urlshortener.service.QRService;
import urlshortener.service.ShortURLService;
import urlshortener.service.UserAgentsService;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minidev.json.JSONObject;


@RestController
public class UrlShortenerController {

  private final ShortURLService shortUrlService;
  private final ClickService clickService;
  private final AccesibleURLRepository accesibleURLRepository;
  private final UserAgentsService userAgentsService;
  private final QRService qrService;

  //--------------------------------CONSTRUCTOR--------------------------------

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, AccesibleURLRepository accesibleURLRepository,
                                UserAgentsService userAgentsService, QRService qrService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.userAgentsService = userAgentsService;
    this.accesibleURLRepository = accesibleURLRepository;
    this.qrService = qrService;
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
  public static byte[] generateQRCodeImage(String barcodeText) throws Exception {
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
    if ( agents != null ) {
      UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
      String[] datos = {userAgent.getBrowser().getName(), userAgent.getOperatingSystem().getName()};
      this.userAgentsService.updateInformation(datos);
    }

    if (l != null) {
      clickService.saveClick(id, extractIP(request));
      return createSuccessfulRedirectToResponse(l);
    } else {
      JSONObject info = new JSONObject();
      info.put("Error", "URL with ID " + id + " not validated yet");
      return new ResponseEntity<>(info, HttpStatus.NOT_FOUND);
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
        su.setquiereQR(true);
        URL crearURL = linkTo(methodOn(UrlShortenerController.class).darQR2(su.getHash())).toUri().toURL();
        su.setQR(crearURL);
        qrService.asyncGenerateQRCodeImage(su.getUri().toString(), su.getHash());
      }
      else {
        su.setquiereQR(false);
      }
      return new ResponseEntity<>(su, h, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  // FUnción encargada de devolver la imagen del QR
  @RequestMapping(value = "/qr/{id}", method = RequestMethod.GET, produces = "image/png")
  public ResponseEntity<byte[]> darQR2(@PathVariable String id) {
    try {
      URL crearURL = linkTo(methodOn(UrlShortenerController.class).redirectTo(id, null, null)).toUri().toURL();
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.setLocation(URI.create(crearURL.toString()));

      byte[] QRcode = qrService.devolverQR(id);

      if (QRcode == null) { // Si no se ha creado aún el QR, se crea.
        QRcode = generateQRCodeImage(crearURL.toString());
        qrService.anyadirQR(id, QRcode);
      }
      CacheControl cacheControl = CacheControl.maxAge(60*60*24*365, TimeUnit.SECONDS).noTransform().mustRevalidate();
      responseHeaders.setCacheControl(cacheControl.toString());

      responseHeaders.setContentType(MediaType.IMAGE_PNG);
      return new ResponseEntity<>(QRcode, responseHeaders, HttpStatus.OK);
    }
    catch(Exception e){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

}
