package com.github.gleans.controller;

import com.github.gleans.dao.UserRepository;
import com.github.gleans.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserController {

    private  UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(cacheNames = "userAll")
    @GetMapping("user/all")
    public Object getUserAll() {
        return userRepository.findAll();
    }

    @Cacheable(value = "users", key = "#userId", unless = "#result.money < 10000")
    @GetMapping("user/con/{userId}")
    public Object getUserByCondition(@PathVariable Long userId) {
        return userRepository.findById(userId);
    }

    @CachePut(value = "users", key = "#user.id")
    @PutMapping("/update")
    public User updatePersonByID(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }

    @CacheEvict(value = "users", allEntries=true)
    @DeleteMapping("/{id}")
    public void deleteUserByID(@PathVariable Long id) {
        List<User> userListOld =  userRepository.findAll();
        log.info("删除前:{}", userListOld.toString());
        userRepository.deleteById(id);
        List<User> userList =  userRepository.findAll();
        log.info("删除后:{}", userList.toString());
    }
}