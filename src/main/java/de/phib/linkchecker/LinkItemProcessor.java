package de.phib.linkchecker;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

public class LinkItemProcessor implements ItemProcessor<Link, Link> {

    private static final Logger LOG = LoggerFactory.getLogger(LinkItemProcessor.class);

    @Override
    public Link process(final Link link) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        String url = link.getUrl();
        int status;

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
            status = response.getStatusCode().value();
        } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
            if (HttpStatus.NOT_FOUND.equals(httpClientOrServerExc.getStatusCode())) {
                // your handling of "NOT FOUND" here
                // e.g. throw new RuntimeException("Your Error Message here", httpClientOrServerExc);
            } else {
                // your handling of other errors here
            }

            status = httpClientOrServerExc.getStatusCode().value();
        }

        final Link checkedLink = new Link(url, status);
        LOG.info("Checked link - " + checkedLink);

        return checkedLink;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        int timeout = 5000;

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();

        clientHttpRequestFactory.setConnectTimeout(timeout);
        clientHttpRequestFactory.setHttpClient(httpClient());

        return clientHttpRequestFactory;
    }

    private CloseableHttpClient httpClient() {
        CloseableHttpClient httpClient = null;

        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

            httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();
        } catch (Exception e) {
            LOG.error("An error occured while trying to create an http client.", e);
        }

        return httpClient;
    }

}
