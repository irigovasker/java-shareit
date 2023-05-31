package ru.practicum.shareit.request.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Component
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r.requestor = ?1 ORDER BY r.created DESC ")
    List<ItemRequest> findUsersRequests(int userId);

    @Query("SELECT DISTINCT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r IN (:itemRequests)")
    List<ItemRequest> loadItemsInRequests(List<ItemRequest> itemRequests);

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r.id = ?1")
    Optional<ItemRequest> findItemRequestByIdWithItems(int itemRequestId);

    List<ItemRequest> findItemRequestsByRequestorNotIn(List<Integer> requestors, Pageable pageable);
}