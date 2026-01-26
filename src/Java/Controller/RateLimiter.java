package Java.Controller;


import io.github.bucket4j.Bucket;


import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static java.time.Duration.ofMinutes;

/**
 * The class responsible for deciding whether a request is allowed to proceed or must be blocked due to too many attempts.
 */

public class RateLimiter {
    //Each user maps to its own bucket (bucket contains login attempts)
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Supplier<Bucket> bucketFactory; // Supplier: thing that makes use buckets

    public RateLimiter(int attempts, Duration window){
        // Define bucketFactory lambda to make buckets the same way every time
        this.bucketFactory = () -> Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(attempts)
                        .refillIntervally(attempts, ofMinutes(window.toMinutes()))
                )
                .build();
    }

    // Should this request be allowed
    public boolean allowRequest(String key){
        // key should be: IP + ":" + username

        // Does the bucket exist in the map
        //      if not -> make new bucket associated with the key
        Bucket bucket = buckets.computeIfAbsent(key, u -> bucketFactory.get());

        // 2. Try to remove token from bucket (1 token per login attempt)
        return bucket.tryConsume(1);
    }

    public void onSuccessfulLogin(String username){
        buckets.remove(username);
    }
}
