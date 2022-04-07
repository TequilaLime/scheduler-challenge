/* (C) 2022 */
package com.doodle.backendchallenge.service;

import com.doodle.backendchallenge.entity.UserEntity;
import com.doodle.backendchallenge.exceptions.NotFoundException;
import com.doodle.backendchallenge.model.dto.UserCollectionDto;
import com.doodle.backendchallenge.model.dto.UserDto;
import com.doodle.backendchallenge.repository.UserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private ModelMapper modelMapper;

  @Transactional
  public UserDto createUser(UserDto userDto) {
    UserEntity userEntity = convertToEntity(userDto);
    return convertToDto(userRepository.save(userEntity));
  }

  @Transactional(readOnly = true)
  public UserCollectionDto readUsers(Integer offset, Integer limit) {
    Sort unsorted =
        Sort.unsorted(); // will keep it as a default value, but in theory sort implementation is
    // done with requirements
    PageRequest pageRequest = PageRequest.of(offset, limit, unsorted);
    Page<UserEntity> pagedUserEntities = userRepository.findAll(pageRequest);
    return convertToDto(pagedUserEntities);
  }

  @Transactional(readOnly = true)
  public UserDto readUser(UUID id) {
    UserEntity userEntity =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.error("user with '{}' id is not found", id);
                  throw new NotFoundException("User not found");
                });
    return convertToDto(userEntity);
  }

  @Transactional
  public void deleteUser(UUID id) {
    if (!userRepository.existsById(id)) {
      log.warn("User to be delete with id: '{}' is not found", id);
      throw new NotFoundException("User not found");
    }
    userRepository.deleteById(id);
  }

  @Transactional
  public void deleteAllUsers() {
    userRepository.deleteAll();
  }

  private UserEntity convertToEntity(UserDto userDto) {
    return modelMapper.map(userDto, UserEntity.class);
  }

  public UserDto convertToDto(UserEntity userEntity) {
    return modelMapper.map(userEntity, UserDto.class);
  }

  private UserCollectionDto convertToDto(Page<UserEntity> page) {
    UserCollectionDto userCollectionDto = new UserCollectionDto();
    userCollectionDto.setItems(page.getContent().stream().map(this::convertToDto).toList());
    userCollectionDto.setPage(page.getPageable().getOffset());
    userCollectionDto.setPageSize((long) page.getNumberOfElements());
    userCollectionDto.setTotalSize(page.getTotalElements());

    return userCollectionDto;
  }
}
