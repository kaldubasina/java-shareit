package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private Booking booking;
    private RequestBookingDto requestBookingDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.email")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();

        requestBookingDto = RequestBookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();
    }

    @Test
    void shouldReturnValidationError() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(RequestBookingDto.builder()
                                .itemId(1L)
                                .build()))
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSaveBooking() throws Exception {
        when(bookingService.add(any(), anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(requestBookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(requestBookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty());
    }

    @Test
    void shouldReturnBookingAfterChangeStatus() throws Exception {
        when(bookingService.bookingDecision(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(requestBookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(requestBookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty());
    }

    @Test
    void shouldReturnBookingById() throws Exception {
        when(bookingService.getByBookingIdAndUserId(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(requestBookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(requestBookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty());
    }

    @Test
    void shouldReturnBookingsByBookerId() throws Exception {
        when(bookingService.getByStateAndUserId(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is(requestBookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(requestBookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker").isNotEmpty())
                .andExpect(jsonPath("$[0].item").isNotEmpty());
    }

    @Test
    void shouldReturnBookingsByOwnerId() throws Exception {
        when(bookingService.getAllByStateAndUserId(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is(requestBookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(requestBookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker").isNotEmpty())
                .andExpect(jsonPath("$[0].item").isNotEmpty());
    }
}
