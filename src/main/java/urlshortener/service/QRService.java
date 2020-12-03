package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.repository.QRRepository;

@Service
public class QRService {
    private final QRRepository QRRepository;

    public QRService(QRRepository QRRepository) { this.QRRepository = QRRepository; }

    public byte[] devolverQR(String clave) { return QRRepository.devolverQR(clave); }

    public void anyadirQR (String clave, byte[] qrCode) { QRRepository.anyadirQR(clave, qrCode); }
}
