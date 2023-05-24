package ru.practicum.shareit.item.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.models.Comment;
import ru.practicum.shareit.item.comment.repositories.CommentsRepository;
import ru.practicum.shareit.item.comment.utils.CommentMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.BadRequestException;
import ru.practicum.shareit.utils.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemsRepository itemsRepository;
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, int ownerId) {
        return ItemMapper.toItemDto(itemsRepository.save(ItemMapper.toNewItem(itemDto, ownerId)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, int ownerId) {
        Item item = itemsRepository.findById(
                itemDto.getId()).orElseThrow(() -> new NotFoundException("Несуществующая вещь")
        );
        if (item.getOwner() != ownerId) {
            throw new NotFoundException("Нет доступа для редактирования");
        }
        return ItemMapper.toItemDto(itemsRepository.save(ItemMapper.updateFields(item, itemDto)));
    }

    @Override
    public ItemOwnerDto getItemById(int itemId, int userId) {
        Item item = itemsRepository.findItemByIdWithComments(
                itemId).orElseThrow(() -> new NotFoundException("Несуществующая вещь")
        );
        if (item.getOwner() != userId) {
            return ItemMapper.toItemOwnerDto(item, toCommentDtoList(item.getComments()));
        } else {
            item = itemsRepository.loadBookingsInItem(item).orElseThrow(() -> new NotFoundException("Несуществующая вещь"));
            return ItemMapper.toItemOwnerDto(
                    item,
                    toCommentDtoList(item.getComments()),
                    toBookingCreateDtoList(item.getBookings())
            );
        }
    }

    @Override
    public List<ItemOwnerDto> getUserItems(int ownerId, int from, int size) {
        return itemsRepository.loadBookingsInItems(
                itemsRepository.findItemsWithCommentsByOwnerId(ownerId, PageRequest.of(from, size)))
                .stream()
                .map(
                        item -> ItemMapper.toItemOwnerDto(
                                item, toCommentDtoList(item.getComments()), toBookingCreateDtoList(item.getBookings())
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItems(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemsRepository.search(
                text, PageRequest.of(from, size))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()
        );
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto comment, int userId, int itemId) {
        Booking booking = bookingRepository.findFirstBookingByBooker_IdAndItem_IdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Можно комментировать только вещи которые вы брали в аренду"));
        comment.setItemId(itemId);
        return CommentMapper.toDto(commentsRepository.save(CommentMapper.toNewComment(comment, booking.getBooker())));
    }

    private List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<BookingCreateDto> toBookingCreateDtoList(List<Booking> bookings) {
        return bookings
                .stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .map(BookingMapper::toBookingCreateDto)
                .collect(Collectors.toList());
    }
}
