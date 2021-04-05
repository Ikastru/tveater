package com.example.tveater.services;

import com.example.tveater.domain.User;
import com.example.tveater.repo.RoleRepository;
import com.example.tveater.repo.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailSenders mailSenders;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RoleRepository roleRepository;

    @Test
    void addUserTest() {
        User user = new User();
        user.setEmail("some@mail.ru");
        boolean isUserCreated = userService.addUser(user);
        Assert.assertTrue(isUserCreated);
        Assert.assertNotNull(user.getActivationCode());
        Assert.assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(roleRepository.getOne(1L))));

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(mailSenders, Mockito.times(1)).send(
                ArgumentMatchers.eq(user.getEmail()), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    void addUserFailTest() {
        User user = new User();
        user.setUsername("John");
        Mockito.doReturn(new User()).when(userRepository).findByUsername("John");
        boolean isUserCreated = userService.addUser(user);
        Assert.assertFalse(isUserCreated);
        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSenders, Mockito.times(0)).send(
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    void activateUserTest() {
        User user = new User();
        user.setActivationCode("bingo");
        Mockito.doReturn(user).when(userRepository).findByActivationCode("activate");
        boolean isUserActivator = userService.activateUser("activate");
        Assert.assertTrue(isUserActivator);
        Assert.assertNull(user.getActivationCode());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void activateUserFailTest() {
        boolean isUserActivator = userService.activateUser("activate");
        Assert.assertFalse(isUserActivator);
        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }
}