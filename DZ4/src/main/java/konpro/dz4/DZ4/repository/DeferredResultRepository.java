package konpro.dz4.DZ4.repository;

import konpro.dz4.DZ4.dto.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DeferredResultRepository {

    private final Map<Long, DeferredResult<List<Pair<String, Long>>>> deferredResultMap = new HashMap<>();

    public DeferredResult<List<Pair<String, Long>>> saveDeferredResultForUserId(Long userId) {
        final DeferredResult<List<Pair<String, Long>>> result = new DeferredResult<>();

        result.onTimeout(() -> result.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)));
        synchronized (deferredResultMap) {
            deferredResultMap.put(userId, result);
        }

        return result;
    }

    public Map<Long, DeferredResult<List<Pair<String, Long>>>> retrieveDefferedResultMap() {
        return deferredResultMap;
    }

}
