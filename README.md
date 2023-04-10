## stack

- Java 17
- Servlet API
- Apache Tomcat 9.0.54
- PostgreSQL
- Gradle 7.5

## get started

To run the project you will need *Java 17*, *Apache Tomcat version 9.+*, *Gradle 7.5* and *PostgreSQL*

To start the project you need to perform the following steps:
1. pull this project
2. сreate a *PostgreSQL* database
3. in the *application.yml* file which is located along the path *src/main/resources/*<br>
   set the **```port```**, **```database name```**, **```username```** and **```password```**
4. using the *terminal* or *cmd* go to the folder with this project
5. enter the command ***gradle build*** and wait for it to complete

## endpoints

The project is running on **```localhost:8080```**

**```GET```** - ***/api/v1/receipt***
>description - creating a receipt<br>
>require parameters - **```productId```** and **```productQty```**<br>
>not require parameters - **```card```**<br>
>url example - ***localhost:8080/api/v1/receipt?productId=1&productQty=2&productId=3&productQty=4&productId=5&productQty=6&card=1234***

**```GET```** - ***/api/v1/products***
>description - getting all products (by default - 20)<br>
>url example - ***localhost:8080/api/v1/products***

**```GET```** - ***/api/v1/products?pageSize=pageSize&pageNumber=pageNumber***
>description - getting products page<br>
>url example - ***localhost:8080/api/v1/products?pageSize=5&pageNumber=3***

**```GET```** - ***/api/v1/products/{id}***
>description - getting a product<br>
>url example - ***localhost:8080/api/v1/products/1***

**```POST```** - ***/api/v1/products***
>description - saving a product<br>
>request body example:<br>
>```javascript
>{
>  "name": "Chicken 1.5kg",
>  "price": 15.54,
>  "isPromotional": false
>}

**```PUT```** - ***/api/v1/products/{id}***
>description - updating a product<br>
>url example - ***localhost:8080/api/v1/products/4<br>***
>request body example:<br>
>```javascript
>{
>  "name": "Cheese 1kg",
>  "price": 10.54,
>  "isPromotional": true
>}

**```DELETE```** - ***/api/v1/products/{id}***
>description - deleting a product<br>
>url example - ***localhost:8080/api/v1/products/1***

**```GET```** - ***/api/v1/discount-cards***
>description - getting all discount cards (by default - 20)<br>
>url example - ***localhost:8080/api/v1/discount-cards***

**```GET```** - ***/api/v1/discount-cards?pageSize=pageSize&pageNumber=pageNumber***
>description - getting discount cards page<br>
>url example - ***localhost:8080/api/v1/discount-cards?pageSize=2&pageNumber=1***

**```GET```** - ***/api/v1/discount-cards/{id}***
>description - getting a discount card<br>
>url example - ***localhost:8080/api/v1/discount-cards/1***

**```POST```** - ***/api/v1/discount-cards***
>description - saving a discount card<br>
>request body example:<br>
>```javascript
>{
>  "number": 3456,
>  "discount": 3.0
>}

**```PUT```** - ***/api/v1/discount-cards/{id}***
>description - updating a discount card<br>
>url example - ***localhost:8080/api/v1/discount-cards/2<br>***
>request body example:<br>
>```javascript
>{
>  "number": 7890,
>  "price": 5.0
>}

**```DELETE```** - ***/api/v1/discount-cards/{id}***
>description - deleting a discount card<br>
>url example - ***localhost:8080/api/v1/discount-cards/1***

## exception handling filter

description - servlet filter for exception handling<br>
>url example - ***localhost:8080/api/v1/products/26<br>***
>response body example:<br>
>```javascript
>{
>  "statusCode": 404,
>  "timestamp": "11/04/2023 00:08:19",
>  "uri": "/api/v1/products/26",
>  "message": "Product with id = 26 not found"
>}
