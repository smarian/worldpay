#How to ...
- build
`mvn clean package`
- run (for Windows use correct path like target\...)
`java -jar target/api-0.0.1-SNAPSHOT.jar`

#Prerequisites:
- Spring Boot was choosen for ease of exercise (innate support for HTTP requests and Tomcat embeded server)
- Architecture:
  - Controller
    - maps the REST endpoints
    - understand mapping between DTO and data formats and when to do it
    - know to deliver responses (get, errors, success)
   - Service
     - Knows only about data objects and can process/transform
     - Has knowledge of data and decides if an offer is to be active or expired
     - has  access to a 'persistance' layer
    - persistance - knows how to store/retrieve/ data
 - DTO is validated with help of a Spring validator, which is loaded to DataBinder
 However validation errors and other business logic errors are not driven through Exception, but captured, processed and returned in a normal flow.
 This is why I didn't feel the need in this example for implementing a custom ExceptionHandler and various Exception classes (like not found, conflict)
 - Structure was choosen to be easely extended in functionality  

#Assumptions:

- An offer is identified by product name. It can have space in it.
- There can be only one active offer  per product
- Merchant agnostic. Because upper, that means 2 merchants will not be able to put an offer on same product. However, implementing differentiated offers between merchants should be easy enough.
- An offer, once made, can't be modified for price, Currency and the only valid status change is in Canceled
- An active offer cannot be deleted
  
 #Ways to improve
  - tidy API contract and or use an established way to document, validate (openapi ?) 
  - may use a more mature mapper (mapstruct ?)
  - can be easy extended to support multiple merchants: make persistance layer to keep a 2nd layer of HashMap with merchant as key and modify accordingly to return more a list of offer for get   
  - can be easy extended to offer a history for offers (maintain a set of offers instead 1 offer per product and deal with order in time and overlapping offers) 