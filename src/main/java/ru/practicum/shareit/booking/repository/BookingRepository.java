package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
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

    List<Booking> findByBookerId(long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(long bookerId, Status status, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(long bookerId,
                                                          LocalDateTime forStart,
                                                          LocalDateTime forEnd,
                                                          Sort sort);

    List<Booking> findByBookerIdAndEndBefore(long bookerId, LocalDateTime forEnd, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(long bookerId, LocalDateTime forStart, Sort sort);

    List<Booking> findByItem_OwnerId(long ownerId, Sort sort);

    List<Booking> findByItem_OwnerIdAndStatus(long ownerId, Status status, Sort sort);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfter(long ownerId,
                                                              LocalDateTime forStart,
                                                              LocalDateTime forEnd,
                                                              Sort sort);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndStatusNot(long ownerId,
                                                               LocalDateTime forEnd,
                                                               Status status,
                                                               Sort sort);

    List<Booking> findByItem_OwnerIdAndStartAfterAndStatusNot(long ownerId,
                                                              LocalDateTime forStart,
                                                              Status status,
                                                              Sort sort);
}
