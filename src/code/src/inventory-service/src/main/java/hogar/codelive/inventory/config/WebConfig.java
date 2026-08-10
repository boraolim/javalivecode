package hogar.codelive.inventory.config;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import hogar.codelive.inventory.constants.ApiConstants;
import hogar.codelive.inventory.middleware.HttpLoggingInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final @NonNull HttpLoggingInterceptor httpLoggingInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(httpLoggingInterceptor)
            .addPathPatterns(ApiConstants.API_ALL_PATTERN)
            .excludePathPatterns(
                        ApiConstants.API_SWAGGER_UI,
                        ApiConstants.API_SWAGGER_HTML,
                        ApiConstants.API_SWAGGER_DOCS,
                        ApiConstants.API_SWAGGER_RESC,
                        ApiConstants.API_SWAGGER_WEBJARS,
                        ApiConstants.API_SWAGGER_FAVICON,
                        ApiConstants.API_ERROR_URL
                );
    }
}
