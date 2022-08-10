package mauriNetwork.mauriPrueba;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/imagenes/**").addResourceLocations("file:C:/Users/Maurizzy/Desktop/Curso Egg/Modulo 5 -Spring/18 - Spring - MVC/Ejercicio/1/imagenes/");
    }
    
    
}
