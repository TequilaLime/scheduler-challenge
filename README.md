# Backend coding challenge

Congratulations!

Your application for the backend position piqued our interest.
You can code – we trust you on that one.
However, we’d like to see your style!
We want you to build a simplified microservice in SpringBoot
which is able to manage online meetings.

Technologies used are Java, Postgres, Docker, Junit 5 and Testcontainers.

## Endpoints

It has 4 endpoints:
- /users
- /slots
- /meetings
- /calendars

All the endpoints are described in the Open API 3 specification file `api-docs.yaml`.
You can also access Swagger UI html page by using the url: http://localhost:8080/swagger-ui.html.
Please note that project has to be runnable in order to start it and access this page.

The purpose of the service is to manage the meeting scheduling.
We can create Users, Slots and Meetings.
User is defined with name.
Slot is defined with startAt and endAt timestamps.
Meeting is defined with title, slotId, and participants.

Users are the clients of the platform.
Slots represent a time span that can be selected to book a meeting.
Why not booking a meeting directly?
Well, we want to decide when meeting can be booked.
Meetings represent occupied slots.
So once slot is selected, it becomes a meeting.  

These 3 entities are simple one and has basic CRD operations (no update).
For example:\
POST /users {"name":"User Name"} will create a user.\
GET /users/<UUID> will read specified user.\
Delete /users/<UUID> will delete specified user.\
Similar applies to all others resources, except Calendar.

Calendar is a bit of specific entity.
It contains all the slots and meeting data for the specified month as well as the count of slots and meetings before and after the selected month.
For example:\
GET /calendars/month=2021-03 will return the data for March 2021.

To understand better how API works, take a look at test classes.
There are multiple examples for every scenario.
Additionally, DTO classes are provided in advance.

## Running the project
In order to run a project, database must be present.
You can make it by running it in a provided docker-compose file
```
docker-compose up
```
Stop it with counter command:
```
docker-compose down
```
Database schema will be automatically initialized.

Please note that you don't have to run the docker-compose command while executing tests.
Testcontainers are being used for such a purpose.

## How to provide the task result
First, please create private GitHub repo and push our code as initial commit.
Then you can push as many commits as you need to finish your challenge.
In the end, send us the link of your repo and share it with account(s) specified in the email by your interviewer.

To finish the challenge, you can take up to 4 hours in the following 7 days.
We trust you on the fair approach.

## Task
The starting state of the project is the project with missing parts.
Basically, following entities are missing: DB models, repositories, and services.
Your task is to implement missing classes.

There are two types of tests - contract tests and performance test.
The first one is about making sure the requests and responses are as expected.
The second one is about checking the performance of the solution.
Please don't consider it as a required part of a challenge, it is only a tool to compare different implementations.
But do have in mind that **the most performant solution** is needed.