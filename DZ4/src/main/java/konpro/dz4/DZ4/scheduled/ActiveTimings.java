package konpro.dz4.DZ4.scheduled;

import konpro.dz4.DZ4.dto.Pair;
import konpro.dz4.DZ4.repository.DeferredResultRepository;
import konpro.dz4.DZ4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Component
@Log
@RequiredArgsConstructor
public class ActiveTimings {

    private final DeferredResultRepository deferredResultRepository;

    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 1000 * 10)
    public void scheduleFixedRateTask() {
        log.info("Starting scheduled job.");
        final LocalDateTime current = LocalDateTime.now();

        final Map<Long, DeferredResult<List<Pair<String, Long>>>> deferredResultMap =
            deferredResultRepository.retrieveDefferedResultMap();
        synchronized (deferredResultMap) {
            for (Map.Entry<Long, DeferredResult<List<Pair<String, Long>>>> entry : deferredResultMap.entrySet()) {
                final Long userId = entry.getKey();
                final DeferredResult<List<Pair<String, Long>>> deferredResult = entry.getValue();

                if (!deferredResult.isSetOrExpired()) {
                    final List<Pair<String, Long>> minutes = userRepository.findById(userId).orElseThrow()
                        .getTvProgrammes()
                        .stream()
                        .filter(tvProgramme -> tvProgramme.getStart().isAfter(current))
                        .map(tvProgramme -> new Pair<String, Long>(tvProgramme.getTitle(),
                            ChronoUnit.MINUTES.between(current, tvProgramme.getStart())))
                        .toList();

                    deferredResult.setResult(minutes);
                }
            }

            deferredResultMap.clear();
            log.info("Ended scheduled job.");
        }
    }

}
