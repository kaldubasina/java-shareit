package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(long bookerId,
                                                               long itemId,
                                                               Status status,
                                                               LocalDateTime current);

    List<Booking> findByBookerId(long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(long bookerId, Status status, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(long bookerId,
                                                          LocalDateTime forStart,
                                                          LocalDateTime forEnd,
                                                          Pageable pageable);

    List<Booking> findByBookerIdAndEndBefore(long bookerId, LocalDateTime forEnd, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfter(long bookerId, LocalDateTime forStart, Pageable pageable);

    List<Booking> findByItem_OwnerId(long ownerId, Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStatus(long ownerId, Status status, Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfter(long ownerId,
                                                              LocalDateTime forStart,
                                                              LocalDateTime forEnd,
                                                              Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndStatusNot(long ownerId,
                                                               LocalDateTime forEnd,
                                                               Status status,
                                                               Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStartAfterAndStatusNot(long ownerId,
                                                              LocalDateTime forStart,
                                                              Status status,
                                                              Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndStatus(long ownerId,
                                                               LocalDateTime forEnd,
                                                               Status status,
                                                               Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStartAfterAndStatus(long ownerId,
                                                              LocalDateTime forStart,
                                                              Status status,
                                                              Pageable pageable);
}
