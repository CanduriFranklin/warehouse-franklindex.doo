# Sprint 3 - RabbitMQ Integration

## 📋 Overview

This document details the RabbitMQ integration implemented in Sprint 3 Phase 3, replacing the temporary `LoggingEventPublisher` with a production-ready asynchronous message broker.

---

## 🎯 Objectives

### Completed ✅
1. ✅ **RabbitMQ Configuration** - Exchanges, queues, bindings, DLQ, retry policies
2. ✅ **Event Publisher** - Production-ready RabbitMQEventPublisher with error handling
3. ✅ **Event Listeners** - Asynchronous consumers for all domain events
4. ✅ **Dead Letter Queue** - Automatic DLQ routing for failed messages
5. ✅ **Conditional Activation** - Enable/disable RabbitMQ via configuration
6. ✅ **Structured Logging** - Comprehensive logging for debugging and monitoring

---

## 🏗️ Architecture

### Message Flow

```
[Application Service] 
    ↓ (publishes)
[EventPublisher Interface]
    ↓ (implements)
[RabbitMQEventPublisher]
    ↓ (sends to)
[RabbitMQ Exchange: warehouse.events]
    ↓ (routes by key)
┌─────────────────┬──────────────────┬─────────────────────┐
│ delivery.       │ baskets.sold     │ baskets.disposed    │
│ received        │                  │                     │
↓                 ↓                  ↓
[Queue:           [Queue:            [Queue:
warehouse.        warehouse.         warehouse.
delivery]         baskets.sold]      baskets.disposed]
    ↓                 ↓                  ↓
[DeliveryReceived [BasketsSold       [BasketsDisposed
EventListener]    EventListener]     EventListener]
```

### Topology

```yaml
Exchanges:
  - warehouse.events (topic)
  - warehouse.dlx (direct)

Queues:
  - warehouse.delivery
  - warehouse.baskets.sold
  - warehouse.baskets.disposed
  - warehouse.dlq (Dead Letter Queue)

Bindings:
  - warehouse.delivery ← warehouse.events [delivery.received]
  - warehouse.baskets.sold ← warehouse.events [baskets.sold]
  - warehouse.baskets.disposed ← warehouse.events [baskets.disposed]
  - warehouse.dlq ← warehouse.dlx [warehouse.dlq]
```

---

## 📦 Components

### 1. RabbitMQConfig

**Location:** `infrastructure/config/RabbitMQConfig.java`

**Responsibilities:**
- Define exchanges, queues, and bindings
- Configure JSON message converter with Jackson
- Set up retry policies with exponential backoff
- Configure Dead Letter Exchange and Queue
- Enable publisher confirms and return callbacks

**Key Features:**
```java
// Retry configuration
- Max attempts: 3
- Initial interval: 3 seconds
- Multiplier: 2.0 (exponential backoff)
- Max interval: 30 seconds

// Dead Letter Configuration
- Failed messages → warehouse.dlx → warehouse.dlq

// Concurrency
- Initial consumers: 3
- Max consumers: 10
- Prefetch count: 10
```

### 2. RabbitMQEventPublisher

**Location:** `infrastructure/event/RabbitMQEventPublisher.java`

**Responsibilities:**
- Publish domain events to RabbitMQ
- Determine routing key based on event type
- Handle serialization errors
- Log publishing activity

**Event Routing:**
```java
DeliveryReceivedEvent → delivery.received
BasketsSoldEvent → baskets.sold
BasketsDisposedEvent → baskets.disposed
```

**Error Handling:**
- Publisher confirms for message acknowledgment
- Return callbacks for unroutable messages
- Structured logging for debugging

**Activation:**
```yaml
rabbitmq.enabled: true  # Use RabbitMQEventPublisher
rabbitmq.enabled: false # Use LoggingEventPublisher
```

### 3. Event Listeners

#### DeliveryReceivedEventListener

**Location:** `infrastructure/event/listener/DeliveryReceivedEventListener.java`

