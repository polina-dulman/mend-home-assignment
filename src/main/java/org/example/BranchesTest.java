package org.example;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.example.vcplatforms.GitHub;
import org.example.vcplatforms.VCPlatform;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class BranchesTest {

    private final String repoName = "mend-branches-test";
    private final String localPath = "./" + repoName;
    private VCPlatform vcPlatform;
    private Common common;

    @Parameters({"vc"})
    @BeforeClass
    public void setup(@Optional("GITHUB") VCs vc) throws Exception {
        System.out.println(vc);
        vcPlatform = vc.getVCInstance();
        vcPlatform.deleteRemoteRepo(repoName);
        vcPlatform.createRemoteRepo(repoName);

        FileUtils.deleteDirectory(new File(localPath));
        Git.cloneRepository().setURI(vcPlatform.getRemoteRepoURL(repoName)).setDirectory(new File(localPath)).setCloneAllBranches(true).call();
        common = new Common(vcPlatform, repoName, localPath);
        System.out.println("Repository cloned to " + localPath);
    }

    @Test
    public void pushToDifferentBranches() throws Exception {
        Git git = Git.init().setDirectory(new File(localPath)).call();

        String f1 = "f1";
        String f1Content = "f1 content";
        Files.write(Path.of(localPath + File.separator + f1), f1Content.getBytes());
        common.commit(git, f1, "msg1");
        git.branchRename().setNewName("b1").call();
        common.push(git);

        git.branchCreate().setName("b2").call();
        git.checkout().setName("b2").call();
        String f2 = "f2";
        String f2Content = "f2 content";
        Files.write(Path.of(localPath + File.separator + f2), f2Content.getBytes());
        common.commit(git, f2, "msg2");
        common.push(git);

        String remoteContentF1 = common.getContent(vcPlatform.getFile(repoName, f1, "b1"));
        assert f1Content.equals(remoteContentF1);

        String remoteContentF2 = common.getContent(vcPlatform.getFile(repoName, f2, "b2"));
        assert f2Content.equals(remoteContentF2);
    }

    @Test
    public void pushSameFileWithDifferentContentToDifferentBranches() throws Exception {
        Git git = Git.init().setDirectory(new File(localPath)).call();

        String f1 = "f1";
        String f1Content = "f1 content";
        Path path = Path.of(localPath + File.separator + f1);
        Files.write(path, f1Content.getBytes());
        common.commit(git, f1, "msg1");
        git.branchRename().setNewName("b-1").call();
        common.push(git);

        git.branchCreate().setName("b-2").call();
        git.checkout().setName("b-2").call();
        String otherContent = "other content";
        Files.write(path, otherContent.getBytes());
        common.commit(git, f1, "msg2");
        common.push(git);

        String remoteContentF1 = common.getContent(vcPlatform.getFile(repoName, f1, "b-1"));
        assert f1Content.equals(remoteContentF1);

        String remoteContentF1B2 = common.getContent(vcPlatform.getFile(repoName, f1, "b-2"));
        assert otherContent.equals(remoteContentF1B2);
    }


}

