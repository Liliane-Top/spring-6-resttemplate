package nl.lilianetop.spring6resttemplate.client;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import nl.lilianetop.spring6resttemplate.model.BeerDTOPageImpl;
import nl.lilianetop.spring6resttemplate.model.BeerStyle;
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
  public static final String GET_BEER_PATH = "/api/v1/beer";
  public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";


  @Override
  public BeerDTO getBeerById(UUID beerId) {
    RestTemplate restTemplate = restTemplateBuilder.build();

    return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
  }

  @Override
  public BeerDTO createBeer(BeerDTO newDto) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    //how to get the property location from the headers?
    URI uri = restTemplate.postForLocation(GET_BEER_PATH, newDto);
    return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
  }

  @Override
  public BeerDTO updateBeer(BeerDTO beerDto) {

    RestTemplate restTemplate = restTemplateBuilder.build();
    restTemplate.put(GET_BEER_BY_ID_PATH, beerDto, beerDto.getId());

    return getBeerById(beerDto.getId());
  }

  @Override
  public void deleteBeer(UUID beerId) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    restTemplate.delete(GET_BEER_BY_ID_PATH, beerId);
  }

  @Override
  public Page<BeerDTO> listBeers() {
    return listBeers(null, null, null, null, null);
  }

  @Override
  public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle,
      Boolean showInventory, Integer pageNumber, Integer pageSize) {
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

    if (beerName != null) {
      uriComponentsBuilder.queryParam("beerName", beerName);
    }

    if (beerStyle != null) {
      uriComponentsBuilder.queryParam("beerStyle", beerStyle);
    }

    if (showInventory != null) {
      uriComponentsBuilder.queryParam("showInventory", showInventory);
    }

    if (pageNumber != null) {
      uriComponentsBuilder.queryParam("pageNumber", pageNumber);
    }

    if (pageSize != null) {
      uriComponentsBuilder.queryParam("pageSize", pageSize);
    }

    ResponseEntity<BeerDTOPageImpl> pageResponseEntity =
        restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

    return pageResponseEntity.getBody();
  }

}
