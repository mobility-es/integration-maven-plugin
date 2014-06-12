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

public abstract class AbstractReadLogsMojo extends AbstractAIQMojo {
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
     * @throws MojoFailureException   in case when execution fails.
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

        final String accessToken = fetchAccessToken(aiqUrl, username, password, org);

        getLog().info("Tailing logs from the org [" + org + "]");

        final HttpClient client = new DefaultHttpClient();

        String since = null;

        try {
            while (true) {
                HttpGet get = new HttpGet(buildIntegrationURI(url, org, action));
                get.setHeader(HttpHeaders.AUTHORIZATION, "BEARER " + accessToken);
                if (since != null) get.setHeader(HttpHeaders.IF_MODIFIED_SINCE, since);
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    since = response.getFirstHeader(HttpHeaders.LAST_MODIFIED).getValue();
                    final InputStream input = response.getEntity().getContent();
                    IOUtils.copy(input, System.out);
                    input.close();
                } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
                    // just wait
                } else {
                    throw new MojoFailureException("Failed to tail logs, the status code is [" +
                            response.getStatusLine().getStatusCode() + "] and error message is [" +
                            response.getStatusLine().getReasonPhrase() + "]");
                }

                Thread.sleep(5*1000);
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        } catch (InterruptedException ignore) {
            // just exit
        }
    }
}
