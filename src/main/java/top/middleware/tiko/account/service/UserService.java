package top.middleware.tiko.account.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import top.middleware.tiko.account.dto.User;

@CacheConfig(cacheNames = {"tiko:account:user"})
public interface UserService extends UserDetailsService {
    @Cacheable(key = "#p0")
    User findById(@NonNull Long userId);

    @CacheEvict(key = "#user.username")
    User save(@NonNull User user);

    @CacheEvict(key = "#user.username")
    void delete(@NonNull Long userId);
}
