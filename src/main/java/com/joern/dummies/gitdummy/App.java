package com.joern.dummies.gitdummy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        // String remoteRepoUrl = PropertyReader.readProperty("git.remote.repoUrl.https");
        String remoteRepoUrl = PropertyReader.readProperty("git.remote.repoUrl.ssh");
        String localRepoPath = PropertyReader.readProperty("git.local.repoPath");

        App app = new App();
        app.useGit(user, password, remoteRepoUrl, localRepoPath);
    }

    public void useGit(String user, String password, String remoteRepoUrl, String localRepoPath){

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


            //-----------------------------------

            String featureBranch = "myFeature";
            String developBranch = "develop";
            String configFile = localRepoPath+"/src/main/resources/puppyprops/config.properties";



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
            GitUtil.stage(GitUtil.FILE_PATTERN_STAGE_ALL, git);
            GitUtil.commit(developBranch+" changes", git);
            GitUtil.push(git, user, password);

            // merge feature into develop while feature is lead
            // and push
            GitUtil.mergeTheirs(featureBranch, null, git);
            GitUtil.push(git, user, password);


        }
    }

}