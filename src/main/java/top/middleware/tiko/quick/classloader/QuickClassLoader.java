package top.middleware.tiko.quick.classloader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import top.middleware.tiko.quick.QuickDefinition;
import top.middleware.tiko.quick.annotation.Quick;
import top.middleware.tiko.quick.bytecode.Generator;
import top.middleware.tiko.quick.utils.QuickUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Quick 模块类加载器
 * <p>
 * TODO 改进计划
 * 1. 先检查controller repository 对应的字节码是否存在
 * 存在 -> 修改class 添加方法 并重新加载
 * 不存在 ->  新建class 加载
 */
@Slf4j
@Component
public class QuickClassLoader
        extends ClassLoader
        implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent>,
        EnvironmentAware, ResourceLoaderAware {

    private static Map<String, QuickDefinition> definitionMap = new HashMap<>(64);
    private ApplicationContext applicationContext;
    private ResourceLoader resourceLoader;
    private Environment environment;

    public QuickClassLoader() {
        // 设置父加载器为 ExtClassLoader
        super(QuickClassLoader.class.getClassLoader().getParent());
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clazz = getSystemClassLoader().loadClass(name);

        return clazz == null ? findClass(name) : clazz;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (definitionMap.containsKey(className)) {
            QuickDefinition definition = definitionMap.get(className);
            if (StringUtils.endsWithIgnoreCase(className, "repository")) {
                // 处理@Repository生成时空指针问题
                definePackage(QuickUtils.getPackage(className), null, null, null, null, null, null, null);
                byte[] bytes = new byte[0];
                try {
                    bytes = Generator.genRepository(definition);
                    // 写字节码到目录 生成Repository代理类
                    String directory = "./target/classes/" + definition.getBasePackage().replace(".", "/") + "/repository/";
                    String fileName = definition.getRepositorySimpleName() + ".class";

                    // JPA 扫描@Repository类的时候只扫文件？？？临时解决方案 手动生成repository的字节码文件
                    // 参见ClassPathScanningCandidateComponentProvider.java -> scanCandidateComponents()
                    QuickUtils.writeByteCodeToFile(bytes, directory, fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return defineClass(definition.getRepositoryName(), bytes, 0, bytes.length);
            } else if (StringUtils.endsWithIgnoreCase(className, "controller")) {
                byte[] bytes = new byte[0];
                try {
                    bytes = Generator.genController(definition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return defineClass(definition.getControllerName(), bytes, 0, bytes.length);
            } else if (StringUtils.endsWithIgnoreCase(className, "Service")) {
                // TODO 暂时只做了两层 仅验证可行性
            } else if (StringUtils.endsWithIgnoreCase(className, "ServiceImpl")) {
                // TODO
            }
        }

        return super.findClass(className);
    }

    @PostConstruct
    public void init() throws Exception {
        // 设置ApplicationClassLoader的parent为this
        // 目前类加载器的顺序 从下至上:app -> quick -> ext -> bootstrap
        String FILED_PARENT = "parent";
        Field field = ClassLoader.class.getDeclaredField(FILED_PARENT);
        field.setAccessible(true);
        field.set(ClassLoader.getSystemClassLoader(), this);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Map<String, Object> entities = applicationContext.getBeansWithAnnotation(Quick.class);

        // 收集定义
        for (Map.Entry<String, Object> entity : entities.entrySet()) {
            Class cls = entity.getValue().getClass();
            QuickDefinition definition = QuickUtils.getDefinition(cls);
            definitionMap.put(definition.getRepositoryName(), definition);
            definitionMap.put(definition.getControllerName(), definition);
        }

        // 注册Bean
        for (Map.Entry<String, QuickDefinition> definitionEntry : definitionMap.entrySet()) {
            try {
                QuickUtils.registerBean(applicationContext, definitionEntry.getKey());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // 重新生成@Repository注解类的代理类
        QuickUtils.reRegisterRepository(applicationContext, environment, resourceLoader);
    }

    @Override
    protected Package getPackage(String name) {
        return super.getPackage(name);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}