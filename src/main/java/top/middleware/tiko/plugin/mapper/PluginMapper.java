package top.middleware.tiko.plugin.mapper;

import org.apache.ibatis.annotations.Param;
import top.middleware.tiko.plugin.dto.Plugin;

import java.util.List;

public interface PluginMapper {
    int dropPluginTables(@Param("tables") List<String> tables);
}
