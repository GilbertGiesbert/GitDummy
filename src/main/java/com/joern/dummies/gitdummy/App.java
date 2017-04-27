package com.joern.dummies.gitdummy;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by jsattler on 27.04.2017.
 */
public class App {

    private static final Logger l = LoggerFactory.getLogger(App.class);

    public static void main(String... args ){

        new App().gitSome();
    }

    public void gitSome(){

        String checkoutPath = "C:\\dev\\tmp\\gitdummypuppy";


        try {
            Git git = Git.cloneRepository()
                    .setURI( "https://github.com/GilbertGiesbert/GitDummyPuppy.git" )
                    .setDirectory( new File(checkoutPath) )
                    .call();

        } catch (GitAPIException e) {
            l.error("Failed to git clone", e);
        }
    }
}