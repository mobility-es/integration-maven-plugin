package com.appearnetworks.aiq;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "server.logs")
@Execute(phase = LifecyclePhase.INITIALIZE)
public class FetchServerLogsMojo extends AbstractFetchLogsMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        execute("server.logs");
    }
}
