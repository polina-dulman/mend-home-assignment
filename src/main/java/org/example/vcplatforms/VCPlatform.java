package org.example.vcplatforms;

import org.eclipse.jgit.transport.CredentialsProvider;

public abstract class VCPlatform {

    public abstract void createRemoteRepo(String repoName) throws Exception;
    public abstract void deleteRemoteRepo(String repoName) throws Exception;
    public abstract CredentialsProvider getCredentialsProvider();

    public abstract String getRemoteRepoURL(String repoName);
    public abstract String getTrees(String repoName, String branch) throws Exception;
    public abstract void uploadFile(String repoName, String fileName) throws Exception;
    public abstract String getFile(String repoName, String fileName, String branchName) throws Exception;

    }
