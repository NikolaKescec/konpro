package konpro.dz4.DZ4.service;

import konpro.dz4.DZ4.entity.TvProgramme;
import konpro.dz4.DZ4.entity.User;
import konpro.dz4.DZ4.repository.TvProgrammeRepository;
import konpro.dz4.DZ4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TvProgrammeRepository tvProgrammeRepository;

    private final UserRepository userRepository;

    public List<TvProgramme> findAll() {
        return tvProgrammeRepository.findAll();
    }

    @Transactional
    public void register(long userId, long tvProgrammeId) {
        final User user = userRepository.findById(userId).orElseThrow();
        final TvProgramme tvProgramme = tvProgrammeRepository.findById(tvProgrammeId).orElseThrow();

        if (!user.getTvProgrammes().contains(tvProgramme)) {
            user.getTvProgrammes().add(tvProgramme);

            tvProgrammeRepository.saveAndFlush(tvProgramme);
        }
    }

}
