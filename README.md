# warehouse-service

Warehouse Service with features to import/display and sell products and artifacts with specific JSON template

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 
See deployment for notes on how to deploy the project on a system.

### Prerequisites

Things you need to get the project up and running on a local machine:

* Docker latest version
* docker-compose latest version

### Building and Running the application

* Execute the below docker-compose command to run the application:
```
docker-compose up --build
```

#### Swagger Endpoint

* Open url 'http://localhost:8080/swagger-ui.html' in the web browser to open swagger to check REST apis.

#### REST services

* REST services can be accessed on: http://localhost:8080 .
* POST method must be used to import files into the system .
* GET method will retrieve articles or product details .
* PUT method in products must be used to sell a product.
* Articles json file needs to be imported before Products file.

#### Actuators 

* Can be accessed on: http://localhost:8080/actuator .


#### PostgreSQL Database connection

* The database can be connected by using the following details:
```
host=localhost
port=5432
database=postgres
user=postgres
password=password123
```

### Stopping the application and cleaning-up
```
docker-compose down -v --rmi all --remove-orphans
```