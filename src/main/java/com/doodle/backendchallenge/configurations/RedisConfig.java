package com.doodle.backendchallenge.configurations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private Integer redisPort;

  @Value("${spring.redis.cluster.nodes}")
  List<String> clusterNodes;

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    RedisClusterConfiguration redisClusterConfiguration =
        new RedisClusterConfiguration();
    RedisClusterNode masterNode = new RedisClusterNode(redisHost, redisPort);
    redisClusterConfiguration.clusterNode(masterNode);
    JedisClientConfiguration jedisClientConfiguration =
        JedisClientConfiguration.builder().usePooling().build();
    JedisConnectionFactory jedisConFactory =
        new JedisConnectionFactory(redisClusterConfiguration, jedisClientConfiguration);
    jedisConFactory.afterPropertiesSet();
    return jedisConFactory;
  }

  @Bean
  RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    ObjectMapper mapper =
        new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer =
        new GenericJackson2JsonRedisSerializer(mapper);
    template.setValueSerializer(genericJackson2JsonRedisSerializer);

    return template;
  }

  @Bean
  public RedisCustomConversions redisCustomConversions(
      OffsetDateTimeToBytesConverter offsetToBytes, BytesToOffsetDateTimeConverter bytesToOffset) {
    return new RedisCustomConversions(Arrays.asList(offsetToBytes, bytesToOffset));
  }
}
