package com.techisthoughts.ia.movieclassification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
	"spring.ai.ollama.base-url=http://localhost:11434",
	"spring.data.redis.host=localhost",
	"spring.data.redis.port=6379",
	"spring.main.lazy-initialization=true"
})
class MovieClassificationApplicationTests {

	@Test
	void contextLoads() {
		// This test simply verifies that the Spring context can load successfully
	}

}
