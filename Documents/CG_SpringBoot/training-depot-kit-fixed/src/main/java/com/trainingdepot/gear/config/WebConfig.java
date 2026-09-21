package com.trainingdepot.gear.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;
    private final AuthInterceptor authInterceptor;
    private final CustomerInterceptor customerInterceptor;
    private final AdminInterceptor adminInterceptor;

    public WebConfig(@Value("${file.upload-dir}") String uploadDir, AuthInterceptor authInterceptor,
            CustomerInterceptor customerInterceptor, AdminInterceptor adminInterceptor) {
        this.uploadDir = uploadDir;
        this.authInterceptor = authInterceptor;
        this.customerInterceptor = customerInterceptor;
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        registry.addResourceHandler("/uploads/images/**").addResourceLocations("file:" + location);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Everything a customer touches - the homepage, browsing, the kit,
        // and checkout/orders - requires depot login. Order matters: auth
        // first (are they logged in at all), then role (are they a USER,
        // not an ADMIN riding a leftover session).
        String[] customerRoutes = {
                "/", "/equipment/**", "/kit/**", "/checkout/**", "/payment/**", "/orders/**"
        };
        registry.addInterceptor(authInterceptor).addPathPatterns(customerRoutes);
        registry.addInterceptor(customerInterceptor).addPathPatterns(customerRoutes);

        // The admin area is gated entirely on its own (see AdminInterceptor,
        // which checks both login and role) so it never shares a login path
        // or a redirect target with the customer flow above. /admin/login
        // itself has to stay excluded or this would redirect to itself.
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");
    }
}
