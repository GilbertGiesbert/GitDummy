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

    public static void clone(String remoteRepoUrl, String localRepoPath, String branch){

        try {
            CloneCommand clone = Git.cloneRepository()
                    .setURI( remoteRepoUrl )
                    .setDirectory( new File(localRepoPath) )
                    .setBranch(branch);
            clone.call();

        } catch (GitAPIException e) {
            l.error("Failed to git clone", e);
        }
    }

    public static void pull(Git git){

        PullCommand pull = git.pull();
        try {
            pull.call();
        } catch (GitAPIException e) {
            l.error("Failed to git pull", e);
        }
    }

    public static void push(Git git, String gitUser, String gitPassword){

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


    public static void commit(Git git){

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

    public static Repository getLocalRepository(String pathToLocalRepo){

        try {
            return new FileRepository(pathToLocalRepo + "/.git");
        } catch (IOException e) {
            l.error("Failed to access local repo, check path="+pathToLocalRepo, e);
            return null;
        }
    }

    public static Git getGit(String pathToLocalRepo){

        Repository localRepo = getLocalRepository(pathToLocalRepo);

        if(localRepo == null){
            l.error("Failed to get git , missing local repo.");
            return null;
        }
        return new Git(localRepo);
    }
}