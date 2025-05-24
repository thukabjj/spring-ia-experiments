package com.techisthoughts.ia.movieclassification.repository;

import com.redis.om.spring.repository.RedisEnhancedRepository;
import com.techisthoughts.ia.movieclassification.repository.entity.MovieEntity;

public interface MovieRepository extends RedisEnhancedRepository<MovieEntity, String> {
}