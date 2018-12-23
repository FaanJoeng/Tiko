package top.middleware.tiko.plugin.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import top.middleware.tiko.plugin.dto.Plugin;

import java.util.List;

//@CacheConfig
public interface PluginService {
    //@Cacheable(key = "'username:'+#p0")
    List<Plugin> findAll();

    int dropPluginTables(List<String> tables);

    //@CacheEvict(key = "'username:'+#p0.username")
    List<Plugin> saveAll(List<Plugin> plugins);

    void deleteAll(List<Plugin> plugins);
}
