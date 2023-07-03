package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.validation.group.AddNewUserAction;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotNull(groups = AddNewUserAction.class, message = "Name should not be null")
    @Size(min = 1, max = 256, message = "Name length should not be longer than 256 characters")
    private String name;

    @Email(message = "Email not valid")
    @NotNull(groups = AddNewUserAction.class, message = "Email should not be null")
    @Size(min = 1, max = 512, message = "Email length should not be longer than 512 characters")
    private String email;
}
