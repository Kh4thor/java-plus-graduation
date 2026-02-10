package malyshev.egor.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {

    @Builder.Default
    private Set<Long> events = new HashSet<>();

    @Builder.Default
    private Boolean pinned = false;

    @NotBlank(message = "title must not be blank")
    @Size(min = 1, max = 50, message = "Длина title должна быть от 1 до 50 символов")
    private String title;
}