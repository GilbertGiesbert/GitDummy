package com.joern.dummies.gitdummy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by jsattler on 27.04.2017.
 */
public class App {

    private static final Logger l = LoggerFactory.getLogger(App.class);

    public static void main(String... args ){

        new App().gitPull();
    }

    public void gitClone(String... args){

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

    public void gitPull(){

        String gitWorkDir = PropertyReader.readProperty("git.local.workDir");

        Repository localRepo = null;
        try {
            localRepo = new FileRepository(gitWorkDir + "/.git");
        } catch (IOException e) {
            l.error("Failed to init local repo", e);
            return;
        }

        Git git = new Git(localRepo);
        PullCommand pc = git.pull();
        try {
            pc.call();
        } catch (GitAPIException e) {
            l.error("Failed to git pull", e);
        }
    }
}