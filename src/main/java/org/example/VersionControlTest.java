package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.example.vcplatforms.GitHub;
import org.example.vcplatforms.VCPlatform;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;

public class VersionControlTest {

    private final String repoName = "mend-version-control-test";
    private final String localPath = "./" + repoName;
    private VCPlatform vcPlatform;
    private Common common;

    private static boolean containsPathWithValue(JsonNode node, String value) {
        for (JsonNode child : node.path("tree")) {
            if (child.path("path").asText().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Parameters({"vc"})
    @BeforeClass
    public void setup(@Optional("GITHUB") VCs vc) throws Exception {
        System.out.println(vc);
        vcPlatform = vc.getVCInstance();
        vcPlatform.deleteRemoteRepo(repoName);
        vcPlatform.createRemoteRepo(repoName);
        common = new Common(vcPlatform, repoName, localPath);
        FileUtils.deleteDirectory(new File(localPath));
    }

    @Test
    public void cloneRepository() throws GitAPIException {
        Git.cloneRepository().setURI(vcPlatform.getRemoteRepoURL(repoName)).setDirectory(new File(localPath)).call();

        System.out.println("Repository cloned to " + localPath);
        File clonedDirectory = new File(localPath);
        assert clonedDirectory.exists() : "Cloned directory does not exist";
        assert clonedDirectory.list().length == 1 : "Cloned directory is not empty, its file length is " + clonedDirectory.list().length;
    }

    @Test(dependsOnMethods = {"cloneRepository"})
    public void pushToRepository() throws Exception {
        Git git = Git.init().setDirectory(new File(localPath)).call();
        String fileToPush = "fileToPush";
        common.commit(git, fileToPush, "msg");
        common.push(git);
        String trees = vcPlatform.getTrees(repoName, "master");
        assert hasFile(trees, fileToPush) : "File " + fileToPush + " is not in branch " + "master";
    }

    @Test(dependsOnMethods = {"cloneRepository"})

    public void pullFromRepository() throws Exception {
        Git git = Git.init().setDirectory(new File(localPath)).call();
        String pathname = "fileToPull.txt";
        vcPlatform.uploadFile(repoName, pathname);

        git.pull().setRemote("origin").setRemoteBranchName("main").call();
        String fullPath = localPath + File.separator + pathname;
        assert new File(fullPath).exists() : "File " + fullPath + " does not exist after pull";
    }

    @Test(dependsOnMethods = {"cloneRepository"})
    public void pushSeveralCommits() throws Exception {
        Git git = Git.init().setDirectory(new File(localPath)).call();
        common.commit(git, "f1", "msg1");
        common.commit(git, "f2", "msg2");
        common.push(git);
        String trees = vcPlatform.getTrees(repoName, "master");
        assert hasFile(trees, "f1") : "File f1 is not in branch master";
        assert hasFile(trees, "f2") : "File f2 is not in branch master";
    }

    private boolean hasFile(String trees, String fileName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(trees);
        return containsPathWithValue(rootNode, fileName);
    }
}

