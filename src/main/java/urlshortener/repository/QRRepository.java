package urlshortener.repository;


public interface QRRepository {
    byte[] devolverQR(String clave);
    byte[] generateQRCodeImage(String barcodeText) throws Exception;
    void anyadirQR (String clave, byte[] qrCode);
}
