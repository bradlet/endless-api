# endless-api
Play project to practice deploying to AWS architecture.

### Running the containerized app
1. Run `gradle jib` to build and push the container image to ECR (uses ecr login helper).
2. Run `docker pull <repository name>` and checkout the image name.
3. Run `docker run -p 8080:8080 <image id>` to start the container (optionally add `-d` to run it detached).