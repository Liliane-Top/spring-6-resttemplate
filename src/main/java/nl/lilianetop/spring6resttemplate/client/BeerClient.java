package nl.lilianetop.spring6resttemplate.client;

import java.util.UUID;
import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import nl.lilianetop.spring6resttemplate.model.BeerStyle;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

public interface BeerClient {

  Page<BeerDTO> listBeers();

  Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle,
      Boolean showInventory, Integer pageNumber, Integer pageSize);
  BeerDTO getBeerById(UUID beerId);

  BeerDTO createBeer(BeerDTO newDto);

  BeerDTO updateBeer(BeerDTO beerDto);

  void deleteBeer(UUID id);
}
