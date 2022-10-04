# Microservice

This project contains source code and supporting files for a serverless application that you can deploy with the SAM CLI. It includes the following files and folders.
TEST README FILE.
- ExtractTransformFunction/src/main - Code for the application's Lambda function.
- events - Airport name and procedure that you can use to invoke the function.
- ExtractTransformFunction/src/test - Unit tests for the application code. 
- template.yaml - A template that defines the application's AWS resources.

Problem statement: SIDs and STARs are procedures which are done in an airport. SIDs and STARs are a stream of waypoints. We want to know, for the airport named WSSS, which are the two waypoints that appear more often for the SIDs. Also for the STARs.
To achieve the objective, create a backend that interrogates the Open ATMS API below connected to a frontend that allows us to select the airport and SIDs or STARs and presents the information.

1. Do the code for the backend, which will talk to the Open ATMS API and find top waypoints for SIDs and STARs, in Java. (Other options are also possible after discussion, please get a confirmation) 
Ans: 
Top waypoints for SIDs and STARs. SID is for departure, STAR is for arrival.
Standard instrument departure (SID) routes – name and version, also known as departure procedures (DP), are published flight procedures followed by aircraft on an IFR flight plan immediately after takeoff from an airport. It is for pilot-nav or radar-nav.
A STAR is an air traffic control (ATC)-coded IFR arrival route established for application to arriving IFR aircraft destined for certain airports. Area navigation (RNAV) STAR/FMSP procedures for arrivals serve the same purpose but are used only by aircraft equipped with flight management systems (FMS) or GPS. The purpose of both is to simplify clearance delivery procedures and facilitate transition between en route and instrument approach procedures. 
The ICAO airport code or location indicator is a fourletter code designating aerodromes around the world. ICAO codes are also used to identify other aviation facilities such as weather stations, international flight service stations or area control centers, whether or not they are located at airports. Flight information regions are also identified by a unique ICAO-code. The IATA code for London's Heathrow Airport is LHR and its ICAO code is EGLL.

2. To build the binary/ bytecode artifact you will use an automated tool, (e.g. Maven or Gradle for Java). 
Ans: Gradle for AWS Lambda Function Serverless Development as it is default. It is just a preference as gradle is incremental and faster. SAM build uses gradle build by default.

3. Put the backend into a container (Docker or equivalent) and send the container to a repo, then make it run in the cloud. (Azure, Google, AWS, use azure container instances, GCP cloud run, ECS or similar option, K8s a plus!) We expect to see an end-to-end CI/CD process: start in a source code repository (GitHub or similar) and do the proper actions / scripts to compile, containerise, send to a container repository and run in cloud. (use GitHub actions or similar)
Ans: VSCode -> Github (SAM Serverless, GitLab is not an option) – Docker (Containerization Tech) – CodePipeline (CICD Orchestration) – CodeBuild (buildspec.yml and SAM build with docker) – ECR (repo) – Lambda (CloudFormation Stack with API Gateway) .
Show CodePipeline

4. The build environment shall be Linux (not Windows)
Ans: Linux
-PRE_BUILD checks the resources required (e.g. awscli, docker, pip)
-BUILD creates docker image 238815075413.dkr.ecr.ap-southeast-1.amazonaws.com/open_atm:$CODE_BUILD through “sam build”
-POST_BUILD pushes new docker image to ECR
-POST_BUILD updates lambda function for framework tester to access via api gateway.

5. There will be a front-end, connecting to the back end, that will select an airport, option of SID or STAR, and return the top two waypoints for each case (no graphical design is required, using a framework is recommended) – SAM Build Command Line, local invoke or AWS Lambda Event is the front end.
You only have half an hour to present your work and demonstrate that it works:
We suggest that in the repository you create a README file with a clear diagram of the components of your solution but also with the steps that are required to build it. (No need for a Power Point.) You can use the README to assist you on your presentation. You are not expected to code live or to memorise the commands you are going to use.
Ans: SAM Build Command Line, local invoke or AWS Lambda Event is the front end.

6. From the half an hour, we recommend you spend some minutes to walk us through the code you have written. Suggest explaining its structure and the key concepts used. We also would like you to show us the code that supports the building, test, and deployment.
Ans:
8. Finally,  demonstrate the interaction with front-end of the solution. Select an airport, SID vs STAR, and show the waypoints as requested in the functionality section. Show us that it works!
Ans: Show AWS Lambda which has an api gateway, iac and serverless function.
9. You only have half an hour to present your work and demonstrate that it works:
We suggest that in the repository you create a README file with a clear diagram of the components of your solution but also with the steps that are required to build it. (No need for a Power Point.) You can use the README to assist you on your presentation. You are not expected to code live or to memorise the commands you are going to use.
Ans: README contains a scale down instructions similar to this document.

