package ru.practicum.shareit.item.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.comment.repositories.CommentsRepository;
import ru.practicum.shareit.item.comment.utils.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.practicum.shareit.Factory.*;


@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ItemsRepositoryTest {
    @Autowired
    private ItemsRepository itemsRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentsRepository commentsRepository;

    @BeforeEach
    void clearBefore() {
        clearContext();
    }

    @AfterEach
    void clearAfter() {
        clearContext();
    }

    @Test
    void shouldFindItemByOwnerId() {
        User user = usersRepository.save(createUser());
        Item item = itemsRepository.save(createItem(user.getId()));
        itemsRepository.save(createItem(user.getId()));

        List<Item> result = itemsRepository.findItemsByOwner(user.getId());

        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(2, result.size());
    }

    @Test
    void shouldFindItemByTextIgnoreCase() {
        User user = usersRepository.save(createUser());
        Item item = itemsRepository.save(createItem(user.getId()));

        Item result = itemsRepository.search("DescRi", PageRequest.of(0, 20)).get(0);

        assertEquals(item.getId(), result.getId());
    }

    @Test
    void shouldFindItemWithComments() {
        User user = usersRepository.save(createUser());
        Item item = itemsRepository.save(createItem(user.getId()));
        commentsRepository.save(CommentMapper.toNewComment(createCommentDto(item.getId()), user));
        commentsRepository.save(CommentMapper.toNewComment(createCommentDto(item.getId()), user));

        assertNull(item.getComments());

        item = itemsRepository.findItemByIdWithComments(item.getId()).orElseThrow();

        assertEquals(item.getComments().size(), 2);
    }

    @Test
    void shouldLoadBookingsInItem() {
        User user = usersRepository.save(createUser());
        User booker = createUser();
        booker.setEmail("another@email.ru");
        booker = usersRepository.save(booker);
        Item item = itemsRepository.save(createItem(user.getId()));
        bookingRepository.save(BookingMapper.toNewBooking(createBookingCreateDto(item.getId()), booker, item));

        assertNull(item.getBookings());

        item = itemsRepository.loadBookingsInItem(item).orElseThrow();

        assertEquals(item.getBookings().size(), 1);
    }

    @Test
    void shouldFindItemWithCommentsByOwnerId() {
        User owner = usersRepository.save(createUser());
        Item item = itemsRepository.save(createItem(owner.getId()));
        commentsRepository.save(CommentMapper.toNewComment(createCommentDto(item.getId()), owner));
        commentsRepository.save(CommentMapper.toNewComment(createCommentDto(item.getId()), owner));

        item = itemsRepository.findItemsWithCommentsByOwnerId(owner.getId(), PageRequest.of(0, 20)).get(0);

        assertEquals(item.getComments().size(), 2);
    }

    @Test
    void shouldLoadBookingsInItems() {
        User owner = usersRepository.save(createUser());
        User booker = createUser();
        booker.setEmail("another@email.ru");
        booker = usersRepository.save(booker);
        List<Item> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Item item = itemsRepository.save(createItem(owner.getId()));
            bookingRepository.save(BookingMapper.toNewBooking(createBookingCreateDto(item.getId()), booker, item));
            result.add(item);
        }

        for (Item item : result) {
            assertNull(item.getBookings());
        }

        result = itemsRepository.loadBookingsInItems(result);

        for (Item item : result) {
            assertEquals(1, item.getBookings().size());
        }
    }

    private void clearContext() {
        commentsRepository.deleteAllInBatch();
        bookingRepository.deleteAllInBatch();
        itemsRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }
}