**Queue:** `warehouse.delivery`

**Responsibilities:**
- Log delivery information
- Send notifications (email, SMS)
- Update analytics/metrics
- Integrate with ERP systems

#### BasketsSoldEventListener

**Location:** `infrastructure/event/listener/BasketsSoldEventListener.java`

**Queue:** `warehouse.baskets.sold`

**Responsibilities:**
- Update sales dashboard
- Send customer receipts
- Record revenue in accounting
- Trigger loyalty points
- Update forecasting models

#### BasketsDisposedEventListener

**Location:** `infrastructure/event/listener/BasketsDisposedEventListener.java`

**Queue:** `warehouse.baskets.disposed`

**Responsibilities:**
- Record loss/waste metrics
- Trigger high disposal alerts
- Update quality control
- Generate compliance reports
- Optimize inventory parameters

---

## 🔄 Retry and Error Handling

### Retry Strategy

1. **First Attempt:** Immediate processing
2. **Second Attempt:** After 3 seconds (if failed)
3. **Third Attempt:** After 6 seconds (exponential backoff)
4. **After Max Attempts:** Message sent to DLQ

### Exponential Backoff Formula

```
delay = initial_interval * (multiplier ^ (attempt - 1))
delay = min(delay, max_interval)

Example:
- Attempt 1: 0ms (immediate)
- Attempt 2: 3000ms (3s)
- Attempt 3: 6000ms (6s)
```

### Dead Letter Queue

**Purpose:** Store failed messages for manual inspection and reprocessing

**Access:**
```bash
# View messages in DLQ
rabbitmqadmin get queue=warehouse.dlq count=10

# Requeue message
rabbitmqadmin publish exchange=warehouse.events \
    routing_key=delivery.received \
    payload="$(rabbitmqadmin get queue=warehouse.dlq ackmode=ack_requeue_true -f json)"
```

---

## 🔧 Configuration

### Environment Variables

```bash
# RabbitMQ Connection
RABBITMQ_ENABLED=true
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=your-secure-password
RABBITMQ_VHOST=/
```

### application.yml

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}
    virtual-host: ${RABBITMQ_VHOST:/}
    listener:
      simple:
        acknowledge-mode: auto
        retry:
          enabled: true
          initial-interval: 3000
          max-attempts: 3
          multiplier: 2
        default-requeue-rejected: false
```

### application-dev.yml

```yaml
rabbitmq:
  enabled: ${RABBITMQ_ENABLED:true}

logging:
  level:
    org.springframework.amqp: DEBUG
    org.springframework.rabbit: INFO
```

---

## 🧪 Testing

### Local Testing with Docker

```bash
# Start RabbitMQ with management UI
docker run -d \
  --name warehouse-rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3.12-management-alpine

# Access management UI
open http://localhost:15672
# Login: guest / guest
```

### Testing Without RabbitMQ

```bash
# Disable RabbitMQ (uses LoggingEventPublisher)
export RABBITMQ_ENABLED=false
./gradlew bootRun
```

### Manual Testing

```bash
# 1. Create a delivery (triggers DeliveryReceivedEvent)
curl -X POST http://localhost:8080/api/v1/deliveries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "totalQuantity": 100,
    "validationDate": "2025-12-31",
    "totalCost": 1000.00,
    "profitMarginPercentage": 20.0
  }'

# 2. Check RabbitMQ management UI
# - Go to Queues tab
# - Verify message was published and consumed
# - Check message details

# 3. View application logs
tail -f logs/warehouse-service.log | grep "📢\|📦\|💰\|🗑️"
```

---

## 📊 Monitoring

### Key Metrics to Monitor

1. **Message Rate**
   - Messages published per second
   - Messages consumed per second
   - Message acknowledgment rate

2. **Queue Depth**
   - Current message count in each queue
   - Alert if queue depth > threshold

3. **Consumer Performance**
   - Processing time per message
   - Success rate
   - Retry rate

4. **Dead Letter Queue**
   - Message count in DLQ
   - Alert if DLQ has messages

### Logging

```bash
# Publisher logs
📢 Published event: DeliveryReceivedEvent to exchange: warehouse.events with routing key: delivery.received

