package hogar.codelive.externalws.service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import hogar.codelive.externalws.response.PostDataResponse;

@Slf4j
@Service
public class JsonPlaceholderService {

    private final WebClient webClient;

    public JsonPlaceholderService(WebClient.Builder webClientBuilder,
                                  @Value("${api.typicode.url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(Objects.requireNonNull(baseUrl))
                .build();
    }

    @Async
    public CompletableFuture<List<PostDataResponse>> getAllPosts() {
        List<PostDataResponse> posts = webClient.get()
                .uri("/posts")
                .retrieve()
                .bodyToFlux(PostDataResponse.class)
                .collectList()
                .block();

        return CompletableFuture.completedFuture(posts);
    }
}