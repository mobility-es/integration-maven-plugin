package com.appearnetworks.aiq;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
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

@Mojo(name = "server.clean")
@Execute(phase = LifecyclePhase.INITIALIZE)
public class CleanServerMojo extends AbstractAIQMojo {

    @Parameter(property = "url")
    private URL url;

    @Parameter(property = "propertiesPath")
    private String propertiesPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
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

        getLog().info("Cleaning data for org [" + org + "] and solution [" + solution + "]");

        final JsonNode authenticationResponse = authenticate(aiqUrl, username, password, org, solution);

        final HttpClient client = new DefaultHttpClient();
        final HttpPost post = new HttpPost(extractLink(authenticationResponse, "clearsolution"));

        post.setHeader(HttpHeaders.AUTHORIZATION, "BEARER " + extractAccessToken(authenticationResponse));
        post.setEntity(new StringEntity("{}", ContentType.APPLICATION_JSON));

        try {
            final HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                getLog().info("Data cleaned successfully.");
            } else {
                throw new MojoFailureException("Failed to clean data, the status code is [" +
                                               response.getStatusLine().getStatusCode() + "] and error message is [" +
                                               response.getStatusLine().getReasonPhrase() + "]");
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }
}
