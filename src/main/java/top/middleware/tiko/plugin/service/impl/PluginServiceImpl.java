package top.middleware.tiko.plugin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.middleware.tiko.plugin.dto.Plugin;
import top.middleware.tiko.plugin.mapper.PluginMapper;
import top.middleware.tiko.plugin.repository.PluginRepository;
import top.middleware.tiko.plugin.service.PluginService;

import java.util.List;

@Service("pluginService")
public class PluginServiceImpl implements PluginService {
    @Autowired
    private PluginMapper pluginMapper;

    @Autowired
    private PluginRepository pluginRepository;

    @Override
    public List<Plugin> findAll() {
        return pluginRepository.findAll();
    }

    @Override
    public int dropPluginTables(List<String> tables) {
        return pluginMapper.dropPluginTables(tables);
    }

    @Override
    public List<Plugin> saveAll(List<Plugin> plugins) {
        return pluginRepository.saveAll(plugins);
    }

    @Override
    public void deleteAll(List<Plugin> plugins) {
        pluginRepository.deleteAll(plugins);
    }
}
