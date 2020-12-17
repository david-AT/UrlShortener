package urlshortener.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.fixtures.ShortURLFixture.someUrl;


import java.net.URI;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.domain.ShortURL;
import urlshortener.repository.AccesibleURLRepository;
import urlshortener.service.ClickService;
import urlshortener.service.QRService;
import urlshortener.service.ShortURLService;

public class UrlShortenerTests {

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

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
  }

  @Test
  @Ignore
  public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
      throws Exception {
    when(shortUrlService.findByKey("someKey")).thenReturn(someUrl());

    mockMvc.perform(get("/{id}", "someKey")).andDo(print())
        .andExpect(status().isTemporaryRedirect())
        .andExpect(redirectedUrl("http://example.com/"));
  }

  @Test
  @Ignore
  public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist()
      throws Exception {
    when(shortUrlService.findByKey("someKey")).thenReturn(null);

    mockMvc.perform(get("/{id}", "someKey")).andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @Ignore
  public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
    configureSave(null);

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
  @Ignore
  public void thatShortenerCreatesARedirectWithSponsor() throws Exception {
    configureSave("http://sponsor.com/");

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
            .andExpect(content().bytes(UrlShortenerController.generateQRCodeImage("http://localhost/f684a3c4")));
  }
}
