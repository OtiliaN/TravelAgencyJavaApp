package org.example;

import org.example.domain.Flight;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.concurrent.Callable;

public class FlightClient {
    private final RestClient restClient = RestClient.builder()
            .requestInterceptor(new CustomRestFlightInterceptor())
            .build();

    public static final String URL = "http://localhost:8080/org/flights";

    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) {
            throw new ServiceException(e.getMessage());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public Flight[] getAll() {
        return execute(() -> restClient.get()
                .uri(URL)
                .retrieve()
                .body(Flight[].class));
    }

    public Flight getById(Long id) {
        return execute(() -> restClient.get()
                .uri(URL + "/" + id)
                .retrieve()
                .body(Flight.class));
    }

    public Flight create(Flight flight) {
        return execute(() -> restClient.post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(flight)
                .retrieve()
                .body(Flight.class));
    }

    public Flight update(Long id, Flight flight) {
        return execute(() -> restClient.put()
                .uri(URL + "/" + id)
                .body(flight)
                .retrieve()
                .body(Flight.class));
    }

    public void delete(Long id) {
        execute(() -> restClient.delete()
                .uri(URL + "/" + id)
                .retrieve()
                .toBodilessEntity());
    }

    public static class CustomRestFlightInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            System.out.println("Sending " + request.getMethod() + " to " + request.getURI() + " with body [" + new String(body) + "]");
            ClientHttpResponse response = execution.execute(request, body);
            System.out.println("Response status: " + response.getStatusCode());
            return response;
        }
    }
}
