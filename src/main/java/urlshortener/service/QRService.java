package urlshortener.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import urlshortener.repository.QRRepository;

@Service
@EnableAsync
public class QRService {
    private final QRRepository QRRepository;

    public QRService(QRRepository QRRepository) { this.QRRepository = QRRepository; }

    public byte[] devolverQR(String clave) { return QRRepository.devolverQR(clave); }

    @Async
    public void asyncGenerateQRCodeImage(String barcodeText, String clave) throws Exception {
        byte[] QRcode =  QRRepository.generateQRCodeImage(barcodeText);
        QRRepository.anyadirQR(clave, QRcode);
    }

    public void anyadirQR (String clave, byte[] qrCode) { QRRepository.anyadirQR(clave, qrCode); }
}
