# GlobalTrade Logistics — Enterprise Supply Chain Platform

**Platform Version:** Jakarta EE 10 / GlassFish 7.0.25 (Java 17)  
**Architecture:** Enterprise Java Beans (EJB 3.1+) Multi-Module EAR + React Single Page Application (SPA)  
**Coursework / Module:** BCD II  

---

## 1. Project Overview

**GlobalTrade Logistics** is a distributed enterprise logistics and supply chain coordination platform built with **Jakarta EE 10**, **EJB 3.1+**, **JPA 3.1** (Hibernate ORM 6.6), **JAX-RS 3.1** RESTful web services, and a modern **React + Tailwind CSS** frontend. It provides mission-critical enterprise capabilities for cross-border freight forwarding, automated customs compliance verification, multimodal route optimization, stateful conversational booking sessions, vendor performance evaluations, and automated background schedulers.

---

## 2. Multi-Module Project Structure

```
GlobalTrade Logistics
├── persistence/        # JPA 3.1 Entities, Enums, Service Interfaces & persistence.xml (JAR)
├── ejb-persistence/    # EJB Stateless Persistence Services & Data Initializer (EJB-JAR)
├── ejb-api/            # EJB Local Interfaces, Lombok DTOs, Application Exceptions (JAR)
├── ejb-security/       # Interceptors (Audit, Compliance, Performance, Validation) & JWT Security (EJB-JAR)
├── ejb-customs/        # Customs compliance EJB business logic (EJB-JAR)
├── ejb-shipment/       # Tracking, Fulfillment, Routing, Booking, Monitoring, Scheduler EJBs (EJB-JAR)
├── ejb-vendor/         # Vendor evaluation and KPI scorecard EJBs (EJB-JAR)
├── web/                # JAX-RS REST Controllers and Request/Response Models (WAR)
│   └── frontend/       # React 18, Vite, Tailwind CSS Single Page Application (SPA)
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
- **Persistence Stateless EJBs** (`ejb-persistence`): High-performance transactional persistence services providing ACID entity management for JPA entities ([`ShipmentPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/ShipmentPersistenceServiceImpl.java), [`CustomsPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/CustomsPersistenceServiceImpl.java), [`VendorPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/VendorPersistenceServiceImpl.java), [`InventoryPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/InventoryPersistenceServiceImpl.java), [`UserPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/UserPersistenceServiceImpl.java), [`AlertPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/AlertPersistenceServiceImpl.java), [`AuditLogPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/AuditLogPersistenceServiceImpl.java), [`PerformanceMetricPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/PerformanceMetricPersistenceServiceImpl.java), [`RefreshTokenPersistenceServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/RefreshTokenPersistenceServiceImpl.java), and [`DataInitializerServiceImpl`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-persistence/src/main/java/lk/raminsenanayake/globaltrade_logistics/persistence/service/impl/DataInitializerServiceImpl.java)).

### 3.2 Stateful Session Beans (`@Stateful`)
- **[`ShipmentBookingSessionBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/service/ShipmentBookingSessionBean.java)** (`ejb-shipment`): Preserves multi-step client booking cart conversationally across item addition, carrier selection, price estimation, and confirmation/cancellation (`@Remove`).

### 3.3 Singleton Session Beans (`@Singleton`, `@Startup`)
- **[`SupplyChainMonitoringBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/service/SupplyChainMonitoringBean.java)** (`ejb-shipment`): Startup initialization, container-managed concurrency (`@Lock(READ)` / `@Lock(WRITE)`), KPI metrics, and alert monitoring.
- **[`LogisticsSchedulerBean`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-shipment/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_shipment/schedule/LogisticsSchedulerBean.java)** (`ejb-shipment`): Background periodic timers (`@Schedule`) for delay detection, customs deadline escalations, and midnight stock audits.

### 3.4 Interceptor Pipeline (`ejb-security`)
- **[`AuditLoggingInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/AuditLoggingInterceptor.java)**: Automated audit trail logging of business bean invocations using `REQUIRES_NEW` transaction isolation.
- **[`PerformanceMonitoringInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/PerformanceMonitoringInterceptor.java)**: Method execution duration tracking and SLA telemetry.
- **[`RegulatoryComplianceInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/RegulatoryComplianceInterceptor.java)**: Real-time enforcement against sanctioned trade jurisdictions and high-value cargo verification.
- **[`VendorValidationInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/VendorValidationInterceptor.java)**: Blocks suspended vendors from dispatch operations.
- **[`SecurityAuthorizationInterceptor`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-security/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_security/interceptor/SecurityAuthorizationInterceptor.java)**: Programmatic role-based caller validation.

