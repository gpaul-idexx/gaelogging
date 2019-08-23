A project to figure out if we can get the logs from a pull subscriber running on GAE Std available
without having to issue a request to the service.

deploy with `./gradlew appengineDeploy -PgoogleProjectId <your projectid>`

Expects that you have a subscriber called: `test1-pull` in the project. 

Publish a message to a topic and the project's subscriber will log it. However, you'll have to go to `/` in order to 
get a log message to show up in stack driver logging. 