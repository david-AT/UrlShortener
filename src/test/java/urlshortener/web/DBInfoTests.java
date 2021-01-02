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

import jdk.javadoc.internal.doclets.toolkit.Content;
import urlshortener.domain.ShortURL;
import urlshortener.repository.AccesibleURLRepository;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.UserAgentsService;

public class DBInfoTests {

    private MockMvc mockMvc2;

    @Mock
    private ShortURLService shortUrlService;

    @InjectMocks
    private DBInfoController DBInfoController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc2 = MockMvcBuilders.standaloneSetup(DBInfoController).build();
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
    public void testBDInfo() throws Exception {
        configureSave(null);

        mockMvc2.perform(get("/DBInfo"))
                .andDo(print())
                .andExpect(content().string(""))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
