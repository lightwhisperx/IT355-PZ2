package lightprojects.pz2.controller;

import lightprojects.pz2.dto.PostDtos.PostResponse;
import lightprojects.pz2.security.CustomUserDetailsService;
import lightprojects.pz2.security.JwtAuthenticationFilter;
import lightprojects.pz2.security.JwtService;
import lightprojects.pz2.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getAll_returnsPostList() throws Exception {
        PostResponse post = new PostResponse(
                1L, "Hello", "World", 2L, "Tech", 3L, "alice",
                "2026-01-01T00:00:00Z", 5, 2);
        when(postService.getAll()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Hello"))
                .andExpect(jsonPath("$[0].author").value("alice"))
                .andExpect(jsonPath("$[0].views").value(5));
    }

    @Test
    void getById_returnsSinglePost() throws Exception {
        PostResponse post = new PostResponse(
                1L, "Hello", "World", 2L, "Tech", 3L, "alice",
                "2026-01-01T00:00:00Z", 6, 2);
        when(postService.getByIdAndCountView(1L)).thenReturn(post);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoryName").value("Tech"));
    }
}
