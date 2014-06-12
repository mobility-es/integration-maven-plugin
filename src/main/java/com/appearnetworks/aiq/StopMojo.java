package com.appearnetworks.aiq;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Mojo(name = "ia.stop")
@Execute(phase = LifecyclePhase.INITIALIZE)
public class StopMojo extends AbstractAIQMojo {
    @Parameter(property = "url")
    private URL url;

    @Parameter(property = "propertiesPath")
    private String propertiesPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Properties properties;
        try {
            properties = PropertiesUtil.getInstance().loadProperties(propertiesPath);
        } catch (IOException e) {
            throw new MojoFailureException("Could not load properties file");
        }

        final String org = properties.getProperty("aiq.orgname");
        final String username = properties.getProperty("aiq.username");
        final String password = properties.getProperty("aiq.password");
        final String aiqUrl = properties.getProperty("aiq.url");

        getLog().info("Stop integration adapter for org [" + org + "]");

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(buildIntegrationURI(url, org, "ia.stop"));

        addAuthenticationHeader(post, aiqUrl, username, password, org);

        try {
            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                getLog().info("Integration adapter stopped successfully.");
            } else {
                throw new MojoFailureException("Failed to stop integration adapter, the status code is [" +
                        response.getStatusLine().getStatusCode() + "] and error message is [" +
                        response.getStatusLine().getReasonPhrase() + "]");
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }
}
