package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS");
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService requestService;
    @Autowired
    private MockMvc mvc;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.email")
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSaveRequest() throws Exception {
        when(requestService.add(any(), anyLong()))
                .thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(ItemRequestDto.builder()
                                .description("description")
                                .build()))
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.requester").isNotEmpty())
                .andExpect(jsonPath("$.created",
                        is(request.getCreated().format(formatter))));
    }

    @Test
    void shouldReturnRequestById() throws Exception {
        when(requestService.getById(anyLong(), anyLong()))
                .thenReturn(request);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.requester").isNotEmpty())
                .andExpect(jsonPath("$.created",
                        is(request.getCreated().format(formatter))));
    }

    @Test
    void shouldReturnRequestsByUserId() throws Exception {
        when(requestService.getByUserId(anyLong()))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests")
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].requester").isNotEmpty())
                .andExpect(jsonPath("$[0].created",
                        is(request.getCreated().format(formatter))));
    }

    @Test
    void shouldReturnRequestsByOtherUsers() throws Exception {
        when(requestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests/all")
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].requester").isNotEmpty())
                .andExpect(jsonPath("$[0].created",
                        is(request.getCreated().format(formatter))));
    }
}