### 3.5 Transfer Objects & Lombok Annotations (`ejb-api/dto`)
All Data Transfer Objects in [`lk.raminsenanayake.globaltrade_logistics.ejb_api.dto`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto) leverage Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`) and implement `Serializable`:
- [`BatchDispatchItem`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/BatchDispatchItem.java), [`BatchDispatchResult`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/BatchDispatchResult.java)
- [`BookingItemDto`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/BookingItemDto.java), [`BookingSummary`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/BookingSummary.java)
- [`OrderItemDto`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/OrderItemDto.java)
- [`RouteOption`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/RouteOption.java), [`RouteResult`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/RouteResult.java)
- [`SystemStatusSummary`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/SystemStatusSummary.java), [`VendorScorecard`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/ejb-api/src/main/java/lk/raminsenanayake/globaltrade_logistics/ejb_api/dto/VendorScorecard.java)

---

## 4. Frontend Single Page Application (`web/frontend`)

The platform features a modern, responsive web application built with **React 18**, **Vite**, **Tailwind CSS**, and **Lucide React** icons:

- **Executive Dashboard:** Real-time KPI summaries, active shipments, pending customs declarations, delayed cargo count, and operational alert management with one-click acknowledgement.
- **Shipment Management:** Full lifecycle tracking (`CREATED`, `PENDING_CLEARANCE`, `IN_TRANSIT`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CUSTOMS_HOLD`, `CANCELLED`), delay scanner, and status updating.
- **Stateful 4-Step Booking Wizard:** Conversational booking wizard backed by `@Stateful` EJB session (Origin/Destination → Item Specifications → Multimodal Carrier Selection → Cost Summary & Confirmation).
- **Customs Compliance Center:** Declaration submission, officer review approval/rejection workflows, tariff calculations, and 72-hour filing deadline monitors.
- **Vendor Partner Portal:** Vendor registration, automated SLA scorecard evaluation, on-time delivery percentages, and compliance status tracking.
- **Multimodal Route Optimization:** Real-time route optimization engine comparing Air, Sea, and Rail carriers based on `COST`, `SPEED`, `ECO` (carbon footprint), and `RELIABILITY`.
- **Warehouse Inventory Control:** SKU management, current stock levels, safety reorder thresholds, and restock operations.
- **Batch Logistics Dispatcher:** High-throughput batch order dispatching and cargo manifest generator.
- **User & Access Management:** User registration and role administration (`ADMIN`, `LOGISTIC_PERSONNEL`, `CUSTOM_OFFICIAL`, `VENDOR`, `CUSTOMER`).

---

## 5. Build & Deployment Instructions

### Prerequisites
- **Java Development Kit:** JDK 17+
- **Build Tool:** Apache Maven 3.9+
- **Node.js & npm:** Node.js 18+ (for frontend development/build)
- **Application Server:** GlassFish 7.0.25+ (or compatible Jakarta EE 10 Application Server)
- **Database:** MySQL 8.0+ (Configured JNDI data source: `jdbc/globaltrade-logistics`)

### Build Backend (EAR Package)
To compile, execute all unit test suites, and package the complete enterprise archive:
```bash
mvn clean package
```

The resulting EAR package will be generated at:
```
ear/target/globaltrade-logistics-ear.ear
```

### Run Frontend Development Server
```bash
cd web/frontend
npm install
npm run dev
```

### Build Frontend for Production
```bash
cd web/frontend
npm run build
```

### Deploy to GlassFish
Deploy the EAR archive using the `asadmin` CLI or GlassFish Admin Console:
```bash
asadmin deploy --force ear/target/globaltrade-logistics-ear.ear
```

---

## 6. REST API Endpoints Overview

All REST endpoints produce and consume `application/json` under the `/api` context path:

- **Authentication:** `POST /api/auth/login`, `POST /api/auth/register`, `POST /api/auth/refresh`, `GET /api/auth/users`
- **Shipments:** `POST /api/shipments`, `GET /api/shipments`, `GET /api/shipments/{trackingNumber}`, `PUT /api/shipments/{trackingNumber}/status`, `GET /api/shipments/delays`, `GET /api/shipments/user/{username}`
- **Customs:** `POST /api/customs/declarations`, `PUT /api/customs/declarations/{decNum}/review`, `GET /api/customs/compliance/{trackingNumber}`, `GET /api/customs/declarations/pending`, `GET /api/customs/declarations/deadlines`
- **Vendors:** `POST /api/vendors`, `GET /api/vendors`, `POST /api/vendors/{vendorCode}/evaluate`, `GET /api/vendors/{vendorCode}/scorecard`, `POST /api/vendors/assign`
- **Routes:** `GET /api/routes/optimize`, `GET /api/routes/compare`
- **Stateful Booking:** `POST /api/booking/start`, `POST /api/booking/items`, `DELETE /api/booking/items/{sku}`, `POST /api/booking/carrier`, `GET /api/booking/summary`, `POST /api/booking/confirm`, `POST /api/booking/cancel`
- **Batch Logistics:** `POST /api/batch/dispatch`, `POST /api/batch/manifest`
- **Monitoring:** `GET /api/monitoring/status`, `GET /api/monitoring/alerts`, `PUT /api/monitoring/alerts/{id}/acknowledge`, `GET /api/monitoring/metrics`
- **Inventory:** `GET /api/inventory`, `GET /api/inventory/{sku}`, `GET /api/inventory/low-stock`, `POST /api/inventory`, `POST /api/inventory/{sku}/restock`

---

## 7. Documentation Files
- [`TECHNICAL_IMPLEMENTATION_DOCUMENTATION.md`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/TECHNICAL_IMPLEMENTATION_DOCUMENTATION.md): Comprehensive architectural designs, EJB session beans, persistence architecture, interceptors, timer services, DTO specifications, frontend architecture, and transaction isolation.
- [`CRITICAL_ANALYSIS_AND_TEST_REPORT.md`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/CRITICAL_ANALYSIS_AND_TEST_REPORT.md): Critical analysis (EJB vs Spring Boot vs Microservices), architectural trade-offs, and automated unit test execution report (16 passing test suites).
