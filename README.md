# GlobalTrade Logistics — Enterprise Supply Chain Platform

**Platform Version:** Jakarta EE 10 / GlassFish 7.0.25  
**Architecture:** Enterprise Java Beans (EJB 3.1+) Multi-Module EAR  
**Coursework / Module:** BCD II  

---

## 1. Project Overview

GlobalTrade Logistics is a distributed enterprise logistics and supply chain coordination platform built with Jakarta EE 10, EJB 3.1+, JPA 3.1 (Hibernate), and JAX-RS RESTful web services. It provides core capabilities for cross-border freight forwarding, automated customs compliance, multimodal route optimization, stateful booking sessions, vendor performance evaluations, and automated background schedulers.

---

## 2. Multi-Module Project Structure

```
GlobalTrade Logistics
├── persistence/        # JPA 3.1 Entities, DAOs, and Persistence Services (JAR)
├── ejb-api/            # EJB Local interfaces (customs, shipment, vendor), DTOs, Exceptions (JAR)
├── ejb-security/       # Interceptors (Audit, Compliance, Performance, Validation) & JWT Security (EJB-JAR)
├── ejb-customs/        # Customs compliance EJB business logic (EJB-JAR)
├── ejb-shipment/       # Tracking, Fulfillment, Routing, Booking, Scheduling EJBs (EJB-JAR)
├── ejb-vendor/         # Vendor evaluation and KPI scorecard EJBs (EJB-JAR)
├── web/                # JAX-RS REST Controllers and Request/Response Models (WAR)
└── ear/                # Enterprise Archive packaging (globaltrade-logistics-ear.ear)
```

---

## 3. Key EJB Components & Capabilities

### 3.1 Stateless Session Beans (`@Stateless`)
- **`CustomsComplianceBean`** (`ejb-customs`): Electronic filing, tariff determination, customs approvals/holds.
- **`ShipmentTrackingBean`** (`ejb-shipment`): Tracking lifecycle, status transitions, ETA calculations, delay detection.
- **`OrderFulfillmentBean`** (`ejb-shipment`): Multi-phase order fulfillment, atomic inventory deduction, restock alerting.
- **`RouteOptimizationBean`** (`ejb-shipment`): Multimodal carrier evaluation (`COST`, `SPEED`, `ECO`, `RELIABILITY`).
- **`BatchLogisticsBean`** (`ejb-shipment`): Batch dispatch processing and cargo manifest generation.
- **`VendorEvaluationBean`** (`ejb-vendor`): Vendor SLA tracking, on-time delivery rate calculations, scorecard generation.

### 3.2 Stateful Session Beans (`@Stateful`)
- **`ShipmentBookingSessionBean`** (`ejb-shipment`): Preserves multi-step client booking cart conversationally across item addition, carrier selection, price estimation, and confirmation/cancellation (`@Remove`).

### 3.3 Singleton Session Beans (`@Singleton`, `@Startup`)
- **`SupplyChainMonitoringBean`** (`ejb-shipment`): Startup initialization, container-managed concurrency (`@Lock(READ)` / `@Lock(WRITE)`), KPI metrics, and alert monitoring.
- **`LogisticsSchedulerBean`** (`ejb-shipment`): Background periodic timers (`@Schedule`) for delay detection, customs deadline escalations, and midnight stock audits.

### 3.4 Interceptor Pipeline (`ejb-security`)
- **`AuditLoggingInterceptor`**: Automated audit trail logging of all business bean invocations.
- **`PerformanceMonitoringInterceptor`**: Method execution duration tracking and SLA telemetry.
- **`RegulatoryComplianceInterceptor`**: Real-time enforcement against sanctioned trade jurisdictions and high-value cargo verification.
- **`VendorValidationInterceptor`**: Blocks suspended vendors from dispatch operations.

---

## 4. Build & Deployment Instructions

### Prerequisites
- **Java Development Kit:** JDK 17+
- **Build Tool:** Apache Maven 3.9+
- **Application Server:** GlassFish 7.0.25+ (or compatible Jakarta EE 10 Application Server)
- **Database:** MySQL 8.0+ (Configured JNDI data source: `jdbc/globaltrade-logistics`)

### Build the Project
To compile, run all automated test suites, and package the complete enterprise archive:
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

All REST endpoints are available under the `/api` context path:

- **Authentication:** `POST /api/auth/login`, `POST /api/auth/register`, `POST /api/auth/refresh`
- **Shipments:** `POST /api/shipments`, `GET /api/shipments`, `GET /api/shipments/{trackingNumber}`, `PUT /api/shipments/{trackingNumber}/status`, `GET /api/shipments/delays`
- **Customs:** `POST /api/customs/declarations`, `PUT /api/customs/declarations/{decNum}/review`, `GET /api/customs/compliance/{trackingNumber}`, `GET /api/customs/declarations/pending`
- **Vendors:** `POST /api/vendors`, `GET /api/vendors`, `POST /api/vendors/{vendorCode}/evaluate`, `GET /api/vendors/{vendorCode}/scorecard`, `POST /api/vendors/assign`
- **Routes:** `GET /api/routes/optimize`, `GET /api/routes/compare`
- **Stateful Booking:** `POST /api/booking/start`, `POST /api/booking/items`, `DELETE /api/booking/items/{sku}`, `POST /api/booking/carrier`, `GET /api/booking/summary`, `POST /api/booking/confirm`, `POST /api/booking/cancel`
- **Batch Logistics:** `POST /api/batch/dispatch`, `POST /api/batch/manifest`
- **Monitoring:** `GET /api/monitoring/status`, `GET /api/monitoring/alerts`, `PUT /api/monitoring/alerts/{id}/acknowledge`, `GET /api/monitoring/metrics`
- **Inventory:** `GET /api/inventory`, `GET /api/inventory/{sku}`, `GET /api/inventory/low-stock`, `POST /api/inventory`, `POST /api/inventory/{sku}/restock`

---

## 6. Documentation Files
- [`TECHNICAL_IMPLEMENTATION_DOCUMENTATION.md`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/TECHNICAL_IMPLEMENTATION_DOCUMENTATION.md): In-depth architectural designs, EJB session beans, interceptors, timer services, and transactions.
- [`CRITICAL_ANALYSIS_AND_TEST_REPORT.md`](file:///D:/Projects/bcd%202/GlobalTrade%20Logistics/CRITICAL_ANALYSIS_AND_TEST_REPORT.md): Critical analysis (EJB vs Spring vs Microservices) and automated unit test execution report.
