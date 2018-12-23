package top.middleware.tiko.quick;

import lombok.Data;

@Data
public class QuickDefinition {
    private String[] requestMapping;

    private String basePackage;

    private String entitySimpleName;

    private String entityName;

    private String controllerName;

    private String controllerSimpleName;

    private String repositoryName;

    private String repositorySimpleName;

    private String serviceName;

    private String serviceSimpleName;

    private String serviceImplName;

    private String serviceImplSimpleName;
}
