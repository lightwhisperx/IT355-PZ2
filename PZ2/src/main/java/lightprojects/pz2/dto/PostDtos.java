package lightprojects.pz2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class PostDtos {

    private PostDtos() {
    }

    public record PostRequest(
            @NotBlank String title,
            @NotBlank String content,
            @NotNull Long categoryId) {
    }

    public record PostResponse(
            Long id,
            String title,
            String content,
            Long categoryId,
            String categoryName,
            Long authorId,
            String author,
            String createdAt,
            long views,
            long likes) {
    }
}
