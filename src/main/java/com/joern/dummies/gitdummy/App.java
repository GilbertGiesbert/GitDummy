package com.joern.dummies.gitdummy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jsattler on 27.04.2017.
 */
public class App {

    private static final Logger l = LoggerFactory.getLogger(App.class);

    public static void main(String... args){

        String gitUser = args[0];
        String gitPswd = args[1];

        new App().useGit(gitUser, gitPswd);
    }

    public void useGit(String gitUser, String gitPswd){

        String gitRepoUrl = PropertyReader.readProperty("git.remote.repoUrl");
        String gitWorkDir = PropertyReader.readProperty("git.local.workDir");
        String gitBranch = PropertyReader.readProperty("git.branch");

        if(StringUtils.isBlank(gitRepoUrl) || StringUtils.isBlank(gitWorkDir) || StringUtils.isBlank(gitBranch)){
            l.error("Failed to use git, missing some config");
            return;
        }

        // GitUtil.clone(gitRepoUrl, gitWorkDir, gitBranch);

        Repository localRepo = GitUtil.getLocalRepository();
        if(localRepo != null){

            // GitUtil.pull(localRepo);

            GitUtil.commit(localRepo);
            GitUtil.push(localRepo, gitUser, gitPswd);
        }
    }
}