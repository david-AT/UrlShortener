package urlshortener.repository.impl;

import org.springframework.stereotype.Repository;
import urlshortener.repository.QRRepository;

import java.util.concurrent.ConcurrentHashMap;


@Repository
public class QRRepositoryImpl implements QRRepository {

  private static final ConcurrentHashMap<String, byte[]> qrs = new ConcurrentHashMap<>();

  public QRRepositoryImpl() { }

  public void anyadirQR (String clave, byte[] qrCode) {
    qrs.putIfAbsent(clave, qrCode);
  }

  public byte[] devolverQR(String clave) {
    return qrs.get(clave);
  }

}
