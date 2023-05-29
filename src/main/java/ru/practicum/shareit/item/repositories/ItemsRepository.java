package ru.practicum.shareit.item.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Component
public interface ItemsRepository extends JpaRepository<Item, Integer> {
    List<Item> findItemsByOwner(int ownerId);

    @Query(" SELECT i FROM Item i " +
            " WHERE i.available = true AND (" +
            " UPPER(i.name) LIKE upper(concat('%', ?1, '%')) " +
            " OR upper(i.description) LIKE upper(concat('%', ?1, '%'))) ")
    List<Item> search(String text, Pageable pageable);

    @Query(" SELECT i FROM Item i " +
            " LEFT JOIN FETCH i.comments com " +
            " WHERE i.id = ?1 ")
    Optional<Item> findItemByIdWithComments(int itemId);

    @Query(" SELECT i FROM Item i " +
            " LEFT JOIN FETCH i.bookings book " +
            " LEFT JOIN FETCH book.booker " +
            " WHERE i = ?1 ")
    Optional<Item> loadBookingsInItem(Item item);


    @Query(" SELECT DISTINCT i FROM Item i " +
            " LEFT JOIN FETCH i.comments com " +
            " LEFT JOIN FETCH com.author " +
            " WHERE i.owner = ?1")
    List<Item> findItemsWithCommentsByOwnerId(int ownerId, Pageable pageable);

    @Query(" SELECT DISTINCT i FROM Item i " +
            " LEFT JOIN FETCH i.bookings book " +
            " LEFT JOIN FETCH book.booker " +
            " WHERE i IN (:items)")
    List<Item> loadBookingsInItems(List<Item> items);
}
