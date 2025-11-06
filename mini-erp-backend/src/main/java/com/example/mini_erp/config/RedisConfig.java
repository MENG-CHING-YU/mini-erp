package com.example.mini_erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.mini_erp.dto.MachineRealtimeDTO;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, MachineRealtimeDTO> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, MachineRealtimeDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 設定 key 的序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // 設定 value 的序列化方式（使用 JSON）
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }
}