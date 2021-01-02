package urlshortener.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.fixtures.ShortURLFixture.someUrl;


import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.domain.ShortURL;
import urlshortener.repository.AccesibleURLRepository;
import urlshortener.repository.UserAgentsRepository;
import urlshortener.service.ClickService;
import urlshortener.service.QRService;
import urlshortener.service.ShortURLService;
import urlshortener.service.UserAgentsService;

public class UrlShortenerTests {

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
  private QRService qrService;

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

  @Test
  public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
      throws Exception {
    when(shortUrlService.findByKey("someKey")).thenReturn(someUrl());
    mockMvc.perform(get("/{id}", "someKey").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")).andDo(print())
        .andExpect(status().isTemporaryRedirect())
        .andExpect(redirectedUrl("http://example.com/"));
  }

  @Test
  public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist()
      throws Exception {
    when(shortUrlService.findByKey("someKey")).thenReturn(null);

    mockMvc.perform(get("/{id}", "someKey").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")).andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
    configureSave(null);
    when(accesibleURLRepository.esAccesibleURL(any())).thenReturn(true);
    mockMvc.perform(post("/link").param("url", "http://example.com/"))
        .andDo(print())
        .andExpect(redirectedUrl("http://localhost/f684a3c4"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.hash", is("f684a3c4")))
        .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
        .andExpect(jsonPath("$.target", is("http://example.com/")))
        .andExpect(jsonPath("$.sponsor", is(nullValue())));
  }

  @Test
  public void thatShortenerCreatesARedirectWithSponsor() throws Exception {
    configureSave("http://sponsor.com/");
    when(accesibleURLRepository.esAccesibleURL(any())).thenReturn(true);
    mockMvc.perform(
        post("/link").param("url", "http://example.com/").param(
            "sponsor", "http://sponsor.com/")).andDo(print())
        .andExpect(redirectedUrl("http://localhost/f684a3c4"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.hash", is("f684a3c4")))
        .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
        .andExpect(jsonPath("$.target", is("http://example.com/")))
        .andExpect(jsonPath("$.sponsor", is("http://sponsor.com/")));
  }

  @Test
  public void thatShortenerFailsIfTheURLisWrong() throws Exception {
    configureSave(null);

    mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
    when(shortUrlService.save(any(String.class), any(String.class), any(String.class)))
        .thenReturn(null);

    mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
        .andExpect(status().isBadRequest());
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
            //.andExpect(MockMvcResultMatchers.header().string("Cache-Control","max-age=31536000, must-revalidate, no-transform"));
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
