package ru.practicum.shareit.booking.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.booking.utils.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.utils.BadRequestException;
import ru.practicum.shareit.utils.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UsersRepository usersRepository;
    private final ItemsRepository itemsRepository;

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreateDto bookingDto, int userId) {
        User user = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemsRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (item.isAvailable() && user.getId() != item.getOwner()) {
            return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toNewBooking(bookingDto, user, item)));
        } else if (user.getId() == item.getOwner()) {
            throw new NotFoundException("Нельзя бронировать свою вещь");
        } else {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
    }

    @Override
    @Transactional
    public BookingDto approveBooking(int userId, int bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getItem().getOwner() != userId) {
            throw new NotFoundException("Нет доступа");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("Бронирование уже подтверждено");
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new NotFoundException("Бронирование не найдено");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUsersBookings(int userId, State state, int from, int size) {
        usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> res = null;
        switch (state) {
            case CURRENT:
                res = bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageable
                );
                break;
            case FUTURE:
                res = bookingRepository.findBookingsByBooker_IdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageable
                );
                break;
            case PAST:
                res = bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageable
                );
                break;
            case WAITING:
                res = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                res = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                res = bookingRepository.findBookingsByBooker_IdOrderByStartDesc(userId, pageable);
        }
        return res.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getUserItemsBookings(int userId, State state, int from, int size) {
        usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Integer> itemIds = itemsRepository.findItemsByOwner(userId).stream().mapToInt(Item::getId).boxed().collect(Collectors.toList());
        Pageable pageable = PageRequest.of(from, size);
        List<Booking> res = null;
        switch (state) {
            case CURRENT:
                res = bookingRepository.findBookingsByStartBeforeAndEndAfterAndItem_IdInOrderByStartDesc(
                        LocalDateTime.now(), LocalDateTime.now(), itemIds, pageable
                );
                break;
            case FUTURE:
                res = bookingRepository.findBookingsByStartAfterAndItem_IdInOrderByStartDesc(LocalDateTime.now(), itemIds, pageable);
                break;
            case PAST:
                res = bookingRepository.findBookingsByStartBeforeAndEndBeforeAndItem_IdInOrderByStartDesc(
                        LocalDateTime.now(), LocalDateTime.now(), itemIds, pageable
                );
                break;
            case WAITING:
                res = bookingRepository.findBookingsByStatusAndItem_IdInOrderByStartDesc(BookingStatus.WAITING, itemIds, pageable);
                break;
            case REJECTED:
                res = bookingRepository.findBookingsByStatusAndItem_IdInOrderByStartDesc(BookingStatus.REJECTED, itemIds, pageable);
                break;
            default:
                res = bookingRepository.findBookingsByItem_IdInOrderByStartDesc(itemIds, pageable);
        }
        return res.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