10. From the half an hour, we recommend you spend some minutes to walk us through the code you have written. Suggest explaining its structure and the key concepts used. We also would like you to show us the code that supports the building, test, and deployment.
Ans:
Show us the code that supports building – gradle and sam build → gradle build. Show us the code that supports test – APIGatewayProxyResponseEvent handleRequest. Show us the code that supports deployment – CICD

11. Finally,  demonstrate the interaction with front-end of the solution. Select an airport, SID vs STAR, and show the waypoints as requested in the functionality section. Show us that it works!
Ans: 
Use an event file that simulate to input 1 airport and SID procedure and 1 airport and STAR procedure.

12. Your feedback about the tech challenge and what you have learnt from it (please do 	at least one element which is new to you, and explain it to us)
Ans:

Handle Typechecking Scenarios in Java using GSON to JSON. I’ll need to find a open source piece of code to integrate typechecking (scenario 1 and 2) as detect api response change should be part of the MVP. This is more important than the features 1 - 4: 

Kafka (publish-subscribe to MQ), Backend database (MySQL – explain about identifying the data that changes all the time and data that do not change at all). But MySQL performance may not meet the requirement of fast changing data. That needs to be discussed with the project team. Improving UI/UX (does not impact MVP), Slack notification system (does not impact MVP).

Scenario 1: The data from API changes. There is a need to handle if the API structure has more elements than the POJO to detect api response changes (e.g. throwing an exception if the json element is required or set the new json element as optional). If the API has less elements than the POJO, the typechecker should understand which is the required fields and optional fields to flag the errors accordingly.
Scenario 2: The data from API is the same as the POJO.

13. How would you work differently if this was not a test but a two week sprint 
Ans: 

Find out from project manager on the triple constraint baseline for the sprints and more about the iteration planning on two week sprint.
Use an existing process is to review the prioritized user stories with the customers. 
Use an existing process to includes estimate the effort of the two week sprint.
Use an existing process to list the initial Product backlog.
Go through Sprint Planning - Sprint Retrospective - Sprint Review.

I would choose to prioritize requirements such as state tree debugging/replay, shifting workloads, typechecking improvement, swagger, logging, monitoring, api gateway configuration, cloud services (IAM, cloud stack), automated tests structure, CIAT, CICD to ECS or EKS) without two week sprints. I would build these requirements for production quality before accelerating to two week sprints to implement features like kafka, backend database, improving uiux, notification system.

14. Your own suggestions on how to improve the code for production quality (how to increase maturity)
Ans: 

I would suggest to build kubernetes containers instead of docker to migrate workload using some software (tanzu kubernetes) from cloud to on-prem or cloud to other clouds and vice-versa for finer control, reliability, performance and cost.
Use logging (log4j, splunk, sam logging, cloudwatch), alert (splunk, cloudwatch for cost usage), monitoring (cloudwatch...), healthcheck (enterprise job monitoring , ECS or EKS healthcheck), dashboard (splunk, cloudwatch).

The team can continue to build the java typechecker to replay (state tree debugging capability) the API GET/ or POST/ for the last replay as well as storing the state tree (json inputs and other required elements) to replay any test results locally without relying on OpenATMs api to regenerate the error. The flight clearance request is like a booking request. If a clearance request failed or success. The state tree can be saved to replay the clearance.

The team can use Swagger to describe the structure of APIs so that machines can read them. After describing the APIs. Typechecking Input -> Swagger can be used to regenerate the POJOs (I used an online generator). This is the advantage of using Swagger as it allows accurate POJO generation and OpenAPI specification and api documentation at the same time.

API key should not be stored anywhere. Use HMAC Authentication is common for securing public APIs. OAuth on the other hand is useful when you need to restrict parts of your API to authenticated users only. Check how the cloud provider secure the keys when transmitting.

Block DDOS by using rate limiting on the api gateway. Check how the cloud provider throttle or rate limit the invokation from the front end and mitigate ddos.

The team can use Swagger to describe the structure of APIs so that machines can read them. After describing the APIs. Typechecking Input -> Swagger can be used to regenerate the POJOs (I used an online generator). This is the advantage of using Swagger as it allows accurate POJO generation and OpenAPI specification and api documentation at the same time.

API key should not be stored anywhere. Use HMAC Authentication is common for securing public APIs. OAuth on the other hand is useful when you need to restrict parts of your API to authenticated users only. Check how the cloud provider secure the keys when transmitting.

