package lightprojects.pz2.dto;

import jakarta.validation.constraints.NotBlank;

public final class CategoryDtos {

    private CategoryDtos() {
    }

    public record CategoryRequest(@NotBlank String name) {
    }

    public record CategoryResponse(Long id, String name) {
    }
}
