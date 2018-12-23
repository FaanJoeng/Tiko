package top.middleware.tiko.quick.utils;


import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.middleware.tiko.App;
import top.middleware.tiko.quick.QuickDefinition;
import top.middleware.tiko.quick.annotation.Quick;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class QuickUtils {
    public static QuickDefinition getDefinition(Class entity) {
        QuickDefinition result = new QuickDefinition();
        String entityPackage = entity.getPackage().getName();
        String basePackage = entityPackage.substring(0, entityPackage.lastIndexOf("."));
        String entitySimpleName = entity.getSimpleName();
        String entityName = basePackage + ".entity." + entitySimpleName;

        String repositorySimpleName = entitySimpleName + "Repository";
        String repositoryName = basePackage + ".repository." + repositorySimpleName;

        String controllerSimpleName = entitySimpleName + "Controller";
        String controllerName = basePackage + ".controller." + controllerSimpleName;

        Quick annotation = (Quick) entity.getAnnotation(Quick.class);
        result.setRequestMapping(annotation.requestMapping());
        result.setBasePackage(basePackage);
        result.setEntitySimpleName(entitySimpleName);
        result.setEntityName(entityName);
        result.setControllerSimpleName(controllerSimpleName);
        result.setControllerName(controllerName);
        result.setRepositorySimpleName(repositorySimpleName);
        result.setRepositoryName(repositoryName);

        return result;
    }

    public static void writeByteCodeToFile(byte[] bytes, String directory, String fileName) throws IOException {
        File dir = new File(directory);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        Path path = Paths.get(directory + fileName);

        Files.write(path, bytes);
    }

    // 参考 RepositoryBeanDefinitionRegistrarSupport -> registerBeanDefinitions()
    public static void reRegisterRepository(ApplicationContext applicationContext, Environment environment, ResourceLoader resourceLoader) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        StandardAnnotationMetadata metadata = new StandardAnnotationMetadata(
                App.class, true);

        AnnotationRepositoryConfigurationSource configurationSource = new AnnotationRepositoryConfigurationSource(metadata, EnableJpaRepositories.class,
                applicationContext, environment, beanFactory) {
            @Override
            public Streamable<String> getBasePackages() {
                return Streamable.of(AutoConfigurationPackages.get(beanFactory));
            }
        };

        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, resourceLoader, environment);

        delegate.registerRepositoriesIn(beanFactory, new JpaRepositoryConfigExtension());
    }

    public static void registerBean(ApplicationContext applicationContext, String className) throws ClassNotFoundException {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        Class clz = Class.forName(className);
        String beanName = StringUtils.uncapitalize(clz.getSimpleName());

        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(className);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);

        if (StringUtils.endsWithIgnoreCase(className, "Controller")) {
            registerMapping(applicationContext, clz);
        }
    }

    public static String getPackage(String className) {
        int position = StringUtils.lastIndexOf(className, ".");

        return  StringUtils.substring(className, 0, position);
    }

    // 参考 RequestMappingHandlerMapping -> afterPropertiesSet()
    private static void registerMapping(ApplicationContext applicationContext, Class clz) {
        String controllerBeanName = StringUtils.uncapitalize(clz.getSimpleName());
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);

        // 扫描Controller进行注册
        Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(clz,
                (MethodIntrospector.MetadataLookup<RequestMappingInfo>) method -> {
                    try {
                        return getMappingForMethod(method, clz);
                    } catch (Throwable ex) {
                        throw new IllegalStateException("Invalid mapping on handler class [" +
                                clz.getName() + "]: " + method, ex);
                    }
                });

        methods.forEach((method, mappingInfo) -> {
            Method invocableMethod = AopUtils.selectInvocableMethod(method, clz);
            mapping.registerMapping(mappingInfo, controllerBeanName, invocableMethod);
        });
    }


    private static RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    private static RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        return createRequestMappingInfo(requestMapping, null);
    }

    private static RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping, @Nullable RequestCondition<?> customCondition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(requestMapping.path())
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }
        return builder.options(new RequestMappingInfo.BuilderConfiguration()).build();
    }
}
