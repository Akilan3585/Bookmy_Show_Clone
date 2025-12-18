package com.bookmyshow.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache Configuration
 * 
 * Caching Strategy:
 * 1. Movies Cache: Stores movie list and individual movie details
 *    - Reduces DB queries for frequently browsed movies
 *    - Evicted when admin creates/updates/deletes movies
 * 
 * 2. Theaters Cache: Stores theaters by city
 *    - Frequently accessed when selecting shows
 *    - Evicted when admin modifies theater data
 * 
 * 3. Shows Cache: Stores shows by movie, city, date combinations
 *    - Most common query pattern for show selection
 *    - Evicted when shows are created/modified or after booking
 * 
 * 4. Seats Cache: Stores seat availability for shows
 *    - Critical for seat selection UI
 *    - Evicted after every booking to ensure real-time accuracy
 *    - Prevents double booking with pessimistic locking
 * 
 * Cache Eviction Flow:
 * - Booking flow: Seat booking evicts seats and shows caches
 * - Admin operations: Create/update/delete evicts respective caches
 * - Cache TTL: Using simple in-memory cache (for production, use Redis with TTL)
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("movies", "theaters", "shows", "seats");
    }
}
