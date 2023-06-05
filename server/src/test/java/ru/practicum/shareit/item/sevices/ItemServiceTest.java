package ru.practicum.shareit.item.sevices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.Factory;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.models.Comment;
import ru.practicum.shareit.item.comment.repositories.CommentsRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.utils.BadRequestException;
import ru.practicum.shareit.utils.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.Factory.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    private ItemService itemService;
    @Mock
    private ItemsRepository itemsRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemsRepository, commentsRepository, bookingRepository, usersRepository);
    }

    @Test
    void shouldCreateItem() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(createUser(1)));
        when(itemsRepository.save(any())).thenReturn(Item.builder().id(1).name("name").description("de").build());
        itemService.createItem(createItemDto(), 1);
        verify(itemsRepository).save(any());
    }

    @Test
    void updateItem() {
        when(itemsRepository.save(any())).thenReturn(Item.builder().id(1).name("testName").description("testDescription").build());
        when(itemsRepository.findById(anyInt()))
                .thenReturn(
                        Optional.of(
                                Item.builder().id(1).name("testName").description("de").owner(1).build()
                        )
                );
        itemService.updateItem(createItemDto(), 1);
        verify(itemsRepository).save(any());
    }

    @Test
    void shouldErrorWhenTryUpdateItemBecauseUserNotOwner() {
        when(itemsRepository.findById(anyInt()))
                .thenReturn(
                        Optional.of(
                                Item.builder().id(1).name("testName").description("de").owner(2).build()
                        )
                );
        assertThrows(NotFoundException.class, () -> itemService.updateItem(createItemDto(), 1));
    }

    @Test
    void shouldErrorWhenTryUpdateItemBecauseItemNotExist() {
        when(itemsRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.updateItem(createItemDto(), 1));
    }

    @Test
    void shouldFindItemByIdUserNotOwner() {
        Item item = Factory.createItem(1, 1);
        Comment comment = createComment(item.getId(), 1, createUser(2));
        item.setComments(List.of(comment));
        when(itemsRepository.findItemByIdWithComments(anyInt())).thenReturn(Optional.of(item));

        ItemOwnerDto itemOwnerDto = itemService.getItemById(1, 2);

        verify(itemsRepository).findItemByIdWithComments(anyInt());
        assertEquals(item.getId(), itemOwnerDto.getId());
        assertEquals(1, itemOwnerDto.getComments().size());
        assertNull(itemOwnerDto.getLastBooking());
        assertNull(itemOwnerDto.getNextBooking());
    }

    @Test
    void shouldFindItemByIdUserOwner() {
        Item item = Factory.createItem(1, 1);
        Comment comment = createComment(item.getId(), 1, createUser(2));
        item.setComments(List.of(comment));
        item.setBookings(List.of(
                        Booking.builder()
                                .id(1)
                                .start(LocalDateTime.now().minusDays(2))
                                .end(LocalDateTime.now().minusDays(1))
                                .item(item)
                                .booker(createUser(2))
                                .status(BookingStatus.APPROVED)
                                .build(),
                        Booking.builder()
                                .id(2)
                                .start(LocalDateTime.now().plusDays(1))
                                .end(LocalDateTime.now().plusDays(2))
                                .item(item)
                                .booker(createUser(2))
                                .status(BookingStatus.APPROVED)
                                .build()
                )
        );
        when(itemsRepository.findItemByIdWithComments(anyInt())).thenReturn(Optional.of(item));
        when(itemsRepository.loadBookingsInItem(any())).thenReturn(Optional.of(item));

        ItemOwnerDto itemOwnerDto = itemService.getItemById(1, 1);

        verify(itemsRepository).findItemByIdWithComments(anyInt());
        verify(itemsRepository).loadBookingsInItem(any());
        assertEquals(item.getId(), itemOwnerDto.getId());
        assertEquals(1, itemOwnerDto.getComments().size());
        assertEquals(1, itemOwnerDto.getLastBooking().getId());
        assertEquals(2, itemOwnerDto.getNextBooking().getId());
    }

    @Test
    void shouldErrorBecauseItemNotExistWhenTryGetItemById() {
        when(itemsRepository.findItemByIdWithComments(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1, 1));
    }

    @Test
    void shouldFindUserItems() {
        Item item = Factory.createItem(1, 1);
        Comment comment = createComment(item.getId(), 1, createUser(2));
        item.setComments(List.of(comment));
        item.setBookings(List.of(
                        Booking.builder()
                                .id(1)
                                .start(LocalDateTime.now().minusDays(2))
                                .end(LocalDateTime.now().minusDays(1))
                                .item(item)
                                .booker(createUser(2))
                                .status(BookingStatus.APPROVED)
                                .build(),
                        Booking.builder()
                                .id(2)
                                .start(LocalDateTime.now().plusDays(1))
                                .end(LocalDateTime.now().plusDays(2))
                                .item(item)
                                .booker(createUser(2))
                                .status(BookingStatus.APPROVED)
                                .build()
                )
        );
        when(itemsRepository.findItemsWithCommentsByOwnerId(anyInt(), any())).thenReturn(List.of(item));
        when(itemsRepository.loadBookingsInItems(any())).thenReturn(List.of(item));

        List<ItemOwnerDto> result = itemService.getUserItems(1, 0, 20);

        verify(itemsRepository).findItemsWithCommentsByOwnerId(anyInt(), any());
        verify(itemsRepository).loadBookingsInItems(any());

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void shouldFindItemsByString() {
        Item item = createItem(1, 1);
        when(itemsRepository.search(any(), any())).thenReturn(List.of(item));
        List<ItemDto> result = itemService.findItems("nAme", 0, 20);
        verify(itemsRepository).search(any(), any());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void shouldReturnEmptyListWhenStringIsBlank() {
        List<ItemDto> result = itemService.findItems("    ", 0, 20);
        verify(itemsRepository, never()).search(any(), any());
        assertEquals(0, result.size());
    }

    @Test
    void shouldCreateComment() {
        Comment comment = createComment(1, 1, createUser(1));
        when(bookingRepository.findFirstBookingByBooker_IdAndItem_IdAndEndBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(Booking.builder().booker(createUser(1)).build()));
        when(commentsRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.createComment(createCommentDto(1, 1), 2, 1);

        verify(bookingRepository).findFirstBookingByBooker_IdAndItem_IdAndEndBefore(anyInt(), anyInt(), any());
        verify(commentsRepository).save(any());

        assertEquals(comment.getId(), result.getId());
    }

    @Test
    void shouldErrorWhenCreateCommentAndUserNotBookItemInPast() {
        when(bookingRepository.findFirstBookingByBooker_IdAndItem_IdAndEndBefore(anyInt(), anyInt(), any())).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> itemService.createComment(createCommentDto(1, 1), 2, 1));
    }
}