package com.joern.dummies.gitdummy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
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

    public static final String FILE_PATTERN_STAGE_ALL = ".";

    public static boolean clone(String remoteRepoUrl, String localRepoPath, String branch){

        boolean success = true;

        try {
            CloneCommand clone = Git.cloneRepository()
                    .setURI( remoteRepoUrl )
                    .setDirectory( new File(localRepoPath) )
                    .setBranch(branch);
            clone.call();

        } catch (GitAPIException e) {
            l.error("Failed to git clone", e);
            success = false;
        }

        return success;
    }

    public static boolean pull(Git git){

        boolean success = true;

        PullCommand pull = git.pull();
        try {
            pull.call();
        } catch (GitAPIException e) {
            l.error("Failed to git pull", e);
            success = false;
        }
        return success;
    }

    public static boolean push(Git git, String user, String password){

        boolean success = true;

        PushCommand push = git.push();
        if(StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)){
            push.setCredentialsProvider( new UsernamePasswordCredentialsProvider( user, password ) );
        }
        try {
            push.call();
        } catch (GitAPIException e) {
            l.error("Failed to git pull", e);
            success = false;
        }
        return success;
    }


    public static boolean stage(String filePattern, Git git){

        boolean success = true;

        try {
            git.add().addFilepattern(filePattern).call();
        } catch (GitAPIException e) {
            l.error("Failed to git add, check filePattern="+filePattern, e);
            success = false;
        }
        return success;
    }

    public static void commit(String commitMessage, Git git){

        CommitCommand commit = git.commit().setMessage(commitMessage);
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

    public static boolean branch(String branchName, Git git){

        boolean success = true;

        try {
            git.branchCreate().setName(branchName).call();
        } catch (GitAPIException e) {
            l.error("Failed to create branch '"+branchName+"'", e);
            success = false;
        }
        return success;
    }

    public static boolean checkout(String branchName, Git git){

        boolean success = true;

        try {
            git.checkout().setName(branchName).call();
        } catch (GitAPIException e) {
            l.error("Failed to checkout branch '"+branchName+"'", e);
            success = false;
        }
        return success;
    }

    public static boolean mergeTheirs(String theirBranchName, String mergeMessage, Git git){

        boolean success = true;
        String currentBranch = null;

        try {

            Repository repository = git.getRepository();
            currentBranch = repository.getBranch();

            Ref ref = repository.findRef(theirBranchName);
            MergeCommand merge = git.merge().setStrategy(MergeStrategy.THEIRS).include(ref);
            if(StringUtils.isNotBlank(mergeMessage)){
                merge.setMessage(mergeMessage);
            }
            merge.call();

        } catch (IOException | GitAPIException e) {
            l.error("Failed to merge branch "+theirBranchName + " into " + currentBranch, e);
            success = false;
        }
        return success;
    }


    // TODO
    public static boolean deleteBranch(String branchName, Git git){

        l.error("not implemented: deleteBranch()");
        return false;
    }
}