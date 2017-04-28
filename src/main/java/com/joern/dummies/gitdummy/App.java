package com.joern.dummies.gitdummy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
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
        String remoteRepoUrl = PropertyReader.readProperty("git.remote.repoUrl");
        String localRepoPath = PropertyReader.readProperty("git.local.repoPath");

        new App().useGit(user, password, remoteRepoUrl, localRepoPath);
    }

    public void useGit(String user, String password, String remoteRepoUrl, String localRepoPath){

        // GitUtil.clone(remoteRepoUrl, localRepoPath, "develop");

        Git git = GitUtil.getGit(localRepoPath);
        if(git != null){

            // GitUtil.pull(git);

            GitUtil.commit(git);
            GitUtil.push(git, user, password);
        }
    }
}