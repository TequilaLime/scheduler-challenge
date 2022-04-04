package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.model.dto.UserCollectionDto;
import com.doodle.backendchallenge.model.dto.UserDto;
import com.doodle.backendchallenge.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserDto createUser(@RequestBody UserDto user) {
    return userService.createUser(user);
  }

  @GetMapping
  @ResponseBody
  public UserCollectionDto readUsers() {
    return userService.readUsers();
  }

  @GetMapping("/{id}")
  @ResponseBody
  public UserDto readUser(@PathVariable UUID id) {
    return userService.readUser(id);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
  }
}
