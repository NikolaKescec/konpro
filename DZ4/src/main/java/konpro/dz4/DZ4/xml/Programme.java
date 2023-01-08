package konpro.dz4.DZ4.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Programme {

    private String title;

    @JacksonXmlProperty(localName = "desc")
    private String description;

    @JacksonXmlProperty(isAttribute = true)
    private String start;

}
