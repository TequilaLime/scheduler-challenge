/* (C) 2022 */
package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.exceptions.PreconditionFailedException;
import com.doodle.backendchallenge.model.dto.UserCollectionDto;
import com.doodle.backendchallenge.model.dto.UserDto;
import com.doodle.backendchallenge.service.UserService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    if (user.getName().isBlank()) {
      log.warn("user name is blank in request");
      throw new PreconditionFailedException("User name is blank");
    }
    return userService.createUser(user);
  }

  @GetMapping
  @ResponseBody
  public UserCollectionDto readUsers(
      @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
    return userService.readUsers(offset, limit);
  }

  @GetMapping("/{id}")
  @ResponseBody
  public UserDto readUser(@PathVariable UUID id) {
    if (id == null) {
      log.warn("UUID object provided is null in request");
      throw new PreconditionFailedException("UUID object provided is null");
    }
    return userService.readUser(id);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
  }
}
