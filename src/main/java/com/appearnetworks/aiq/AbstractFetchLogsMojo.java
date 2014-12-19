package com.appearnetworks.aiq;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public abstract class AbstractFetchLogsMojo extends AbstractAIQMojo {

    @Parameter(property = "url")
    private URL url;

    @Parameter(property = "propertiesPath")
    private String propertiesPath;

    /**
     * Executes the mojo for given action.
     *
     * @param action The name of the action to be executed by the integration supervison, must not be null.
     *
     * @throws MojoExecutionException in case when provided data is invalid.
     * @throws MojoFailureException in case when execution fails.
     */
    protected void execute(final String action) throws MojoExecutionException, MojoFailureException {
        final Properties properties;
        try {
            properties = PropertiesUtil.getInstance().loadProperties(propertiesPath);
        } catch (IOException e) {
            throw new MojoFailureException("Could not load properties file");
        }

        final String org = properties.getProperty("aiq.orgname");
        final String username = properties.getProperty("aiq.username");
        final String password = properties.getProperty("aiq.password");
        final String aiqUrl = properties.getProperty("aiq.url");
        final String solution = properties.getProperty("aiq.solution");

        getLog().info("Fetching logs from the org [" + org + "]");

        final HttpClient client = new DefaultHttpClient();
        final HttpGet get = new HttpGet(buildIntegrationURI(url, org, action));

        final String accessToken = authenticate(aiqUrl, username, password, org, solution).getAccessToken();
        get.setHeader(HttpHeaders.AUTHORIZATION, "BEARER " + accessToken);

        try {
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final InputStream input = response.getEntity().getContent();
                IOUtils.copy(input, System.out);
                input.close();
            } else {
                throw new MojoFailureException("Failed to fetch logs, the status code is [" +
                                               response.getStatusLine().getStatusCode() + "] and error message is [" +
                                               response.getStatusLine().getReasonPhrase() + "]");
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }
}
