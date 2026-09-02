# GlobalTrade Logistics — Enterprise Supply Chain Platform

**Platform Version:** Jakarta EE 10 / GlassFish 7.0.25  
**Architecture:** Enterprise Java Beans (EJB 3.1+) Multi-Module EAR  
**Coursework / Module:** BCD II  

---

## 1. Project Overview

**GlobalTrade Logistics** is a distributed enterprise logistics and supply chain coordination platform built with **Jakarta EE 10**, **EJB 3.1+**, **JPA 3.1** (Hibernate ORM 6.6), and **JAX-RS 3.1** RESTful web services. It provides mission-critical enterprise capabilities for cross-border freight forwarding, automated customs compliance verification, multimodal route optimization, stateful conversational booking sessions, vendor performance evaluations, and automated background schedulers.

---

## 2. Multi-Module Project Structure

```
GlobalTrade Logistics
├── persistence/        # JPA 3.1 Entities, DAOs, EntityManagerProducer & Persistence Services (JAR)
├── ejb-api/            # EJB Local Interfaces, Lombok DTOs, Application Exceptions (JAR)
├── ejb-security/       # Interceptors (Audit, Compliance, Performance, Validation) & JWT Security (EJB-JAR)
├── ejb-customs/        # Customs compliance EJB business logic (EJB-JAR)
├── ejb-shipment/       # Tracking, Fulfillment, Routing, Booking, Monitoring, Scheduler EJBs (EJB-JAR)
├── ejb-vendor/         # Vendor evaluation and KPI scorecard EJBs (EJB-JAR)
├── web/                # JAX-RS REST Controllers and Request/Response Models (WAR)
└── ear/                # Enterprise Archive packaging (globaltrade-logistics-ear.ear)
```

---

## 3. Key EJB Components & Capabilities

### 3.1 Stateless Session Beans (`@Stateless`)
- **[`CustomsComplianceBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-customs/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_customs/service/CustomsComplianceBean.java)** (`ejb-customs`): Electronic filing, tariff determination, customs approvals/holds.
- **[`ShipmentTrackingBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/service/ShipmentTrackingBean.java)** (`ejb-shipment`): Tracking lifecycle, status transitions, ETA calculations, delay detection.
- **[`OrderFulfillmentBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/service/OrderFulfillmentBean.java)** (`ejb-shipment`): Multi-phase order fulfillment, atomic inventory deduction, restock alerting.
- **[`RouteOptimizationBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/service/RouteOptimizationBean.java)** (`ejb-shipment`): Multimodal carrier evaluation (`COST`, `SPEED`, `ECO`, `RELIABILITY`).
- **[`BatchLogisticsBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/service/BatchLogisticsBean.java)** (`ejb-shipment`): Batch dispatch processing and cargo manifest generation.
- **[`VendorEvaluationBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-vendor/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_vendor/service/impl/VendorEvaluationBean.java)** (`ejb-vendor`): Vendor SLA tracking, on-time delivery rate calculations, scorecard generation.

### 3.2 Stateful Session Beans (`@Stateful`)
- **[`ShipmentBookingSessionBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/service/ShipmentBookingSessionBean.java)** (`ejb-shipment`): Preserves multi-step client booking cart conversationally across item addition, carrier selection, price estimation, and confirmation/cancellation (`@Remove`).

### 3.3 Singleton Session Beans (`@Singleton`, `@Startup`)
- **[`SupplyChainMonitoringBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/service/SupplyChainMonitoringBean.java)** (`ejb-shipment`): Startup initialization, container-managed concurrency (`@Lock(READ)` / `@Lock(WRITE)`), KPI metrics, and alert monitoring.
- **[`LogisticsSchedulerBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/schedule/LogisticsSchedulerBean.java)** (`ejb-shipment`): Background periodic timers (`@Schedule`) for delay detection, customs deadline escalations, and midnight stock audits.

### 3.4 Interceptor Pipeline (`ejb-security`)
- **[`AuditLoggingInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/AuditLoggingInterceptor.java)**: Automated audit trail logging of business bean invocations.
- **[`PerformanceMonitoringInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/PerformanceMonitoringInterceptor.java)**: Method execution duration tracking and SLA telemetry.
- **[`RegulatoryComplianceInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/RegulatoryComplianceInterceptor.java)**: Real-time enforcement against sanctioned trade jurisdictions and high-value cargo verification.
- **[`VendorValidationInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/VendorValidationInterceptor.java)**: Blocks suspended vendors from dispatch operations.

### 3.5 Transfer Objects & Lombok Annotations (`ejb-api/dto`)
All Data Transfer Objects in [`lk.raminsenanayake.globaltrade_logistics.ejb_api.dto`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto) leverage Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`) and implement `Serializable`:
- [`BatchDispatchItem`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/BatchDispatchItem.java), [`BatchDispatchResult`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/BatchDispatchResult.java)
- [`BookingItemDto`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/BookingItemDto.java), [`BookingSummary`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/BookingSummary.java)
- [`OrderItemDto`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/OrderItemDto.java)
- [`RouteOption`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/RouteOption.java), [`RouteResult`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/RouteResult.java)
- [`SystemStatusSummary`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/SystemStatusSummary.java), [`VendorScorecard`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/VendorScorecard.java)

