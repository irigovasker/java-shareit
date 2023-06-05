package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getUserRequests(int userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> createItemRequest(ItemRequestDto itemRequestDto, int userId) {
        itemRequestDto.setCreated(LocalDateTime.now());
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllRequests(int from, int size, int userId) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", (long) userId, params);
    }

    public ResponseEntity<Object> getItemRequestById(int requestId, int userId) {
        return get("/" + requestId, userId);
    }
}
