package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.validation.group.AddNewUserAction;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotNull(groups = AddNewUserAction.class, message = "Имя не может быть пустым")
    @Size(min = 1, max = 256, message = "Имя не может быть длинее 256 символов")
    private String name;

    @Email(message = "Email не валидный")
    @NotNull(groups = AddNewUserAction.class, message = "Email не может быть пустым")
    @Size(min = 1, max = 512, message = "Email не может быть длинее 512 символов")
    private String email;
}
