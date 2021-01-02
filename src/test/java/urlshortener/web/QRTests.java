package urlshortener.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.net.URI;
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
import urlshortener.service.QRService;
import urlshortener.service.ShortURLService;

public class QRTests {
    private MockMvc mockMvc;

    @Mock
    private ClickService clickService;

    @Mock
    private AccesibleURLRepository accesibleURLRepository;

    @Mock
    private ShortURLService shortUrlService;

    @Mock
    private QRService qrService;

    @InjectMocks
    private UrlShortenerController urlShortener;

    @InjectMocks
    private UserAgentsController userAgentsController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
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
    public void testQR() throws Exception {
        configureSave(null);
        when(accesibleURLRepository.esAccesibleURL(any())).thenReturn(true);
        when(qrService.devolverQR(any())).thenReturn(null);
        mockMvc.perform(post("/link").param("url", "http://example.com/").param("wantQR", "true"))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash", is("f684a3c4")))
                .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
                .andExpect(jsonPath("$.target", is("http://example.com/")))
                .andExpect(jsonPath("$.sponsor", is(nullValue())))
                .andExpect(jsonPath("$.qr", is("http://localhost/qr/f684a3c4")));

        mockMvc.perform(get("/qr/f684a3c4"))
                .andDo(print())
                .andExpect(content().bytes(UrlShortenerController.generateQRCodeImage("http://localhost/f684a3c4")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
