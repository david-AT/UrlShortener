package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.repository.UserAgentsRepository;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserAgentsService {
    private final UserAgentsRepository userAgentsRepository;

    public UserAgentsService(UserAgentsRepository userAgentsRepository) { this.userAgentsRepository = userAgentsRepository; }

    public ConcurrentHashMap<String, Integer> devolverInformacion() { return this.userAgentsRepository.devolverInformacion(); }

    public void updateInformation (String[] info) { userAgentsRepository.updateInformation(info); }
}
