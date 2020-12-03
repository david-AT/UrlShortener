package urlshortener.repository;


public interface QRRepository {
    byte[] devolverQR(String clave);
    void anyadirQR (String clave, byte[] qrCode);
}
