package konpro.dz4.DZ4.runner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import konpro.dz4.DZ4.entity.TvProgramme;
import konpro.dz4.DZ4.repository.TvProgrammeRepository;
import konpro.dz4.DZ4.xml.Programme;
import konpro.dz4.DZ4.xml.Tv;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    private final TvProgrammeRepository tvProgrammeRepository;

    @Override
    public void run(String... args) throws Exception {
        final File file = new File("src/main/resources/program/program.xml");
        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Tv tv = xmlMapper.readValue(file, Tv.class);

        final List<TvProgramme> tvProgrammes =
            tv.getProgramme().stream().map(this::mapTvProgrammeFromProgramme).toList();
        tvProgrammeRepository.saveAll(tvProgrammes);
    }

    private TvProgramme mapTvProgrammeFromProgramme(Programme programme) {
        final TvProgramme tvProgramme = new TvProgramme();

        tvProgramme.setTitle(programme.getTitle());
        tvProgramme.setDescription(programme.getDescription());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z");
        LocalDateTime dateTime = LocalDateTime.parse(programme.getStart(), formatter);
        tvProgramme.setStart(dateTime);

        return tvProgramme;
    }

}
