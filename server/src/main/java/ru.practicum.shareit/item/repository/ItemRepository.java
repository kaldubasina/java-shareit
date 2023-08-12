package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId, Pageable pageable);

    List<Item> findByItemRequestId(long requestId);

    List<Item> findByItemRequestIdIn(List<Long> requestIds);

    List<Item> findByAvailableTrueAndDescriptionContainingOrAvailableTrueAndNameContainingAllIgnoreCase(
            String containsInName, String containsInDescription, Pageable pageable);

}
