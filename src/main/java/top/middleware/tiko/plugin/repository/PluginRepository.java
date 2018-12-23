package top.middleware.tiko.plugin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.middleware.tiko.plugin.dto.Plugin;

@Repository
public interface PluginRepository extends JpaRepository<Plugin, Long> {
}
