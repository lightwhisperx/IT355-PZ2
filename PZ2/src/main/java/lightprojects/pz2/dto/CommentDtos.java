package lightprojects.pz2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CommentDtos {

    private CommentDtos() {
    }

    public record CommentRequest(@NotBlank @Size(max = 2000) String content) {
    }

    public record CommentResponse(
            Long id,
            String content,
            Long authorId,
            String author,
            String createdAt) {
    }
}
