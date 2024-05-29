# Overview

These are sanity tests for some of the basic GitHub features. The tests are divided into two topics: `VersionControlTest` and `BranchesTest`. These two topics can be run in parallel, as defined in the `testng.xml`.

Inside each class, there are dependent tests to make the run faster (i.e., not cloning the repository for every test case).

## Usage

1. Create a personal access token in the GitHub UI and paste it in the `GitHub.java` class under `personalAccessToken`. Also, enter your GitHub username in the `userName` field.
2. Run the tests from `testng.xml`.
3. If you want to test Gitlab or Azure devops as well, you should add them at `VCs.java` and add the appropriate parameters to the `testng.xml`