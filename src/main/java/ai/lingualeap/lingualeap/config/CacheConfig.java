package ai.lingualeap.lingualeap.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Local cache manager for development and testing.
     */
    @Bean
    @Profile({"dev", "test"})
    public CacheManager localCacheManager() {
        //TODO In production, consider using Redis or other distributed cache.
        return new ConcurrentMapCacheManager(
                "userProgress",
                "lessonProgress",
                "exerciseProgress",
                "courseProgress",
                "userActivitySummary",
                "availableLessons"
        );
    }

    /**
     * Cache configuration constants.
     */
    public static final class CacheNames {
        public static final String USER_PROGRESS = "userProgress";
        public static final String LESSON_PROGRESS = "lessonProgress";
        public static final String EXERCISE_PROGRESS = "exerciseProgress";
        public static final String COURSE_PROGRESS = "courseProgress";
        public static final String USER_ACTIVITY = "userActivitySummary";
        public static final String AVAILABLE_LESSONS = "availableLessons";

        private CacheNames() {
        }
    }
}
