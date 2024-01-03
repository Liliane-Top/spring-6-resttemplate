package nl.lilianetop.spring6resttemplate.client;

import nl.lilianetop.spring6resttemplate.model.BeerDTO;
import org.springframework.data.domain.Page;

public interface BeerClient {

  Page<BeerDTO> listBeers(String beerName);

}
