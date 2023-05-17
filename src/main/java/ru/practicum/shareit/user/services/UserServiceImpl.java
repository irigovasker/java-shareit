package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.user.utils.UserMapper;
import ru.practicum.shareit.utils.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return usersRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(usersRepository.save(UserMapper.toNewUser(userDto)));
    }

    @Override
    public UserDto getUserById(int id) {
        return UserMapper.toUserDto(usersRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User userFromDB = usersRepository.findById(userDto.getId()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(usersRepository.save(UserMapper.updateFields(userFromDB, userDto)));
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        usersRepository.deleteById(userId);
    }
}