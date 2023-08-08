package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private Item item;
    private ItemDto itemDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.email")
                .build();

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();

        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("text")
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldReturnValidationError() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(ItemDto.builder()
                                .name("")
                                .build()))
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(CommentDto.builder()
                                .text("")
                                .build()))
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSaveItem() throws Exception {
        when(itemService.add(any(), anyLong(), nullable(Long.class)))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void shouldReturnItemAfterUpdate() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(item);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void shouldReturnItemById() throws Exception {
        when(itemService.getByItemId(anyLong(), anyLong()))
                .thenReturn(item);

        mvc.perform(get("/items/{itemId}", 1)
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void shouldReturnItemsByUserId() throws Exception {
        when(itemService.getByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items")
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void shouldReturnItemsByText() throws Exception {
        when(itemService.searchByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search")
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .param("text", "name")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void shouldSaveComment() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(comment);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header(REQUEST_HEADER_USER_ID, 1)
                        .content(mapper.writeValueAsString(CommentDto.builder().text("text").build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthor().getName())))
                .andExpect(jsonPath("$.created",
                        is(comment.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }
}
