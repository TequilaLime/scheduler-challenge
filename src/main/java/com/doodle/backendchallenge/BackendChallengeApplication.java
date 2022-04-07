/* (C) 2022 */
package com.doodle.backendchallenge;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendChallengeApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackendChallengeApplication.class, args);
  }

  /**
   * Will leave it here, as there is no Configuration class yet.
   *
   * @return ModelMapper
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setSkipNullEnabled(true);
    return modelMapper;
  }
}
