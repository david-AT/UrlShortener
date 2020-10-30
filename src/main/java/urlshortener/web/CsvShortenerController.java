package urlshortener.web;

import java.io.File;
import java.net.URI;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Paths;
import java.util.*;
import java.io.StringWriter;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.opencsv.CSVWriter;

import java.net.MalformedURLException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.repository.AccesibleURLRepository;

@RestController
public class CsvShortenerController {

  private final ShortURLService shortUrlService;
  private final ClickService clickService;
  private final AccesibleURLRepository accesibleURLRepository;

  //--------------------------------CONSTRUCTOR--------------------------------

  public CsvShortenerController(ShortURLService shortUrlService, ClickService clickService, 
                                AccesibleURLRepository accesibleURLRepository) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.accesibleURLRepository = accesibleURLRepository;
  }

  //----------------------------FUNCIONES-PRIVADAS-----------------------------
 
  // Función que acorta una lista de URLs 
  private List<String> acortarURLs(String[] urlsDir,String sponsor,HttpServletRequest request){
    List<String> list = new ArrayList<String>();
    for (String url: urlsDir) {
      list.add(((shortUrlService.save(url, sponsor, request.getRemoteAddr())).getUri()).toString());
    }
    return list;
  }

  // Crea un nuevo CSV con las URLs acortadas
  private static String crearCSV (String[] URLs, List<Boolean> accesibles, 
                                   List<String> acortadas) throws IOException {
    StringWriter strW = new StringWriter();
    CSVWriter writeCSV = new CSVWriter(strW);
    for (int i = 0; i < URLs.length; i++){
      // TODO: Cuando se haga escalable, quitar el booleano de "accesibles"
      String[] newLine = {URLs[i],acortadas.get(i),accesibles.get(i).toString()};
      writeCSV.writeNext(newLine);
    }
    writeCSV.close();
    return strW.toString();
  }

  //----------------------------FUNCIONES-PÚBLICAS-----------------------------

  // Función encargada de coger el CSV subido por el usuario y acortar el contenido
  @RequestMapping(value = "/linkCSV", method = RequestMethod.POST)
  public ResponseEntity<String> shortenerCSV(@RequestParam("csv") MultipartFile csv,
                                            @RequestParam(value = "sponsor", required = false) String sponsor,
                                            HttpServletRequest request) 
                                            throws MultipartException, IllegalStateException, IOException {
    // Extraer las URLs del CSV
    String URLsExtracted = new String(csv.getBytes());
    if (URLsExtracted.equals("")) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    String[] URLs = URLsExtracted.split("\n"); 

    // Tratar las URLs extraidas
    List<Boolean> accesibles = accesibleURLRepository.sonAccesiblesURLs(URLs);
    List<String> acortadas = acortarURLs(URLs,sponsor,request);

    // Crear el CSV nuevo
    String result = crearCSV(URLs,accesibles,acortadas);

    // Devolver el CSV creado
    HttpHeaders h = new HttpHeaders();
    h.add(HttpHeaders.CONTENT_TYPE, "text/csv");
    h.add(HttpHeaders.CONTENT_LENGTH, Integer.toString(result.length()));
    h.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=URLsRecortadas.csv");
    return new ResponseEntity<>(result, h, HttpStatus.CREATED);
  }

}
