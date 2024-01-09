package nl.lilianetop.spring6resttemplate.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import nl.lilianetop.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
class BeerClientImplTest {

  @Autowired
   BeerClientImpl beerClient;

  @Test
  void deleteBeer() {

    BeerDTO newDto = BeerDTO.builder()
        .price(new BigDecimal("7.99"))
        .beerName("Amstel pilsner Amsterdam")
        .beerStyle(BeerStyle.PILSNER)
        .quantityOnHand(500)
        .upc("12345")
        .build();

    BeerDTO beerDto = beerClient.createBeer(newDto);

    beerClient.deleteBeer(beerDto.getId());

    assertThrows(HttpClientErrorException.class, () -> {
      // calling this no longer existing beerDTO should throw an exception
      beerClient.getBeerById(beerDto.getId());
    });

  }

  @Test
  void updateBeer() {
    BeerDTO newDto = BeerDTO.builder()
        .price(new BigDecimal("7.99"))
        .beerName("Amstel pilsner Amsterdam")
        .beerStyle(BeerStyle.PILSNER)
        .quantityOnHand(500)
        .upc("12345")
        .build();

    BeerDTO beerDto = beerClient.createBeer(newDto);

    final String newName = "Amstel pilsner Rotterdam";
    beerDto.setBeerName(newName);
    BeerDTO updatedBeer = beerClient.updateBeer(beerDto);

    assertEquals(newName, updatedBeer.getBeerName());

  }

  @Test
  void createBeer() {
    BeerDTO newDto = BeerDTO.builder()
        .price(new BigDecimal("7.99"))
        .beerName("Amstel pilsner")
        .beerStyle(BeerStyle.PILSNER)
        .quantityOnHand(500)
        .upc("12345")
        .build();

    BeerDTO savedDto = beerClient.createBeer(newDto);
    assertNotNull(savedDto);
  }

  @Test
  void getBeerById() {
    Page<BeerDTO> beerDTOs = beerClient.listBeers();

    BeerDTO dto = beerDTOs.getContent().get(0);

    BeerDTO byId = beerClient.getBeerById(dto.getId());

    assertNotNull(byId);

  }

  @Test
  void listBeersNoName() {

    beerClient.listBeers(null, null, false, 0, 25);

  }

  @Test
  void listBeers() {

    beerClient.listBeers("ALE", null, false, 0, 25);

  }



}