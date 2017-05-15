package com.joern.dummies.gitdummy;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.Merger;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    /*
        Note: don't know how to implement
     */
    static boolean pullAndMerge(Git git){

        Merger ourMerger = MergeStrategy.OURS.newMerger(git.getRepository());
        Merger theirMerger = MergeStrategy.THEIRS.newMerger(git.getRepository());

        boolean success = true;

        FetchCommand fetch = git.fetch();

        // PullCommand pull = git.pull();
        // PullCommand pull = git.pull().setRebase(BranchConfig.BranchRebaseMode.NONE).setStrategy(MergeStrategy.RECURSIVE);
        // PullCommand pull = git.pull().setStrategy(MergeStrategy.RESOLVE);

        try {

            FetchResult fetchResult = fetch.call();
            Ref ref = fetchResult.getAdvertisedRef("develop");
            MergeCommand merge = git.merge().include(ref);

            MergeResult mergeResult = merge.call();
            Map<String, int[][]>  conflictsMap = mergeResult.getConflicts();

            for(String s: conflictsMap.keySet()){

                int[][] bla = conflictsMap.get(s);

            }


            // PullResult pullResult = pull.call();
            // MergeResult mergeResult = pullResult.getMergeResult();










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
            MergeResult mergeResult = merge.call();

            success = mergeResult.getConflicts() == null;

        } catch (IOException | GitAPIException e) {
            l.error("Failed to merge branch "+theirBranchName + " into " + currentBranch, e);
            success = false;
        }
        return success;
    }

    public static boolean merge(String theirBranchName, String mergeMessage, Git git){

        boolean success = true;
        String currentBranch = null;

        try {

            Repository repository = git.getRepository();
            currentBranch = repository.getBranch();

            Ref ref = repository.findRef(theirBranchName);
            MergeCommand merge = git.merge().include(ref);
            if(StringUtils.isNotBlank(mergeMessage)){
                merge.setMessage(mergeMessage);
            }


            MergeResult mergeResult = merge.call();
            Map<String, int[][]> conflictsMap = mergeResult.getConflicts();

            success = conflictsMap == null;

        } catch (IOException | GitAPIException e) {
            l.error("Failed to merge branch "+theirBranchName + " into " + currentBranch, e);
            success = false;
        }
        return success;
    }


    // TODO: test
    public static boolean deleteLocalBranch(String branchName, boolean forceDelete, Git git){

        boolean success = true;

        try {
            git.branchDelete().setForce(forceDelete).setBranchNames("refs/heads/"+branchName).call();
        } catch (GitAPIException e) {
            l.error("Failed to delete local branch "+branchName, e);
            success = false;
        }

        return success;
    }

    // TODO: test
    public static boolean deleteRemoteBranch(String branchName, Git git){

        boolean success = true;
        String remoteServerName = "origin";

        try {
            RefSpec refSpec = new RefSpec()
                    .setSource(null)
                    .setDestination("refs/heads/"+branchName);
            git.push().setRefSpecs(refSpec).setRemote(remoteServerName).call();
        } catch (GitAPIException e) {
            l.error("Failed to delete remote branch "+branchName+" on remote server "+remoteServerName, e);
            success = false;
        }
        return success;
    }

    public static boolean reset(String ref, ResetCommand.ResetType resetType, Git git){
        boolean success = true;
        try {
            git.reset().setMode( ResetCommand.ResetType.HARD ).setRef( ref ).call();
        } catch (GitAPIException e) {
            l.error("Failed to reset ref "+ref, e);
            success = false;
        }
        return success;
    }
}