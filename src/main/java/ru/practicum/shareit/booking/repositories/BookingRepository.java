package ru.practicum.shareit.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.utils.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByBooker_IdAndStartAfterOrderByStartDesc(int userId, LocalDateTime start);

    List<Booking> findBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(int userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByBooker_IdAndStatusOrderByStartDesc(int userId, BookingStatus status);

    List<Booking> findBookingsByBooker_IdOrderByStartDesc(int userId);

    List<Booking> findBookingsByStartBeforeAndEndAfterAndItem_IdInOrderByStartDesc(LocalDateTime start, LocalDateTime end, List<Integer> itemIds);

    List<Booking> findBookingsByStartAfterAndItem_IdInOrderByStartDesc(LocalDateTime start, List<Integer> itemIds);

    List<Booking> findBookingsByStartBeforeAndEndBeforeAndItem_IdInOrderByStartDesc(LocalDateTime start, LocalDateTime end, List<Integer> itemIds);

    List<Booking> findBookingsByStatusAndItem_IdInOrderByStartDesc(BookingStatus status, List<Integer> itemIds);

    List<Booking> findBookingsByItem_IdInOrderByStartDesc(List<Integer> itemIds);

    Optional<Booking> findFirstBookingByBooker_IdAndItem_IdAndEndBefore(int bookerId, int itemId, LocalDateTime before);
}
