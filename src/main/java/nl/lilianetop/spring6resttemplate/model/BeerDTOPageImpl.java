package nl.lilianetop.spring6resttemplate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@JsonIgnoreProperties(ignoreUnknown = true, value = "pageable")//this property is not being returned by the payload, hence we want to ignore it
public class BeerDTOPageImpl<BeerDTO> extends PageImpl<nl.lilianetop.spring6resttemplate.model.BeerDTO> {

  @JsonCreator(mode= Mode.PROPERTIES)//annotation is for that this constructor should be used and bind the properties from payload to pojo
  public BeerDTOPageImpl(@JsonProperty("content") List<nl.lilianetop.spring6resttemplate.model.BeerDTO> content,
      @JsonProperty("number") int page,
      @JsonProperty("size") int size,
      @JsonProperty("totalElements") long total) {
    super(content, PageRequest.of(page, size), total);
  }

  public BeerDTOPageImpl(List<nl.lilianetop.spring6resttemplate.model.BeerDTO> content, Pageable pageable,
      long total) {
    super(content, pageable, total);
  }

  public BeerDTOPageImpl(List<nl.lilianetop.spring6resttemplate.model.BeerDTO> content) {
    super(content);
  }
}
