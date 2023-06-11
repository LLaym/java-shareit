package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.validation.group.AddNewUserAction;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotNull(groups = AddNewUserAction.class, message = "Имя не может быть пустым")
    @Size(min = 1, max = 64, message = "Имя не может быть длинее 64 символов")
    private String name;

    @NotNull(groups = AddNewUserAction.class, message = "Email не может быть пустым")
    @Email(message = "Email не валидный")
    private String email;
}
