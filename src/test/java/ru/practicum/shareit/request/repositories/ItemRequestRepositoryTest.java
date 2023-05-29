package ru.practicum.shareit.request.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.Factory.createUser;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ItemRequestRepositoryTest {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ItemsRepository itemsRepository;
    @Autowired
    private ItemRequestRepository requestRepository;

    @BeforeEach
    void clearBefore() {
        clearContext();
    }

    @AfterEach
    void clearAfter() {
        clearContext();
    }

    private void clearContext() {
        requestRepository.deleteAllInBatch();
        itemsRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @Test
    void shouldFindUsersRequests() {
        User user = usersRepository.save(createUser());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .requestor(user.getId())
                .description("test")
                .build()
        );

        List<ItemRequest> result = requestRepository.findUsersRequests(user.getId());

        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
    }

    @Test
    void shouldLoadItemsInRequests() {
        User user = usersRepository.save(createUser());
        User itemOwner = usersRepository.save(User.builder().name("owner").email("owner@mail.ru").build());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .requestor(user.getId())
                .description("test")
                .build()
        );
        ItemRequest request2 = requestRepository.save(ItemRequest.builder()
                .requestor(user.getId())
                .description("another")
                .build()
        );
        itemsRepository.save(Item.builder()
                .name("name")
                .description("description")
                .owner(itemOwner.getId())
                .request(request.getId())
                .available(true)
                .build()
        );
        itemsRepository.save(Item.builder()
                .name("name")
                .description("description")
                .owner(itemOwner.getId())
                .request(request2.getId())
                .available(true)
                .build()
        );

        assertNull(request.getItems());
        assertNull(request2.getItems());

        List<ItemRequest> result = requestRepository.loadItemsInRequests(List.of(request, request2));

        assertEquals(2, result.size());
        for (ItemRequest itemRequest : result) {
            assertNotNull(itemRequest.getItems());
        }
    }

    @Test
    void shouldFindItemRequestByIdWithItems() {
        User user = usersRepository.save(createUser());
        User itemOwner = usersRepository.save(User.builder().name("owner").email("owner@mail.ru").build());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .requestor(user.getId())
                .description("test")
                .build()
        );
        itemsRepository.save(Item.builder()
                .name("name")
                .description("description")
                .owner(itemOwner.getId())
                .request(request.getId())
                .available(true)
                .build()
        );

        assertNull(request.getItems());

        request = requestRepository.findItemRequestByIdWithItems(request.getId()).orElseThrow();

        assertEquals("name", request.getItems().get(0).getName());
    }

    @Test
    void shouldFindItemRequestsByRequestorNotLike() {
        User user = usersRepository.save(createUser());
        User user2 = usersRepository.save(User.builder().name("owner").email("owner@mail.ru").build());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .requestor(user.getId())
                .description("test")
                .build()
        );
        ItemRequest request2 = requestRepository.save(ItemRequest.builder()
                .requestor(user2.getId())
                .description("test")
                .build()
        );
        List<ItemRequest> result = requestRepository.findItemRequestsByRequestorNotLike(user.getId(), PageRequest.of(0, 20));

        assertEquals(1, result.size());
        assertEquals(request2.getId(), result.get(0).getId());
    }
}