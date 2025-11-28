package ru.practicum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;

import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {

    public StatsClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> save(EndpointHitDto endpointHit) {
        return post("/hit", endpointHit);
    }
}
