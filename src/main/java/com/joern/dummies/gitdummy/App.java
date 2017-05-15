package com.joern.dummies.gitdummy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by jsattler on 27.04.2017.
 */
public class App {

    private static final Logger l = LoggerFactory.getLogger(App.class);

    public static void main(String... args){

        String user = args[0];
        String password = args[1];
        l.debug("gitUser="+user);
        l.debug("gitPassword not blank="+StringUtils.isNotBlank(password));
        // doesn't matter if url is https or ssh
        String remoteRepoUrl = PropertyReader.readProperty("git.remote.repoUrl.https");
        // String remoteRepoUrl = PropertyReader.readProperty("git.remote.repoUrl.ssh");
        String localRepoPath = PropertyReader.readProperty("git.local.repoPath");

        App app = new App();
        app.gitExample2(user, password, remoteRepoUrl, localRepoPath);
    }

    public void gitExample(String user, String password, String remoteRepoUrl, String localRepoPath){

        // GitUtil.clone(remoteRepoUrl, localRepoPath, "develop");

        Git git = GitUtil.getGit(localRepoPath);
        if(git != null){

            // GitUtil.pull(git);

            // GitUtil.commit(git);
            // GitUtil.push(git, user, password);

            /*
            String configFile = localRepoPath+"/src/main/resources/puppyprops/config.proprties";
            FileEdit.editFileContent(configFile, "develop", false);

            String newBranch = "javaBranch_"+TimeStamp.stamp(new Date(), "yyyyMMdd_HHmmss");
            GitUtil.branch(newBranch, git);
            */

            // GitUtil.checkout("develop", git);

            // GitUtil.merge("worker", git);

            // GitUtil.reset("5d18cc6b23e1bd14426b909c3357631f7dbc74c5", ResetCommand.ResetType.HARD, git);

        }
    }


    /*
        make concurrent changes in feature and develop
        merge feature into develop
        overriding develop with feature
     */
    public void gitExample1(String user, String password, String remoteRepoUrl, String localRepoPath) {


        Git git = GitUtil.getGit(localRepoPath);
        if (git != null) {

            String featureBranch = "myFeature";
            String developBranch = "develop";
            String configFile = localRepoPath+"/src/main/resources/puppyprops/config.properties";
            String devConfig = localRepoPath+"/src/main/resources/puppyprops/devConfig.properties";


            // make feature changes and push
            GitUtil.branch(featureBranch, git);
            GitUtil.checkout(featureBranch, git);
            FileEdit.editFileContent(configFile, featureBranch+" branch was here", true);
            GitUtil.stage(GitUtil.FILE_PATTERN_STAGE_ALL, git);
            GitUtil.commit(featureBranch+" changes", git);
            GitUtil.push(git, user, password);


            // make develop changes and push
            GitUtil.checkout(developBranch, git);
            FileEdit.editFileContent(configFile, developBranch+" branch was here", true);
            FileEdit.editFileContent(devConfig, developBranch+" branch was here", true);
            GitUtil.stage(GitUtil.FILE_PATTERN_STAGE_ALL, git);
            GitUtil.commit(developBranch+" changes", git);
            GitUtil.push(git, user, password);

            // merge feature into develop while feature is lead
            // and push
            GitUtil.mergeTheirs(featureBranch, null, git);
            GitUtil.push(git, user, password);

        }
    }


    public void gitExample2(String user, String password, String remoteRepoUrl, String localRepoPath) {


        Git git = GitUtil.getGit(localRepoPath);
        if (git != null) {

            String featureBranch = "myFeature_"+TimeStamp.stamp(new Date(), "sss");
            String developBranch = "develop";
            String relPathResources = "src/main/resources";
            String devConfig = localRepoPath+"/"+relPathResources+"/config/devConfig.properties";
            String puppyConfig = localRepoPath+"/"+relPathResources+"/config/puppyConfig.properties";
            String databaseConfig = localRepoPath+"/"+relPathResources+"/database/database.properties";

            String rollbackRef = null;
            try {
                rollbackRef = git.getRepository().findRef(developBranch).getObjectId().getName();
            } catch (IOException e) {
                l.error("Failed to set rollback point");
            }

            // make feature changes and commit
            GitUtil.branch(featureBranch, git);
            GitUtil.checkout(featureBranch, git);
            // FileEdit.editFileContent(devConfig, featureBranch+" branch was here", false);
            FileEdit.editFileContent(puppyConfig, featureBranch+" branch was here", false);
            // FileEdit.editFileContent(databaseConfig, featureBranch+" branch was here", false);
            //GitUtil.stage(relPathResources+"/config/puppyConfig.properties", git);
            GitUtil.stage(GitUtil.FILE_PATTERN_STAGE_ALL, git);
            GitUtil.commit(featureBranch+" changes", git);

            // make develop changes and commit
            GitUtil.checkout(developBranch, git);
            FileEdit.editFileContent(devConfig, developBranch+" branch was here", false);
            // FileEdit.editFileContent(puppyConfig, developBranch+" branch was here", false);
            FileEdit.editFileContent(databaseConfig, developBranch+" branch was here", false);
            GitUtil.stage(GitUtil.FILE_PATTERN_STAGE_ALL, git);
            GitUtil.commit(developBranch+" changes", git);
            // GitUtil.push(git, user, password);


            // merge feature into develop
            boolean mergeSuccess = GitUtil.merge(featureBranch, null, git);
            if(mergeSuccess){

                // and push
                GitUtil.push(git, user, password);
            }else{
                // reset develop
                if(StringUtils.isNotBlank(rollbackRef)){
                    GitUtil.reset(rollbackRef, ResetCommand.ResetType.HARD, git);
                }
            }
            GitUtil.deleteLocalBranch(featureBranch, true, git);
        }

    }

}