# Consumer logs
📦 Received DeliveryReceivedEvent: deliveryBoxId=..., quantity=...
✅ Successfully processed DeliveryReceivedEvent: ...

# Error logs
❌ Error processing BasketsSoldEvent: ... - Error: ...
❌ Message returned: ... - Reply: ... - Exchange: ... - Routing Key: ...
```

### RabbitMQ Management UI

- **Queues Tab:** View queue depths, rates, consumers
- **Exchanges Tab:** View bindings and routing
- **Connections Tab:** Monitor active connections
- **Channels Tab:** View publisher/consumer channels

---

## 🚀 Deployment

### Production Checklist

- [ ] Set strong RabbitMQ credentials
- [ ] Enable SSL/TLS for RabbitMQ connections
- [ ] Configure clustering for high availability
- [ ] Set up monitoring and alerting (Prometheus/Grafana)
- [ ] Configure backup and disaster recovery
- [ ] Set resource limits (memory, disk)
- [ ] Enable publisher confirms in production
- [ ] Configure appropriate retry policies
- [ ] Set up DLQ monitoring and alerting
- [ ] Document runbook for message recovery

### Production Configuration

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST}  # Load balancer or cluster
    port: 5671  # TLS port
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}
    ssl:
      enabled: true
      verify-hostname: true
    publisher-confirm-type: correlated
    publisher-returns: true
    listener:
      simple:
        acknowledge-mode: manual
        prefetch: 10
        concurrency: 5
        max-concurrency: 20
```

---

## 🔍 Troubleshooting

### Issue: Messages not being consumed

**Symptoms:**
- Queue depth increasing
- No consumer logs

**Diagnosis:**
```bash
# Check if listeners are active
curl http://localhost:8080/api/actuator/health | jq '.components.rabbit'

# Check RabbitMQ connections
rabbitmqctl list_connections
rabbitmqctl list_channels
```

**Solutions:**
1. Verify `rabbitmq.enabled=true`
2. Check RabbitMQ is running
3. Verify credentials are correct
4. Check firewall rules
5. Review application logs for errors

### Issue: Messages going to DLQ

**Symptoms:**
- Messages in `warehouse.dlq`
- Consumer error logs

**Diagnosis:**
```bash
# Get messages from DLQ
rabbitmqadmin get queue=warehouse.dlq count=10

# Check error logs
grep "❌ Error processing" logs/warehouse-service.log
```

**Solutions:**
1. Fix the root cause (business logic error)
2. Update consumer code
3. Redeploy application
4. Requeue messages from DLQ

### Issue: High latency

**Symptoms:**
- Slow message processing
- Queue depth increasing

**Diagnosis:**
- Check consumer processing time in logs
- Monitor CPU/memory usage
- Check database connection pool

**Solutions:**
1. Increase consumer concurrency
2. Optimize business logic
3. Add caching
4. Scale horizontally

---

## 📚 References

- [Spring AMQP Documentation](https://docs.spring.io/spring-amqp/docs/current/reference/html/)
- [RabbitMQ Best Practices](https://www.rabbitmq.com/best-practices.html)
- [Dead Letter Exchanges](https://www.rabbitmq.com/dlx.html)
- [Publisher Confirms](https://www.rabbitmq.com/confirms.html)

---

## 🎓 Next Steps

1. **Implement Outbox Pattern** - Ensure exactly-once delivery
2. **Add Circuit Breaker** - Handle RabbitMQ unavailability
3. **Implement Event Sourcing** - Store event history
4. **Add Message Tracing** - Distributed tracing with Zipkin
5. **Performance Testing** - Load test with high message volume

---

**Created:** 2025-10-14  
**Author:** Franklin Canduri  
**Sprint:** 3 - Phase 3  
**Status:** ✅ Completed
