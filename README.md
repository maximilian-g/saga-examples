Project is created as demo implementation of some microservices in domain area "Restaurant".
<br>
"Saga" pattern (with Orchestration technique) is implemented using simple HTTP requests.
Order service class (https://github.com/maximilian-g/saga-examples/blob/master/order-service/src/main/java/com/maximilian/restaurant/order/service/OrderService.java)
Order service is an Orchestrator in this scenario.

This project was created to practice usage of
Spring, parts of Spring Cloud, Open Feign, and microservices concept in general.

Current project will use only HTTP as communication protocol for now.
<br>
Main possibility of this demo is create order, call hierarchy will look like:
<br>

    POST -> order-service.createOrder:
    1 - PUT -> customer-service.canMakeOrders(), - returns customer info, changes customer status, so "canMakeOrders" property cannot be changed temporary
    2 - POST -> kitchen-service.createTicket(),
    3 - POST -> authorization-service.authorizeCard(), | Final action after which rollback is not possible
    4 - POST -> kitchen-service.approveTicket()
    5 - POST -> customer-service.changeStatusToReadyForUpdate()
    6 - POST -> order-service.approveOrder()
If any action before "Final" action will fail, compensating action will be performed for every occurred action to make our state consistent.

Docker images and docker compose files are provided.

