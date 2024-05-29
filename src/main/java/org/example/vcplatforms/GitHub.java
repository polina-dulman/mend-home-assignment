package org.example.vcplatforms;

import com.squareup.okhttp.*;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.util.List;

public class GitHub extends VCPlatform {
    private final String remoteRepoURL;
    private final String userName = "";
    private final String personalAccessToken = "";
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final String apiBaseUrl = "https://api.github.com";

    public GitHub() {
        remoteRepoURL = "https://github.com/" + userName;
    }

    @Override
    public void createRemoteRepo(String repoName) throws Exception {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"name\": \"" + repoName + "\"}");

        String url = apiBaseUrl + "/user/repos";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "token " + personalAccessToken)
                .build();

        callApi(request, List.of());

    }    @Override
    public void deleteRemoteRepo(String repoName) throws Exception {
        String url = apiBaseUrl + "/repos/" + userName + "/" + repoName;
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", "token " + personalAccessToken)
                .build();
        callApi(request, List.of(404)); //ignore if already deleted

    }

    private Response callApi(Request request, List<Integer> successfulResponseCodes) throws Exception {
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful() || successfulResponseCodes.contains(response.code())) {
            System.out.println(request.toString() + " successfully executed");
        } else {
            throw new Exception("Following request failed :\n" + request.toString() + "\nResponse code: " + response.code());
        }
        return response;
    }

    @Override
    public CredentialsProvider getCredentialsProvider() {
        return new UsernamePasswordCredentialsProvider("${token}", personalAccessToken);
    }

    @Override
    public String getRemoteRepoURL(String repoName) {
        return remoteRepoURL + "/" + repoName;
    }

    @Override
    public String getTrees(String repoName, String branch) throws Exception {
        String url = apiBaseUrl + "/repos/" + userName + "/" + repoName + "/git/trees/" + branch + "?recursive=1";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "token " + personalAccessToken)
                .build();
        Response response = callApi(request, List.of());
        return response.body().string();
    }
    @Override
    public void uploadFile(String repoName, String fileName) throws Exception {
        String url = apiBaseUrl + "/repos/" + userName + "/" + repoName + "/contents/" + fileName;
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"message\": \"my commit message\", \"content\": \"bXkgbmV3IGZpbGUgY29udGVudHM=\"}");

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "token " + personalAccessToken)
                .build();
        callApi(request, List.of());
    }
    @Override
    public String getFile(String repoName, String fileName, String branchName) throws Exception {
        String url = apiBaseUrl + "/repos/" + userName + "/" + repoName + "/contents/" + fileName + "?ref=" + branchName;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "token " + personalAccessToken)
                .build();
        Response response = callApi(request, List.of());
        return response.body().string();
    }
}
