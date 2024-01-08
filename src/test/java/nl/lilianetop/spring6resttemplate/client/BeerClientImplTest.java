package nl.lilianetop.spring6resttemplate.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@SpringBootTest
class BeerClientImplTest {

  @Autowired
   BeerClientImpl beerClient;

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