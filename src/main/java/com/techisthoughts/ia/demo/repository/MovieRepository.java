package com.techisthoughts.ia.demo.repository;

import com.redis.om.spring.repository.RedisEnhancedRepository;
import com.techisthoughts.ia.demo.repository.entity.MovieEntity;

public interface MovieRepository extends RedisEnhancedRepository<MovieEntity, String> {
}