package com.rpicloud;

import com.fasterxml.classmate.TypeResolver;
import com.github.javafaker.Faker;
import com.rpicloud.models.Pie;
import com.rpicloud.repositories.PieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


import java.time.LocalDate;
import java.util.Arrays;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@SpringBootApplication
@EnableSwagger2
public class SpringBootRestExampleApplication {

    private final Faker faker = new Faker();

    @Autowired
    private TypeResolver typeResolver;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestExampleApplication.class, args);
	}

    @Bean
    public CommandLineRunner initializeDb(PieRepository repository){
        return (args) -> {
            repository.deleteAll();
            //Insert some random pies
            for(int i = 0; i < 20; i++) {
                repository.save(new Pie(faker.lorem().word(), faker.lorem().sentence()));
            }
        };
    }

    @Bean
    public Docket swaggerSettings() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .directModelSubstitute(LocalDate.class, String.class).genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(newRule(typeResolver.resolve(DeferredResult.class, typeResolver.resolve(ResponseEntity.class, WildcardType.class)), typeResolver.resolve(WildcardType.class)));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot Rest Swagger documentation")
                .description("This is the documentation of the Spring Boot Rest Swagger documentation")
                .contact("Kasper Nissen")
                .license("Apache License Version 2.0")
                .version("1.0")
                .build();
    }

}
