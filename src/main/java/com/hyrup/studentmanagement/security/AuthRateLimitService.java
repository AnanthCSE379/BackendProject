package com.hyrup.studentmanagement.security;

import com.hyrup.studentmanagement.config.SecurityProperties;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthRateLimitService {

    private static final long WINDOW_MILLIS = 60_000;
    private static final long CLEANUP_AFTER_MILLIS = 5 * 60_000;

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();
    private final SecurityProperties securityProperties;

    public AuthRateLimitService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public boolean isAllowed(String key) {
        long now = System.currentTimeMillis();

        WindowCounter counter = counters.computeIfAbsent(key, ignored -> new WindowCounter(now, 0));

        synchronized (counter) {
            if (now - counter.windowStart >= WINDOW_MILLIS) {
                counter.windowStart = now;
                counter.count = 0;
            }

            if (counter.count >= securityProperties.getAuthRateLimitPerMinute()) {
                return false;
            }

            counter.count++;
        }

        cleanupIfNeeded(now);
        return true;
    }

    private void cleanupIfNeeded(long now) {
        if (counters.size() < 2_000) {
            return;
        }

        counters.entrySet().removeIf(entry -> now - entry.getValue().windowStart > CLEANUP_AFTER_MILLIS);
    }

    private static final class WindowCounter {
        private long windowStart;
        private int count;

        private WindowCounter(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
