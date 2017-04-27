package com.joern.dummies.gitdummy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by jsattler on 27.04.2017.
 */
public class App {

    private static final Logger l = LoggerFactory.getLogger(App.class);

    public static void main(String... args ){

        new App().gitSome(args);
    }

    public void gitSome(String... args){

        String gitUser = args[0];
        String gitPswd = args[1];
        String gitRepoUrl = PropertyReader.readProperty("git.remote.repoUrl");
        String gitWorkDir = PropertyReader.readProperty("git.local.workDir");
        String gitBranch = PropertyReader.readProperty("git.branch");

        if(StringUtils.isBlank(gitRepoUrl) || StringUtils.isBlank(gitWorkDir) || StringUtils.isBlank(gitBranch)){
            l.error("Failed to start git client due to invalid config");
            return;
        }

        try {
            CloneCommand cc = Git.cloneRepository()
                    .setURI( gitRepoUrl )
                    .setDirectory( new File(gitWorkDir) )
                    .setBranch(gitBranch);

            if(StringUtils.isNotBlank(gitUser) && StringUtils.isNotBlank(gitPswd)){
                cc.setCredentialsProvider( new UsernamePasswordCredentialsProvider( gitUser, gitPswd ) );
            }
            Git git =  cc.call();

        } catch (GitAPIException e) {
            l.error("Failed to git clone", e);
        }
    }
}