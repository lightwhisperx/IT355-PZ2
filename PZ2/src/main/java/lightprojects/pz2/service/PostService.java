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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAll() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(PostService::toResponse)
                .toList();
    }

    @Transactional
    public PostResponse getByIdAndCountView(Long id) {
        Post post = findPost(id);
        post.setViews(post.getViews() + 1);
        return toResponse(post);
    }

    @Transactional
    public PostResponse create(PostRequest request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));

        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setCategory(category);
        post.setAuthor(author);

        return toResponse(postRepository.save(post));
    }

    @Transactional
    public PostResponse like(Long id) {
        Post post = findPost(id);
        post.setLikes(post.getLikes() + 1);
        return toResponse(post);
    }

    @Transactional
    public void delete(Long id, Authentication authentication) {
        Post post = findPost(id);

        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        boolean isOwner = post.getAuthor().getUsername().equals(authentication.getName());
        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own posts");
        }

        commentRepository.deleteByPostId(id);
        postRepository.delete(post);
    }

    private Post findPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    private static boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    static PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory().getId(),
                post.getCategory().getName(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getCreatedAt() != null ? post.getCreatedAt().toString() : null,
                post.getViews(),
                post.getLikes());
    }
}
