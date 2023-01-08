package konpro.dz4.DZ4.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Tv {

    @JacksonXmlElementWrapper(useWrapping = false)
    List<Programme> programme = new ArrayList<>();

}
