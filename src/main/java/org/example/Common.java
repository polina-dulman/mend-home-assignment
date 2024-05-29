package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.example.vcplatforms.VCPlatform;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;

public class Common {

    private final VCPlatform vcPlatform;
    private final String repoName;
    private final String localPath;
    public Common(VCPlatform vcPlatform, String repoName, String localPath) {
        this.vcPlatform = vcPlatform;
        this.repoName = repoName;
        this.localPath = localPath;
    }

    public void push(Git git) throws GitAPIException, URISyntaxException {
        git.remoteAdd()
                .setName("origin")
                .setUri(new URIish(vcPlatform.getRemoteRepoURL(repoName)))
                .call();
        git.push()
                .setPushAll()
                .setCredentialsProvider(vcPlatform.getCredentialsProvider())
                .call();
    }

    public void commit(Git git, String fileName, String msg) throws IOException, GitAPIException {
        new File(localPath + File.separator + fileName).createNewFile();
        git.add().addFilepattern(".").call();
        git.commit().setMessage(msg).call();
    }

    public String getContent(String responseJsonString) throws JsonProcessingException {
        System.out.println(responseJsonString);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseJsonString);
        System.out.println(node.path("content").asText());
        return new String(Base64.getDecoder().decode(node.path("content").asText().replace("\n", "")));
    }
}

