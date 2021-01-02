package urlshortener.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.domain.ShortURL;
import urlshortener.repository.AccesibleURLRepository;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.UserAgentsService;

public class UserAgentsTests {
    private MockMvc mockMvc;

    private MockMvc mockMvc2;

    private static final ConcurrentHashMap<String, Integer> resumenInformativo = new ConcurrentHashMap<>();

    @Mock
    private ClickService clickService;

    @Mock
    private AccesibleURLRepository accesibleURLRepository;

    @Mock
    private ShortURLService shortUrlService;

    @Mock
    private UserAgentsService userAgentsService;

    @InjectMocks
    private UrlShortenerController urlShortener;

    @InjectMocks
    private UserAgentsController userAgentsController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
        this.mockMvc2 = MockMvcBuilders.standaloneSetup(userAgentsController).build();

        resumenInformativo.put("Chrome", 0);
        resumenInformativo.put("Firefox", 0);
        resumenInformativo.put("Internet", 0);
        resumenInformativo.put("Opera", 0);

        resumenInformativo.put("iOS", 0);
        resumenInformativo.put("Android", 0);
        resumenInformativo.put("Windows", 0);
        resumenInformativo.put("Linux", 0);
    }

    private void configureSave(String sponsor) {
        when(shortUrlService.save(any(), any(), any()))
                .then((Answer<ShortURL>) invocation -> new ShortURL(
                        "f684a3c4",
                        "http://example.com/",
                        URI.create("http://localhost/f684a3c4"),
                        sponsor,
                        null,
                        null,
                        0,
                        false,
                        null,
                        null));
    }

    @Test
    public void testUserAgents() throws Exception {
        configureSave(null);

        mockMvc.perform(get("/{id}", "f684a3c4").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"))
                .andDo(print());
        verify(userAgentsService).updateInformation(any());

        // We check that the endpoints returns the user agents information in JSON format
        when(userAgentsService.devolverInformacion()).thenReturn(resumenInformativo);
        mockMvc2.perform(get("/userAgents").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"))
                .andDo(print())
                .andExpect(content().json("{\"Linux\":\"Linux: 0\",\"Chrome\":\"Chrome: 0\",\"Windows\":\"Windows: 0\",\"iOS\":\"iOS: 0\",\"InternetExplorer\":\"Internet Explorer: 0\",\"Opera\":\"Opera: 0\",\"Firefox\":\"Firefox: 0\",\"Android\":\"Android: 0\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
