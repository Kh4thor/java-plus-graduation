package ru.practicum.ewm.service.category.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequest {
    @NotBlank
    @Size(min=1, max=50)
    private String name;
}
