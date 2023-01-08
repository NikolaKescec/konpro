package konpro.dz4.DZ4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<U, V> {

    private U first;

    private V second;

}
