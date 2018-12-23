package top.middleware.tiko.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.middleware.tiko.plugin.dto.Plugin;
import top.middleware.tiko.plugin.service.PluginService;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 插件初始化管理器
 * <p>
 * TODO 实现插件版本升级
 */
@Slf4j
@Component
public class TikoPluginManager implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (dataSource != null) {
            // 参考org.springframework.boot.autoconfigure.jdbc.DataSourceInitializer
            Resource[] definitions = null;
            ResourcePatternResolver loader = new PathMatchingResourcePatternResolver();

            try {
                try {
                    definitions = loader.getResources("classpath*:/top/middleware/tiko/plugin/**/def.json");
                } catch (FileNotFoundException e) {
                    log.info("插件加载没有找到描述文件。");
                }

                // 已安装的插件
                List<Plugin> installedPlugins = pluginService.findAll();

                // 目前最新的插件集合
                List<Plugin> currentPlugins = new ArrayList<>();

                // 需要新安装的插件 最新插件集合对已安装插件集合取差 currentPlugins - installedPlugins
                List<Plugin> newPlugins = new ArrayList<>();
                Resource[] scripts;
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

                if (definitions != null) {
                    for (Resource definition : definitions) {
                        Plugin pluginDef = objectMapper.readValue(definition.getInputStream(), Plugin.class);
                        currentPlugins.add(pluginDef);
                        if (!installedPlugins.contains(pluginDef)) {
                            newPlugins.add(pluginDef);

                            // 获取新添加插件表初始化文件
                            String relatedPackage = pluginDef.getRelatedPackage().replace(".", "/");
                            scripts = loader.getResources("classpath*:/" + relatedPackage + "/**/script/*.sql");

                            for (Resource script : scripts) {
                                populator.addScript(script);
                            }

                            pluginDef.setTables(String.join(",", pluginDef.getTablesList()));
                        }
                    }

                    // 创建新添加插件对应的表
                    populator.setContinueOnError(false);
                    populator.setSeparator(";");
                    DatabasePopulatorUtils.execute(populator, dataSource);
                }

                if (!CollectionUtils.isEmpty(newPlugins)) {
                    pluginService.saveAll(newPlugins);
                }

                // 已安装的插件集合对最新插件集合取差 得到多余的插件集合
                installedPlugins.removeAll(currentPlugins);

                // 移除多的相关表
                List<String> toDropTables = new ArrayList<>();
                for (Plugin plugin : installedPlugins) {
                    String tables = plugin.getTables();
                    toDropTables.addAll(Arrays.asList(tables.split(",")));
                }

                if (!CollectionUtils.isEmpty(toDropTables)) {
                    pluginService.dropPluginTables(toDropTables);
                }

                // 移除plugin表多余插件数据
                if (!CollectionUtils.isEmpty(installedPlugins)) {
                    pluginService.deleteAll(installedPlugins);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