---

## 4. Build & Deployment Instructions

### Prerequisites
- **Java Development Kit:** JDK 17+
- **Build Tool:** Apache Maven 3.9+
- **Application Server:** GlassFish 7.0.25+ (or compatible Jakarta EE 10 Application Server)
- **Database:** MySQL 8.0+ (Configured JNDI data source: `jdbc/globaltrade-logistics`)

### Build the Project
To compile, execute all unit test suites, and package the complete enterprise archive:
```bash
mvn clean package
```

The resulting EAR package will be generated at:
```
ear/target/globaltrade-logistics-ear.ear
```

### Deploy to GlassFish
Deploy the EAR archive using the `asadmin` CLI or GlassFish Admin Console:
```bash
asadmin deploy --force ear/target/globaltrade-logistics-ear.ear
```

---

## 5. REST API Endpoints Overview

All REST endpoints are exposed under the `/api` context path:

- **Authentication:** `POST /api/auth/login`, `POST /api/auth/register`, `POST /api/auth/refresh`, `GET /api/auth/users`
- **Shipments:** `POST /api/shipments`, `GET /api/shipments`, `GET /api/shipments/{trackingNumber}`, `PUT /api/shipments/{trackingNumber}/status`, `GET /api/shipments/delays`
- **Customs:** `POST /api/customs/declarations`, `PUT /api/customs/declarations/{decNum}/review`, `GET /api/customs/compliance/{trackingNumber}`, `GET /api/customs/declarations/pending`, `GET /api/customs/declarations/deadlines`
- **Vendors:** `POST /api/vendors`, `GET /api/vendors`, `POST /api/vendors/{vendorCode}/evaluate`, `GET /api/vendors/{vendorCode}/scorecard`, `POST /api/vendors/assign`
- **Routes:** `GET /api/routes/optimize`, `GET /api/routes/compare`
- **Stateful Booking:** `POST /api/booking/start`, `POST /api/booking/items`, `DELETE /api/booking/items/{sku}`, `POST /api/booking/carrier`, `GET /api/booking/summary`, `POST /api/booking/confirm`, `POST /api/booking/cancel`
- **Batch Logistics:** `POST /api/batch/dispatch`, `POST /api/batch/manifest`
- **Monitoring:** `GET /api/monitoring/status`, `GET /api/monitoring/alerts`, `PUT /api/monitoring/alerts/{id}/acknowledge`, `GET /api/monitoring/metrics`
- **Inventory:** `GET /api/inventory`, `GET /api/inventory/{sku}`, `GET /api/inventory/low-stock`, `POST /api/inventory`, `POST /api/inventory/{sku}/restock`

---

## 6. Documentation Files
- [`TECHNICAL_IMPLEMENTATION_DOCUMENTATION.md`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/TECHNICAL_IMPLEMENTATION_DOCUMENTATION.md): Comprehensive architectural designs, EJB session beans, interceptors, timer services, DTO specifications, and transaction isolation.
- [`CRITICAL_ANALYSIS_AND_TEST_REPORT.md`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/CRITICAL_ANALYSIS_AND_TEST_REPORT.md): Critical analysis (EJB vs Spring Boot vs Microservices) and automated unit test execution report (16 passing test suites).
