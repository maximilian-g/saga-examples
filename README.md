Project is created as demo implementation of some services in domain area "Restaurant".

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
If any action before "Final" action will fail, compensating action will be performed for every occurred action.

Docker images and docker compose file to be implemented.

