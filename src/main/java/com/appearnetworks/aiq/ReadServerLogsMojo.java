package com.appearnetworks.aiq;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "server.logs.tail")
@Execute(phase = LifecyclePhase.INITIALIZE)
public class ReadServerLogsMojo extends AbstractReadLogsMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        execute("server.logs.tail");
    }
}
