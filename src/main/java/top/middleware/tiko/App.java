package top.middleware.tiko;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import top.middleware.tiko.plugin.TikoPluginManager;
import top.middleware.tiko.security.WebSecurityConfiguration;

@SpringBootApplication
@EnableSwagger2
@EnableCaching
@EnableJpaRepositories
@MapperScan(value = {"top.middleware.tiko.**.mapper"})
@Import(value = {WebSecurityConfiguration.class})
public class App {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App.class);
        application.addListeners(new TikoPluginManager());
        application.run(args);

    }

    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder().title("Tiko").build())
                .select()
                .paths(PathSelectors.any())
                .build();
    }
}