Block DDOS by using rate limiting on the api gateway. Check how the cloud provider throttle or rate limit the invokation from the front end and mitigate ddos.


* [CLion](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [GoLand](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [IntelliJ](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [WebStorm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [Rider](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [PhpStorm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [PyCharm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [RubyMine](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [DataGrip](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [VS Code](https://docs.aws.amazon.com/toolkit-for-vscode/latest/userguide/welcome.html)
* [Visual Studio](https://docs.aws.amazon.com/toolkit-for-visual-studio/latest/user-guide/welcome.html)

## Deploy the airlab serverless application

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. It uses Docker to run your functions in an Amazon Linux environment that matches Lambda. It can also emulate your application's build environment and API.

To use the SAM CLI, you need the following tools.

* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* Java11 - [Install the Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* Docker - [Install Docker community edition](https://hub.docker.com/search/?type=edition&offering=community)

To build and deploy your application for the first time, run the following in your shell:

```bash
sam build
sam deploy --guided
```

The first command will build the source of your application. The second command will package and deploy your application to AWS, with a series of prompts:

* **Stack Name**: The name of the stack to deploy to CloudFormation. This should be unique to your account and region, and a good starting point would be something matching your project name.
* **AWS Region**: The AWS region you want to deploy your app to.
* **Confirm changes before deploy**: If set to yes, any change sets will be shown to you before execution for manual review. If set to no, the AWS SAM CLI will automatically deploy application changes.
* **Allow SAM CLI IAM role creation**: Many AWS SAM templates, including this example, create AWS IAM roles required for the AWS Lambda function(s) included to access AWS services. By default, these are scoped down to minimum required permissions. To deploy an AWS CloudFormation stack which creates or modifies IAM roles, the `CAPABILITY_IAM` value for `capabilities` must be provided. If permission isn't provided through this prompt, to deploy this example you must explicitly pass `--capabilities CAPABILITY_IAM` to the `sam deploy` command.
* **Save arguments to samconfig.toml**: If set to yes, your choices will be saved to a configuration file inside the project, so that in the future you can just re-run `sam deploy` without parameters to deploy changes to your application.

You can find your API Gateway Endpoint URL in the output values displayed after deployment.

## Use the SAM CLI to build and test locally

Build your application with the `sam build` command.

```bash
Microservice$ sam build
```

The SAM CLI installs dependencies defined in `ExtractTransformFunction/build.gradle`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.

```bash
Microservice$ sam local invoke ExtractTransformFunction --event events/simulate_sid_event.json
Microservice$ sam local invoke ExtractTransformFunction --event events/simulate_star_event.json

```

The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.

```bash
Microservice$ sam local start-api
Microservice$ curl http://localhost:3000/
```

The SAM CLI reads the application template to determine the API's routes and the functions that they invoke. The `Events` property on each function's definition includes the route and method for each path.

```yaml
      Events:
        ExtractTransform:
          Type: Api
          Properties:
            Path: /hello
            Method: get
```

## Add a resource to your application
The application template uses AWS Serverless Application Model (AWS SAM) to define application resources. AWS SAM is an extension of AWS CloudFormation with a simpler syntax for configuring common serverless application resources such as functions, triggers, and APIs. For resources not included in [the SAM specification](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md), you can use standard [AWS CloudFormation](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-template-resource-type-ref.html) resource types.

## Fetch, tail, and filter Lambda function logs

To simplify troubleshooting, SAM CLI has a command called `sam logs`. `sam logs` lets you fetch logs generated by your deployed Lambda function from the command line. In addition to printing the logs on the terminal, this command has several nifty features to help you quickly find the bug.

`NOTE`: This command works for all AWS Lambda functions; not just the ones you deploy using SAM.

```bash
Microservice$ sam logs -n ExtractTransformFunction --stack-name Microservice --tail
```

You can find more information and examples about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

## Unit tests

Tests are defined in the `ExtractTransformFunction/src/test` folder in this project.

```bash
Microservice$ cd ExtractTransformFunction
ExtractTransformFunction$ gradle test
```

## Cleanup

To delete the sample application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
aws cloudformation delete-stack --stack-name Microservice
```

## Resources

See the [AWS SAM developer guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) for an introduction to SAM specification, the SAM CLI, and serverless application concepts.

Next, you can use AWS Serverless Application Repository to deploy ready to use Apps that go beyond hello world samples and learn how authors developed their applications: [AWS Serverless Application Repository main page](https://aws.amazon.com/serverless/serverlessrepo/)
