package ru.practicum.shareit.booking.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.Factory.createItem;
import static ru.practicum.shareit.Factory.createUser;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ItemsRepository itemsRepository;

    @BeforeEach
    void clearBefore() {
        clearContext();
    }

    @AfterEach
    void clearAfter() {
        clearContext();
    }

    private void clearContext() {
        bookingRepository.deleteAllInBatch();
        itemsRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @Test
    void shouldFindBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.WAITING)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().plusDays(10))
                .status(BookingStatus.WAITING)
                .build()
        );

        List<Booking> bookings = bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 20));

        assertEquals(bookings.get(1).getId(), booking2.getId());
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void shouldFindBookingsByBooker_IdAndStartAfterOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(4))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.WAITING)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().plusDays(10))
                .status(BookingStatus.WAITING)
                .build()
        );

        List<Booking> bookings = bookingRepository.findBookingsByBooker_IdAndStartAfterOrderByStartDesc(booker.getId(), LocalDateTime.now(), PageRequest.of(0, 20));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void shouldFindBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.WAITING)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(6))
                .status(BookingStatus.WAITING)
                .build()
        );

        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 20));

        assertEquals(2, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking2.getId(), result.get(1).getId());
    }

    @Test
    void shouldFindBookingsByBooker_IdAndStatusOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.WAITING)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(6))
                .status(BookingStatus.REJECTED)
                .build()
        );

        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED, PageRequest.of(0, 20));

        assertEquals(1, result.size());
        assertEquals(booking2.getId(), result.get(0).getId());
    }

    @Test
    void shouldFindBookingsByBooker_IdOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.WAITING)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(6))
                .status(BookingStatus.REJECTED)
                .build()
        );

        List<Booking> result = bookingRepository.findBookingsByBooker_IdOrderByStartDesc(booker.getId(), PageRequest.of(0, 20));

        assertEquals(2, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking2.getId(), result.get(1).getId());
    }

    @Test
    void shouldFindBookingsByStartBeforeAndEndAfterAndItem_IdInOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));
        Item item2 = itemsRepository.save(createItem(itemOwner.getId()));
        List<Integer> ids = List.of(item.getId(), item2.getId());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.WAITING)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().plusDays(6))
                .status(BookingStatus.WAITING)
                .build()
        );

        List<Booking> result = bookingRepository.findBookingsByStartBeforeAndEndAfterAndItem_IdInOrderByStartDesc(LocalDateTime.now(), LocalDateTime.now(), ids, PageRequest.of(0, 20));

        assertEquals(2, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking2.getId(), result.get(1).getId());
    }

    @Test
    void shouldFindBookingsByStartAfterAndItem_IdInOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));
        Item item2 = itemsRepository.save(createItem(itemOwner.getId()));
        List<Integer> ids = List.of(item.getId(), item2.getId());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6))
                .status(BookingStatus.WAITING)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(16))
                .status(BookingStatus.WAITING)
                .build()
        );

        List<Booking> result = bookingRepository.findBookingsByStartAfterAndItem_IdInOrderByStartDesc(LocalDateTime.now(), ids, PageRequest.of(0, 20));

        assertEquals(2, result.size());
        assertEquals(booking.getId(), result.get(1).getId());
        assertEquals(booking2.getId(), result.get(0).getId());
    }

    @Test
    void shouldFindBookingsByStartBeforeAndEndBeforeAndItem_IdInOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));
        Item item2 = itemsRepository.save(createItem(itemOwner.getId()));
        List<Integer> ids = List.of(item.getId(), item2.getId());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.WAITING)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(6))
                .status(BookingStatus.WAITING)
                .build()
        );

        List<Booking> result = bookingRepository.findBookingsByStartBeforeAndEndBeforeAndItem_IdInOrderByStartDesc(LocalDateTime.now(), LocalDateTime.now(), ids, PageRequest.of(0, 20));

        assertEquals(2, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking2.getId(), result.get(1).getId());
    }

    @Test
    void shouldFindBookingsByStatusAndItem_IdInOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));
        Item item2 = itemsRepository.save(createItem(itemOwner.getId()));
        List<Integer> ids = List.of(item.getId(), item2.getId());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.REJECTED)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(6))
                .status(BookingStatus.WAITING)
                .build()
        );

        List<Booking> result = bookingRepository.findBookingsByStatusAndItem_IdInOrderByStartDesc(BookingStatus.REJECTED, ids, PageRequest.of(0, 20));

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void shouldFindBookingsByItem_IdInOrderByStartDesc() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));
        Item item2 = itemsRepository.save(createItem(itemOwner.getId()));
        List<Integer> ids = List.of(item.getId(), item2.getId());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.REJECTED)
                .build()
        );

        Booking booking2 = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(6))
                .status(BookingStatus.WAITING)
                .build()
        );

        List<Booking> result = bookingRepository.findBookingsByItem_IdInOrderByStartDesc(ids, PageRequest.of(0, 20));

        assertEquals(2, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking2.getId(), result.get(1).getId());
    }

    @Test
    void shouldFindFirstBookingByBooker_IdAndItem_IdAndEndBefore() {
        User itemOwner = usersRepository.save(createUser());
        User booker = usersRepository.save(User.builder().name("booker").email("booker@email.ru").build());
        Item item = itemsRepository.save(createItem(itemOwner.getId()));

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.REJECTED)
                .build()
        );

        Booking result = bookingRepository.findFirstBookingByBooker_IdAndItem_IdAndEndBefore(booker.getId(), item.getId(), LocalDateTime.now()).orElseThrow();

        assertEquals(booking.getId(), result.getId());
    }
}