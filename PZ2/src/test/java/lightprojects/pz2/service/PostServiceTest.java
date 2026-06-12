package lightprojects.pz2.service;

import lightprojects.pz2.dto.PostDtos.PostRequest;
import lightprojects.pz2.dto.PostDtos.PostResponse;
import lightprojects.pz2.entity.Category;
import lightprojects.pz2.entity.Post;
import lightprojects.pz2.entity.User;
import lightprojects.pz2.repository.CategoryRepository;
import lightprojects.pz2.repository.CommentRepository;
import lightprojects.pz2.repository.PostRepository;
import lightprojects.pz2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    private User author(String username, Long id) {
        User u = new User(username, username + "@test.com", "x");
        u.setId(id);
        return u;
    }

    private Category category(Long id, String name) {
        Category c = new Category(name);
        c.setId(id);
        return c;
    }

    @Test
    void create_buildsPostFromCurrentUserAndCategory() {
        User user = author("alice", 1L);
        Category category = category(2L, "Tech");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        PostResponse response = postService.create(new PostRequest("Title", "Body", 2L), "alice");

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.author()).isEqualTo("alice");
        assertThat(response.authorId()).isEqualTo(1L);
        assertThat(response.categoryId()).isEqualTo(2L);
        assertThat(response.categoryName()).isEqualTo("Tech");
    }

    @Test
    void getByIdAndCountView_incrementsViews() {
        Post post = new Post();
        post.setId(5L);
        post.setTitle("T");
        post.setContent("C");
        post.setCategory(category(1L, "Misc"));
        post.setAuthor(author("bob", 3L));
        post.setViews(7);
        when(postRepository.findById(5L)).thenReturn(Optional.of(post));

        PostResponse response = postService.getByIdAndCountView(5L);

        assertThat(response.views()).isEqualTo(8);
        assertThat(post.getViews()).isEqualTo(8);
    }

    @Test
    void delete_byNonOwnerNonAdmin_isForbidden() {
        Post post = new Post();
        post.setId(9L);
        post.setAuthor(author("alice", 1L));
        when(postRepository.findById(9L)).thenReturn(Optional.of(post));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("bob");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .when(auth).getAuthorities();

        assertThatThrownBy(() -> postService.delete(9L, auth))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("your own posts");

        verify(postRepository, never()).delete(any());
    }

    @Test
    void delete_byAdmin_removesPostAndComments() {
        Post post = new Post();
        post.setId(9L);
        post.setAuthor(author("alice", 1L));
        when(postRepository.findById(9L)).thenReturn(Optional.of(post));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(auth).getAuthorities();

        postService.delete(9L, auth);

        verify(commentRepository).deleteByPostId(9L);
        verify(postRepository).delete(post);
    }
}
