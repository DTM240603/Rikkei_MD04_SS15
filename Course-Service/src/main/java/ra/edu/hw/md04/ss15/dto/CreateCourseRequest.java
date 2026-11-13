package ra.edu.hw.md04.ss15.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCourseRequest(
        @NotBlank String name,
        String description
) {
}
