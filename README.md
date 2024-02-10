Project is created as demo implementation of some microservices in domain area "Restaurant".
<br>
"Saga" pattern (with Choreography technique) is implemented using RabbitMQ.

This project was created to practice usage of
Spring, parts of Spring Cloud, Open Feign, and microservices concept in general.

Current project will use only AMQP (RabbitMQ in this case) as communication protocol between services.
<br>
Main possibility of this demo is create order, call hierarchy will look like:
<br>

    POST -> order-service.createOrder:
    1 - PRODUCED OrderCreated message in queue "order.created" by order-service - creating order, sending event to queue
    2 - CONSUMED OrderCreated message in queue "order.created" by customer-service -> customer-service.canMakeOrders(), - returns customer info, changes customer status, so "canMakeOrders" property cannot be changed temporary
    3 - PRODUCED OrderCreated message in queue "customer.valid" by customer-service - customer service sending event to another queue (for another service)
    4 - CONSUMED OrderCreated message in queue "customer.valid" by kitchen-service -> kitchen-service.createTicket()
    5 - PRODUCED OrderCreated message in queue "card.auth" by kitchen-service - sending order event for card authorization for this order
    6 - CONSUMED OrderCreated message in queue "card.auth" by authorization-service -> authorization-service.authorizeCard(), | Final action after which rollback is not possible
    7 - PRODUCED OrderId message in queue "ticket.approved" by authorization-service - approving kitchen ticket
    8 - PRODUCED OrderId message in queue "order.approved" by authorization-service - approving order itself
    9 - PRODUCED CustomerId message in queue "customer.end-transaction" by authorization-service - to complete transaction on customer
    10 - CONSUMED OrderId message in queue "ticket.approved" by kitchen-service -> kitchen-service.approveTicket()
    11 - CONSUMED OrderId message in queue "order.approved" by order-service -> order-service.approveOrder()
    12 - CONSUMED CustomerId message in queue "customer.end-transaction" by customer-service -> customer-service.changeStatusToReadyForUpdate()

If any action before "Final" action will fail, compensating action will be performed for every occurred action to make our state consistent.

Docker images and docker compose files are provided.
