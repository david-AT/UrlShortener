package urlshortener.web;

import net.glxn.qrgen.javase.QRCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.domain.ShortURL;
import urlshortener.repository.AccesibleURLRepository;
import urlshortener.service.ShortURLService;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@RestController
public class QRGeneratorController {
    private final ShortURLService shortUrlService;
    private final AccesibleURLRepository accesibleURLRepository;

    //--------------------------------CONSTRUCTOR--------------------------------

    public QRGeneratorController(ShortURLService shortUrlService, AccesibleURLRepository accesibleURLRepository) {
        this.shortUrlService = shortUrlService;
        this.accesibleURLRepository = accesibleURLRepository;
    }

    //----------------------------FUNCIONES-PRIVADAS-----------------------------

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

    // Función encargada de coger la URL acortada creada y generar un código QR con esa URL acortada
    @RequestMapping(value = "/linkQR", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> qrgenerator(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false)
                                                      String sponsor,
                                              HttpServletRequest request) throws Exception {

        // Comprobar que la URL tiene una sintaxis correcta y que es también accesible (StatusCode_200)
        boolean esAccesible = accesibleURLRepository.esAccesibleURL(url);
        if (esAccesible) {
            ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
            HttpHeaders h = new HttpHeaders();
            h.setLocation(su.getUri());

            // Pasamos de byte[] a base64 String
            String encodedQR = Base64.getEncoder().encodeToString(generateQRCodeImage(su.getUri().toString()));
            su.setQR(encodedQR);
            return new ResponseEntity<>(su, h, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
