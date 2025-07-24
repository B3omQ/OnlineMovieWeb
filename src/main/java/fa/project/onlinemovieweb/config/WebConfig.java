package fa.project.onlinemovieweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/assets/avatars/**")
                .addResourceLocations("file:resources/", "file:assets/avatars/")
                .setCachePeriod(0);

        registry
                .addResourceHandler("/assets/banners/**")
                .addResourceLocations("file:assets/banners/")
                .setCachePeriod(0);

        registry
                .addResourceHandler("/assets/posters/**")
                .addResourceLocations("file:assets/posters/")
                .setCachePeriod(0);
    }
}
