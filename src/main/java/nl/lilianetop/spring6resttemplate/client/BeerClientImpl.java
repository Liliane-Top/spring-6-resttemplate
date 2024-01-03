package nl.lilianetop.spring6resttemplate.client;

import lombok.RequiredArgsConstructor;
import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import nl.lilianetop.spring6resttemplate.model.BeerDTOPageImpl;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

  private final RestTemplateBuilder restTemplateBuilder;
  public static final String GET_BEER_PATH = "/api/v1/beer/";

  @Override
  public Page<BeerDTO> listBeers(String beerName) {
    RestTemplate restTemplate = restTemplateBuilder.build();

//    ResponseEntity<String>  stringResponse =
//        restTemplate.getForEntity(BASE_URL + GET_BEER_PATH, String.class);
//
//    ResponseEntity<Map>  mapResponse =
//        restTemplate.getForEntity(BASE_URL + GET_BEER_PATH, Map.class);
//
//    ResponseEntity<JsonNode>  jsonResponse =
//        restTemplate.getForEntity(BASE_URL + GET_BEER_PATH, JsonNode.class);
//
//    jsonResponse.getBody().findPath("content")
//            .elements().forEachRemaining(jsonNode -> {
//          System.out.println(jsonNode.get("beerName").asText());
//        });
//    System.out.println(stringResponse.getBody());

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

    if(beerName != null) {
      uriComponentsBuilder.queryParam("beerName", beerName);
    }

    ResponseEntity<BeerDTOPageImpl> pageResponseEntity =
        restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

    return pageResponseEntity.getBody();
  }
}
