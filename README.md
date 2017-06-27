# Atlas Project Templates

![build status](https://circleci.com/gh/sts-atlas/jake/tree/master.svg?style=shield&circle-token=41edafae9ba7a64b544cdbc352c5f3c9ff99f78d)

Project and service examples for getting your project into production with Atlas!

## What will happen
Following the instructions below will result in your new repository having full CI/CD into the Atlas cluster.  Changes checked into master (with successful tests run) will automatically be pushed into production.  Changes on branches will be built and tested, but not deployed.

## Prerequisites
* Install [Docker](https://www.docker.com/community-edition#download)
* Be in the sts-atlas org to access [releases](https://github.com/sts-atlas/template/releases/latest) from the jake project

## Repository Setup

1.  Create (and clone) the [repository](https://help.github.com/articles/creating-a-new-repository/) that you will host your project in
1.  Download the [latest release](https://github.com/sts-atlas/template/releases/latest)
1.  Unzip the contents of the release into your repository
    * cd into your new repository directory
    * Run the following command, replacing the path and version appropriately
        ```bash
        unzip /PATH/TO/YOUR/ZIP/jake-<VERSION>.zip && cp -r atlas-samples-<VERSION>/ . && rm -Rf ./atlas-samples-<VERSION>
        ```
    * You now have a directory in your repository (jake-0.4)
    * Move all files 
1.  Run the `bin/init` script in the project.
1.  Commit & Push
    ```bash
    git add .
    git commit -m "Adding files from sample project"
    git push --set-upstream origin master
    ```
1.  Log into [circleci.com](https://circleci.com) (or create an account with your github user)
1.  In Circle, go to settings, projects, then find your new repo.  Select the gear on the right and then the environment variables menu item on the left. Add [environment variables](https://circleci.com/docs/2.0/env-vars/) for:
    1.  JWT_SECRET `16D37EB64AC84CE3897BC68F6A269A5A`
    1.  DOCKER_USERNAME `stsatlas+atlasquayrobot`
    1.  DOCKER_PASSWORD `HD449TCAIUROKR4CXMVJP10XQ82FA4H6NBTCMXG6ND8JJWZH88CMWAGAH051VKOT`
    > The DOCKER_USERNAME & DOCKER_PASSWORD are the login for your image repository
1.  Still in project settings, go to overview and select "Follow Project" to kick off your first build!
1.  If your first build fails with "job build not found", make a nonsense commit (a change that has no effect, like a new line) and push it! (This is a known bug in Circle)
1. Once your Circle build passes, your app should be up in production, it can be accessed with \<service\>.\<org\>_\<project\>.swarm.commonstack.io

## Local Swarm Setup
* Run `bin/create-deployer`, which will init your swarm and start up a deployer
* Your swarm is now up!  Please refer to the local deployer api section of this readme to understand how to use a local deployer

## Local Deployer API

### With bin/atlas

* bin/atlas local-dockerize
  * This will build docker images locally for your services and put them in your local registry
* bin/atlas local-deploy
  * This will take your images from your local registry and push them into your local swarm
* bin/atlas local-poll
  * Use this to see the status of tasks running on your local swarm

### Using HTTP

* Deploy: POST /org/\<org\>/project/\<project\>
  * Along with the request you must send a file (using a multipart form).  One way to do this is with the -F flag on a curl, like so:
  ```bash
  curl -X POST --header "Authorization: Token $JWT" -F "file=@docker-compose.yml" -H "Host: deployer.sts-atlas_atlas-deploy.swarm.commonstack.io" http://localhost/org/<org>/project/<proejct>
  ```
  * This will deploy your services as defined in your docker compose file
* Poll: GET /org/\<org\>/project/\<project\>
  * This endpoint will return the status of the tasks running for your project (a task is a running service)
* Delete: DELETE /org/\<org\>/project/\<project\>
  * This endpoint will delete your stack

#### Proxy Service
Along with the deployer service, a proxy service is included in the deployer docker compose file.  The proxy allows us to forward requests such that requests to a subdomain actually get routed to a service.  So instead of using the random ports that docker assigns to its containers, you can use subdomains like my_service.org_project.swarm.commonstack.io.

#### Host Header
However, while developing locally all of the services run on localhost, so how do we connect to a specific service?  The answer is with a host header.  This host header tells the proxy service where to forward requests.  So to access a service called my_service, you make a request to localhost with a host header equal to what your production url would be:
```bash
curl -H 'Host:my_service.org_project.swarm.commonstack.io' localhost/metrics/healthcheck
```
Since the deployer is running on the swarm like any other service, you hit it with a host header like so:
```bash
curl -H 'Host:deployer.sts-atlas_atlas-deploy.swarm.commonstack.io' localhost/metrics/healthcheck
```

#### Authorization Header
Along with the host header, you just specify an Authorization header.  This is a JWT built with your specific org and project in the payload.  Instead of building this yourself, you should use the script in bin/jwtgen that we have already made for you.  For now we have a single jwt secret, which you must export as an environment variable.
```bash
export JWT_SECRET=16D37EB64AC84CE3897BC68F6A269A5A
```
