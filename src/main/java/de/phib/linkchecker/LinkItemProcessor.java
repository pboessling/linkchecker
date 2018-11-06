package de.phib.linkchecker;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

/**
 * ItemProcessor, which performs the link checking.
 */
public class LinkItemProcessor implements ItemProcessor<Link, Link> {

    private static final Logger LOG = LoggerFactory.getLogger(LinkItemProcessor.class);

    /**
     * Processes a Link by sending an http get request to the url of the given link, and returning a new Link wrapping
     * the url and the http response status code.
     *
     * @param link the link
     * @return a new link wrapping the url and the status code
     */
    @Override
    public Link process(final Link link) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        String url = link.getUrl();
        int status = -1;

        try {
            ResponseEntity<String> response = restTemplate
                    .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
            status = response.getStatusCode().value();
        } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerException) {
            status = httpClientOrServerException.getStatusCode().value();
        } catch (Exception e) {
            LOG.error("An error occured, while trying to access url '" + url + "'. Message: " + e.getMessage());
        }

        final Link checkedLink = new Link(url, status);
        LOG.info("Checked link - " + checkedLink);

        return checkedLink;
    }

    /**
     * Creates a ClientHttpRequestFactory.
     *
     * @return a ClientHttpRequestFactory
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        int timeout = 5000;

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();

        clientHttpRequestFactory.setConnectTimeout(timeout);
        clientHttpRequestFactory.setHttpClient(httpClient());

        return clientHttpRequestFactory;
    }

    /**
     * Creates a CloseableHttpClient.
     *
     * @return a CloseableHttpClient
     */
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
