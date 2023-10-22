package guru.springframework.msscbreweryclient.web.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Component
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {

    private final Integer maxTotalConnections;
    private final Integer defaultMaxTotalConnections;
    private final Long connectionRequestTimeout;
    

    public BlockingRestTemplateCustomizer(@Value("${sfg.connectionrequesttimeout}") Integer maxTotalConnections, @Value("${sfg.defaultmaxtotalconnections}") Integer defaultMaxTotalConnections, @Value("${sfg.connectionrequesttimeout}") Long connectionRequestTimeout) {
        this.maxTotalConnections = maxTotalConnections;
        this.defaultMaxTotalConnections = defaultMaxTotalConnections;
        this.connectionRequestTimeout = connectionRequestTimeout;
    }


    public ClientHttpRequestFactory clientHttpRequestFactory() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(defaultMaxTotalConnections);

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout, TimeUnit.SECONDS)
                .build();

        CloseableHttpClient client = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(client);
    }

    ;

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(this.clientHttpRequestFactory());

    }
}
