## Rate Limiter


    

Introduction
===

The purpose of this project is to build a demo Rate Limiter API. The following technologies are used:


- `Akka HTTP`: A simple Web Server
- `Zerocode`: A load testing tool
- `Maven`: Build tool
- `Java >= 8`: Used for everything :wink:


If the rate gets higher than the threshold on an endpoint, the API should stop responding for 5 seconds 
===

This section of requirement was not really understood, so I assumed that when the number of permits/requets have been exceeded within a particular quota, and their still requests coming in within that quota, delay the next quota by additional 5 seconds. 

Project Structure
===

The project makes use of just one maven project with a main folder and the following sub-packages:

- `account`: Contains the bank and account actors 
- `commons`: Contains utility classes
- `controllers`: Contains the directives to expose the application for HTTP access
- `models`: Contains the hotek model
- `services`: Contains the API and implementation for the rate limiter and hotel repository.
 


    
Integration Test and Running
===

Under the root folder, there is a `integration-test` folder, it contains the following classes
- `BasicSingleRequest`: Runs a single scenario of getting hotels of bangkok
- `BasicMultipleRequest`: Runs `BasicSingleRequest` above 100 times within 9 seconds, making it acquire 100 permits with that time frame
- `FailedSingleRequest`: Runs a single scenario of failing to get any other permit
- `CombinedTestSuiteIT`: This is the only directly executable integration test. During integration test, the city is configured to allow 100 requests within 10 seconds, so `CombinedTestSuiteIT` runs `BasicMultipleRequest` then `FailedSingleRequest` to assert that 100 concurrent requests are able to acquire all 100 permits within 9 seconds and subsequent request fails with status 403
  

To package the application without running the integration test 

> mvn clean package

After packaging, an executable jar file will be produced, run with
> java -jar ./target/java-project-1.0.jar

You can package and run the integration test
> mvn clean verify

if the integration tests fail, it could be due to system resources, package and run the application then run the integration tests separately in another terminal using 
>  mvn clean verify -P integration

To configure the number requests/permits allowed for a particular endpoint use 
> -Dapi.{endpoint}.permits={value}

To configure the time quota for a particular endpoint use 
> -Dapi.{endpoint}.timer={value}

For example to configure that the city endpoint allow 100 requests within 10 seconds
> java -Dapi.city.permits=100 -Dapi.city.timer=10 -jar ./target/java-project-1.0.jar 

Optionally you can run the project through maven
>mvn exec:java -Dexec.mainClass="com.kiamesdavies.limiter.App"

Endpoints will be available at [localhost:9099](http://localhost:9099/)

Usage
===

<table>
<thead>
<tr>
<th>Endpoint</th>
<th>Body/Parameters</th>
<th>Description</th>

<th>Success Response</th>
</tr>
</thead>
<tbody>
<tr>
	<td><code>GET /city/{city}</code></td>
  <td>sord=[ASC | DESC]</td>
	<td>Returns all the hotels belonging to a specific city</td>
	<td>
      <pre>
"data": [
            {
              "id": "integer",
              "city": "string",
              "room": "string",
              "price": "double"
            }
        ]
	  </pre>
    </td>
</tr>
<tr>
	<td><code>GET /room/{room}</code></td>
  <td>sord=[ASC | DESC]</td>
	<td>Returns all the hotels belonging to a specific room, </td>
	<td>
       <pre>
"data": [
            {
              "id": "integer",
              "city": "string",
              "room": "string",
              "price": "double"
            }
        ]
    </pre>
    </td>
</tr>
</tbody>
</table>

