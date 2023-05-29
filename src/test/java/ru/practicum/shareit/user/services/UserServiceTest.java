package ru.practicum.shareit.user.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.utils.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.Factory.createUserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UsersRepository usersRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(usersRepository);
    }

    @Test
    void getAllUsers() {
        userService.getAllUsers();

        verify(usersRepository).findAll();
    }

    @Test
    void createUser() {
        UserDto dto = createUserDto();

        when(usersRepository.save(any())).thenReturn(User.builder().id(1).name(dto.getName()).email(dto.getEmail()).build());

        userService.createUser(dto);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(usersRepository).save(argumentCaptor.capture());

        User value = argumentCaptor.getValue();

        assertEquals(dto.getName(), value.getName());
        assertEquals(dto.getEmail(), value.getEmail());
    }

    @Test
    void updateUser() {
        when(usersRepository.findById(anyInt()))
                .thenReturn(
                        Optional.of(User.builder().id(1).name("name").email("email").build())
                );
        when(usersRepository.save(any()))
                .thenReturn(
                        User.builder().id(1).name("name").email("update").build()
                );

        UserDto result = userService.updateUser(UserDto.builder().id(1).email("update").build());
        verify(usersRepository).findById(1);
        verify(usersRepository).save(any());

        assertEquals(1, result.getId());
        assertEquals("name", result.getName());
        assertEquals("update", result.getEmail());
    }

    @Test
    void shouldErrorWhenUpdateNotExistUser() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(createUserDto()));
    }

    @Test
    void getUserById() {
        when(usersRepository.findById(1))
                .thenReturn(
                        Optional.of(User.builder().id(1).name("name").email("email").build())
                );
        UserDto result = userService.getUserById(1);
        verify(usersRepository).findById(1);
        assertEquals(1, result.getId());
        assertEquals("name", result.getName());
        assertEquals("email", result.getEmail());
    }

    @Test
    void shouldErrorWhenEmptyOptional() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1);
        verify(usersRepository).deleteById(1);
    }
}