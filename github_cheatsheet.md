## Interact with GitHub using Terminal


##### create a clone of a github repo on your local machine
    git clone <ssh url looking like that: git@github.com:userexample/example.git>
##### show status of the local version of repository(main, branch)
    git status
##### stage new file from your local version, so it can be synchronized with the github version of the repository
    git add filename.py
##### prepare the added files to be synchronized with a message explaininng the work
    git commit -m "commit message"
##### upload the commited files to github
    git push
##### update the local version of the repository to the current state on github
    git pull
##### create a new branch in repository and change to this branch   
    git switch -c <branch_name>
##### change to a certain branch of repository
    git switch <branch_name>
##### synchonize the last version of a file from another branch while you are in the branch which is going to be changed
    git checkout branch-name -- <file-name>
##### reset my local files back to the last commit
    git reset --hard HEAD
##### show last commits of the branch you are currently in(hash of a certain commit is the cryptic string after "commit ") 
    git log
##### reset to certain hash(=certain old version of the repository)
    git reset --hard <your commit hash key>
##### discard any last added files or directories
    git clean -df
##### update a certain local file from someone elses github repository
    git fetch git@github
    git checkout FETCH_HEAD -- <file_name>
###### or to get the whole folder
    git checkout FETCH_HEAD -- <order_name/.>

##### check the origin repo of the local clone
    git remote -v
##### change the origin repo's url(in case you want to push local changes to another github repo)
    git remote set-url origin <new.git.url/here>

##### connect temporarily to another repo(called upstream) 
    git remote add upstream <git@github.com:neuefische/example.git> 

##### fetch data from that upstream
    git fetch upstream
##### create a new branch in repo
    git branch <branch_name>
##### show a list of all branches inside the upstream
    git branch -a

##### select and save the files from the upstream to your local machine. In the example we get the whole folder.
    git checkout <remotes/upstream/some_branch> /<folder_name>
##### delete an origin branch
    git branch -d <branch-name>
##### delete a remote branch
    git push --delete <remote name> <branch name>


&nbsp;

&nbsp;

## Workflow to keep updated github repositories of both DRIVER and NAVIGATOR working together

##### DRIVER:
    save the document you have been working on.
    git add <file_name>
    git commit -m "<your message>"
    git push
##### NAVIGATOR:
    git fetch <clone link of the DRIVERS repo>
    git checkout FETCH_HEAD <filename>
    git commit -m "<your message>"
    git push
    
&nbsp;

&nbsp;

## Setting up GitHub for a new Project


#### Steps needs to be done:
##### Create README.md
* contains description of the repo
* automatically displayed on the front page of the repo
##### add gitignore file
* for folders and files which should be excluded from being uploaded to github
* exclude data folder and credentials information like API keys, confidential data, etc.
##### add licence
* MIT licence
    
&nbsp;

&nbsp;

## Connect VS Code with github to work on your repository locally (works with Windows and Mac)


##### create an ssh key in windows terminal
    ssh-keygen
##### copy key to paste in github settings
##### 1. method: move into ssh folder
    cd .ssh
##### print ssh file to copy it (default name is id_rsa.pub)
    cat id_rsa.pub
##### 2. method: start Git(GUI) on windows and in "help/Show SSH key" open the key and copy
##### after copying the key go to github "Profile/Settings/SSH and GPG keys" --> "New SSH key"

