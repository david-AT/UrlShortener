package urlshortener.repository.impl;

import net.glxn.qrgen.javase.QRCode;
import org.springframework.stereotype.Repository;
import urlshortener.repository.QRRepository;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class QRRepositoryImpl implements QRRepository {

  private static final ConcurrentHashMap<String, byte[]> qrs = new ConcurrentHashMap<>();

  public QRRepositoryImpl() { }

  public void anyadirQR (String clave, byte[] qrCode) {
    qrs.putIfAbsent(clave, qrCode);
  }

  // Función que genera el código QR a través de una URL
  public byte[] generateQRCodeImage(String barcodeText) throws Exception {
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

  public byte[] devolverQR(String clave) {
    return qrs.get(clave);
  }

}
