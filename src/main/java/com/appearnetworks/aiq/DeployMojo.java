package com.appearnetworks.aiq;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Mojo(name = "ia.deploy")
@Execute(phase = LifecyclePhase.PACKAGE)
public class DeployMojo extends AbstractAIQMojo {
    @Parameter(property = "url")
    private URL url;

    @Parameter(property = "propertiesPath")
    private String propertiesPath;

    @Parameter(property = "deploy.file")
    private File file;

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
        final String platformUrl = properties.getProperty("aiq.url");

        getLog().info("Deploy integration adapter [" + file + "] for org [" + org + "]");
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(buildIntegrationURI(url, org, "ia.deploy"));

        addAuthenticationHeader(post, platformUrl, username, password, org);

        MultipartEntity entity = new MultipartEntity();
        entity.addPart("file", new FileBody(file));
        post.setEntity(entity);

        try {
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                getLog().info("Integration is deployed successfully.");
            } else {
                throw new MojoFailureException("Failed to deploy integration adapter, the status code is [" +
                                               response.getStatusLine().getStatusCode() + "] and error message is [" +
                                               response.getStatusLine().getReasonPhrase() + "]");
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }
}
