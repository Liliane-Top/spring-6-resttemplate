package nl.lilianetop.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.lilianetop.spring6resttemplate.config.OAuthClientInterceptor;
import nl.lilianetop.spring6resttemplate.config.RestTemplateBuilderConfig;
import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import nl.lilianetop.spring6resttemplate.model.BeerDTOPageImpl;
import nl.lilianetop.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static nl.lilianetop.spring6resttemplate.client.BeerClientImpl.GET_BEER_BY_ID_PATH;
import static nl.lilianetop.spring6resttemplate.client.BeerClientImpl.GET_BEER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;


@RestClientTest
class BierClientRestTemplateMockTest {

    public static final String URL = "http://localhost:8080";
    private static final String TOKEN_VALUE = "Bearer testToken";

    BeerClient beerClient;
    MockRestServiceServer server;
    BeerDTO beerDTOTestObject;
    String beerDTOJson;

    @Autowired
    RestTemplateBuilder restTemplateBuilderConfigured;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;
    @Mock
    RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(
            new MockServerRestTemplateCustomizer());

    @MockBean
    OAuth2AuthorizedClientManager manager;

    @TestConfiguration
    @Import(RestTemplateBuilderConfig.class)
    public static class TestConfig {
        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryClientRegistrationRepository(ClientRegistration
                    .withRegistrationId("springauth")
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .clientId("testClientId")
                    .tokenUri("testToken")
                    .build());
        }

        @Bean
        OAuth2AuthorizedClientService oAuth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
            return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        }

        @Bean
        OAuthClientInterceptor oAuthClientInterceptor(OAuth2AuthorizedClientManager manager,
                                                      ClientRegistrationRepository clientRegistrationRepository) {
            return new OAuthClientInterceptor(manager, clientRegistrationRepository);
        }
    }

    @BeforeEach
    void setUP() throws JsonProcessingException {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("springauth");
        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "testToken",
                Instant.MIN, Instant.MAX);

        when(manager.authorize(any())).thenReturn(new OAuth2AuthorizedClient
                (clientRegistration, "testClientId", token));

        RestTemplate restTemplate = restTemplateBuilderConfigured.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);

        beerClient = new BeerClientImpl(mockRestTemplateBuilder);
        beerDTOTestObject = getBeerDto();
        beerDTOJson = objectMapper.writeValueAsString(beerDTOTestObject);
    }

    @Test
    void testListBeersWithQueryParam() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(getPage());

        URI uri = UriComponentsBuilder.fromHttpUrl(URL + GET_BEER_PATH)
                .queryParam("beerName", "ALE")
                .build()
                .toUri();

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                .andExpect(header("Authorization", TOKEN_VALUE))
                .andExpect(queryParam("beerName", "ALE"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Page<BeerDTO> responsePage = beerClient
                .listBeers("ALE", null, null, null, null);

        assertThat(responsePage.getContent().size()).isEqualTo(1);
    }

    @Test
    void deleteBeerNotFound() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTOTestObject.getId()))
                .andExpect(header("Authorization", TOKEN_VALUE))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class, () -> beerClient.deleteBeer(beerDTOTestObject.getId()));

        server.verify();
    }

    @Test
    void deleteBeer() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTOTestObject.getId()))
                .andExpect(header("Authorization", TOKEN_VALUE))
                .andRespond(withNoContent());

        beerClient.deleteBeer(beerDTOTestObject.getId());
        server.verify();
    }

    @Test
    void updateBeer() {
        server.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTOTestObject.getId()))
                .andExpect(header("Authorization", TOKEN_VALUE))
                .andRespond(withNoContent());

        mockGetOperation();

        BeerDTO beerDTOResponse = beerClient.updateBeer(beerDTOTestObject);
        assertThat(beerDTOResponse.getBeerName()).isEqualTo("Mango Bobs");
    }

    @Test
    void createBeer() {
        URI uri = UriComponentsBuilder.fromPath(GET_BEER_BY_ID_PATH)
                .build(beerDTOTestObject.getId());

        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(URL + GET_BEER_PATH))
                .andExpect(header("Authorization", TOKEN_VALUE))
                .andRespond(withAccepted().location(uri));

        mockGetOperation();

        BeerDTO beerDTOResponse = beerClient.createBeer(beerDTOTestObject);
        assertThat(beerDTOResponse.getBeerName()).isEqualTo("Mango Bobs");
    }

    @Test
    void getBeerById() {
        mockGetOperation();

        BeerDTO beerDTO = beerClient.getBeerById(beerDTOTestObject.getId());
        assertThat(beerDTO.getId()).isEqualTo(beerDTOTestObject.getId());
    }

    @Test
    void listBeers() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET)).andExpect(requestTo(URL + GET_BEER_PATH))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
        Page<BeerDTO> dtos = beerClient.listBeers();
        assertThat(dtos.getContent().size()).isPositive();
    }

    BeerDTO getBeerDto() {
        return BeerDTO.builder().id(UUID.randomUUID()).price(new BigDecimal("10.99"))
                .beerName("Mango Bobs").beerStyle(BeerStyle.ALE).quantityOnHand(500).upc("1234345").build();
    }

    BeerDTOPageImpl getPage() {
        return new BeerDTOPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
    }

    private void mockGetOperation() {
        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL + GET_BEER_BY_ID_PATH, beerDTOTestObject.getId()))
                .andRespond(withSuccess(beerDTOJson, MediaType.APPLICATION_JSON));
    }
}
