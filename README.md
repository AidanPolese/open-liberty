#OpenLiberty
[![Build Status](https://travis.ibm.com/was-liberty/open-liberty.svg?token=PsNAEgmnTFbhywLCP5JB&branch=integration)](https://travis.innovate.ibm.com/was-liberty/open-liberty)
[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)
[![Chat](https://img.shields.io/badge/chat-on%20slack-brightgreen.svg)](https://ibm-cloud.slack.com/messages/was-open-liberty/)

OpenLiberty is a highly composable, fast to start, dynamic application server runtime environment. **More of a description goes here**.

* [Quick Start](https://github.ibm.com/was-liberty/open-liberty#quick-start)
* [Learn concepts and commands](https://github.ibm.com/was-liberty/open-liberty#learn-concepts-and-commands)
* [License](https://github.ibm.com/was-liberty/open-liberty#license)
* [Issues](https://github.ibm.com/was-liberty/open-liberty#issues)
* [Git Large File Storage](https://github.ibm.com/was-liberty/open-liberty#git-large-file-storage)

##Quick Start
1. Install Git LFS.  See setup [Git Large File Storage](https://github.ibm.com/was-liberty/open-liberty#git-large-file-storage)
2. After forking the main Open Liberty repo on GitHub, you can then clone the main repo to your system:

    ```git clone git@github.ibm.com:open-liberty/WS-CD-Open.git```

    ```cd WS-CD-Open```

3. Run a [gradle build](https://github.ibm.com/was-liberty/open-liberty/wiki/Gradle-Build-Setup)
4. Run gradle command to execute automated tests.

## Learn concepts and commands

## License
Eclipse Public License - v 1.0 

Licenses under the **link to license goes here**.

## Issues

Report [issues](https://github.ibm.com/was-liberty/open-liberty/issues) on GitHub.

You can also join our [slack channel](https://ibm-cloud.slack.com/messages/was-open-liberty/) and chat with other developers.  

##Git Large File Storage
The Open Liberty git repository contains a number of large binaries that are too large to store directly in the git repository. The Open Liberty repository uses git Large File Storage (LFS) in order to store the different versions of the large files. Git LFS replaces large files with text pointers inside git, while storing the file contents on a remote server.

When cloning the git repository LFS will discover the text pointers and will download the contents of the large files when checking out a specific branch. In order to clone a repository with files contained in LFS the Git Large File Storage support must be installed. Once LFS is installed the repository can be cloned just like any other git repository.

If a clone was done before installing LFS then the working directory may contain a number of files representing the large files that have a small bit of text instead of the real content of the large file. This text acts as the pointer for LFS to find the remote content of the file on the LFS server. To recover from this without having to reclone the complete repository you can run the following command to discover and download the files stored in LFS:

```git lfs fetch```

Then run the following command to find all the LFS pointer files.

```git lfs ls-files```

Remove all the files listed by this command and then run the following command to get their real content:

```git checkout .```

Alternatively you could do a git checkout for each file listed individually.

In the very rare case that you need to add a new large file (please reconsider your options!) then you will need to install the LFS pre-push hook into your clone. Do that with the following command:

```git lfs install```

After that you will need to run 'git lfs track' command to track the paths of the new large files you are adding. This will update the .gitattributes. Comment the changes to .gitattributes along with the new large large files in a single commit. When you push the commits LFS will also push the large files to the LFS server.
