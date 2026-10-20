# Decisions

## 1. Which endpoints did you add, and why does this system need them?

I added `GET /api/v1/employee/count`. I chose this because it gives a webhook
consumer a cheap way to check whether the employee collection contains any
records without downloading the full list. It is also useful for a basic
health or synchronization check while this example is using in-memory data.

## 2. Did you add any dependency? What does it buy, and what does it cost?

I didn't add any dependencies. The project already includes Spring Web for
the controllers and JSON binding, SLF4J for logging, and Spring Boot's test
starter for the MockMvc tests. Adding another library would not have solved a
problem that the existing dependencies could not handle.

## 3. How did you handle errors and logging, and why those mechanisms?

I return `400 Bad Request` for incomplete or invalid create requests. Spring
handles malformed UUID path variables as `400 Bad Request`, while a correctly
formed UUID that is not in the collection returns `404 Not Found`. I used a
`ConcurrentHashMap` because the service may receive overlapping requests even
though the data is only held in memory.

The service logs employee creation and UUID lookup results. I don't log complete 
request bodies because employee data, especially email addresses, should not end 
up in application logs.

## 4. Where the brief left a decision to you, what did you decide?

I start the service with two mock employees because the brief allows generated
mock data. This makes the GET endpoints useful immediately instead of making a
new user create the first record before testing them.

The service generates UUIDs, derives `fullName` from the first and last names,
and allows `contractTerminationDate` to be omitted. I require the other create
fields, reject negative salary and age values, and reject a termination date
that comes before the hire date. A successful create returns `201 Created`.

## 5. What did you deliberately choose *not* to do?

I left out a database, authentication provider, container, message broker, and
update/delete endpoints. The challenge specifically keeps the exercise to API
endpoints and in-memory storage, so adding that infrastructure would add scope
without improving the required behavior. For a real deployment, I would revisit
persistence, authentication, and update operations once their contracts and
operational requirements were known.
