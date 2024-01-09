package nl.lilianetop.spring6resttemplate.client;

import static nl.lilianetop.spring6resttemplate.client.BeerClientImpl.GET_BEER_BY_ID_PATH;
import static nl.lilianetop.spring6resttemplate.client.BeerClientImpl.GET_BEER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import nl.lilianetop.spring6resttemplate.config.RestTemplateBuilderConfig;
import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import nl.lilianetop.spring6resttemplate.model.BeerDTOPageImpl;
import nl.lilianetop.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BierClientMockTest {

  public static final String URL = "http://localhost:8080";

  BeerClient beerClient;

  MockRestServiceServer server;

  @Autowired
  RestTemplateBuilder restTemplateBuilderConfigured;

  @Autowired
  ObjectMapper objectMapper;

  @Mock
  RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(
      new MockServerRestTemplateCustomizer());

  @BeforeEach
  void setUP() {
    //this RestTemplate is not a mock and being bindTo the mockserver.
    RestTemplate restTemplate = restTemplateBuilderConfigured.build();
    server = MockRestServiceServer.bindTo(restTemplate).build();

    // the RestTemplateBuilder is being mocked
    when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
    beerClient = new BeerClientImpl(mockRestTemplateBuilder);
  }

  @Test
  void getBeerById() throws JsonProcessingException {

    BeerDTO beerDTOTest = getBeerDto();
    String payLoad = objectMapper.writeValueAsString(beerDTOTest);

    server.expect(method(HttpMethod.GET))
        .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTOTest.getId()))
        .andRespond(withSuccess(payLoad, MediaType.APPLICATION_JSON));

    BeerDTO beerDTO = beerClient.getBeerById(beerDTOTest.getId());
    assertThat(beerDTO.getId()).isEqualTo(beerDTOTest.getId());
  }

  @Test
  void listBeers() throws JsonProcessingException {
//Jackson response from mock server?
    String payload = objectMapper.writeValueAsString(getPage());

    server.expect(method(HttpMethod.GET))
        .andExpect(requestTo(URL + GET_BEER_PATH))
        .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
    Page<BeerDTO> dtos = beerClient.listBeers();
    assertThat(dtos.getContent().size()).isPositive();
  }

  BeerDTO getBeerDto() {
    return BeerDTO.builder()
        .id(UUID.randomUUID())
        .price(new BigDecimal("10.99"))
        .beerName("Mango Bobs")
        .beerStyle(BeerStyle.ALE)
        .quantityOnHand(500)
        .upc("1234345")
        .build();
  }

  BeerDTOPageImpl getPage() {
    return new BeerDTOPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
  }
}
