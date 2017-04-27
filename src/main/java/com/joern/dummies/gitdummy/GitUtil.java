package com.joern.dummies.gitdummy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
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
public class GitUtil {

    private static final Logger l = LoggerFactory.getLogger(GitUtil.class);

    public static void clone(String gitRepoUrl, String gitWorkDir, String gitBranch){

        try {
            CloneCommand clone = Git.cloneRepository()
                    .setURI( gitRepoUrl )
                    .setDirectory( new File(gitWorkDir) )
                    .setBranch(gitBranch);
            clone.call();

        } catch (GitAPIException e) {
            l.error("Failed to git clone", e);
        }
    }

    public static void pull(Repository localRepo){

        if(localRepo == null){
            l.error("Failed to git pull, missing local repo.");
            return;
        }

        Git git = new Git(localRepo);
        PullCommand pull = git.pull();
        try {
            pull.call();
        } catch (GitAPIException e) {
            l.error("Failed to git pull", e);
        }
    }

    public static void push(Repository localRepo, String gitUser, String gitPassword){

        if(localRepo == null){
            l.error("Failed to git push, missing local repo.");
            return;
        }

        Git git = new Git(localRepo);
        PushCommand push = git.push();
        if(StringUtils.isNotBlank(gitUser) && StringUtils.isNotBlank(gitPassword)){
            push.setCredentialsProvider( new UsernamePasswordCredentialsProvider( gitUser, gitPassword ) );
        }
        try {
            push.call();
        } catch (GitAPIException e) {
            l.error("Failed to git pull", e);
        }
    }


    public static void commit(Repository localRepo){

        if(localRepo == null){
            l.error("Failed to git commit, missing local repo.");
            return;
        }

        Git git = new Git(localRepo);
        String addAll = ".";

        try {
            git.add().addFilepattern(addAll).call();
        } catch (GitAPIException e) {
            l.error("Failed to git commit, can't add files to stage", e);
        }

        CommitCommand commit = git.commit();
        commit.setMessage("java commit at "+TimeStamp.stamp());
        try {
            commit.call();
        } catch (GitAPIException e) {
            l.error("Failed to git commit", e);
        }
    }

    public static Repository getLocalRepository(){

        String gitWorkDir = PropertyReader.readProperty("git.local.workDir");

        Repository localRepo = null;
        try {
            localRepo = new FileRepository(gitWorkDir + "/.git");
        } catch (IOException e) {
            l.error("Failed to access local repo", e);
        }
        return localRepo;
    }
}