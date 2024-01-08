package nl.lilianetop.spring6resttemplate.client;

import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import nl.lilianetop.spring6resttemplate.model.BeerStyle;
import org.springframework.data.domain.Page;

public interface BeerClient {

  Page<BeerDTO> listBeers();

  Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle,
      Boolean showInventory, Integer pageNumber, Integer pageSize);

}
