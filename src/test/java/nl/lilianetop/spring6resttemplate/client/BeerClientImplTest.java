package nl.lilianetop.spring6resttemplate.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BeerClientImplTest {

  @Autowired
   BeerClientImpl beerClient;

  @Test
  void listBeersNoName() {

    beerClient.listBeers(null, null, false, 0, 25);

  }

  @Test
  void listBeers() {

    beerClient.listBeers("ALE", null, false, 0, 25);

  }

}