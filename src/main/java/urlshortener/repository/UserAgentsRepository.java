package urlshortener.repository;

import java.util.concurrent.ConcurrentHashMap;

public interface UserAgentsRepository {
    void updateInformation (String[] info);
    ConcurrentHashMap<String, Integer> devolverInformacion ();

}
