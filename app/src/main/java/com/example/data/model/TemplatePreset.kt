package com.example.data.model

data class TemplatePreset(
    val id: String,
    val name: String,
    val category: String,
    val iconName: String,
    val description: String,
    val keyPrefix: String,
    val defaultSections: List<TemplateSection>,
    val defaultRequirements: List<TemplateRequirement>,
    val defaultUseCases: List<TemplateUseCase>
)

data class TemplateSection(
    val sectionNumber: String,
    val title: String,
    val content: String,
    val guidancePrompt: String
)

data class TemplateRequirement(
    val reqId: String,
    val title: String,
    val statement: String,
    val type: String,
    val priority: String,
    val acceptanceCriteria: String,
    val verificationMethod: String,
    val linkedUseCases: String
)

data class TemplateUseCase(
    val useCaseId: String,
    val title: String,
    val primaryActor: String,
    val preConditions: String,
    val mainSuccessScenario: String,
    val alternateFlows: String,
    val postConditions: String,
    val linkedRequirementIds: String
)

object TemplateCatalog {
    val presets: List<TemplatePreset> = listOf(
        TemplatePreset(
            id = "template_academic",
            name = "Academic Software Engineering Capstone",
            category = "Academic & Student",
            iconName = "School",
            description = "Standard IEEE 830-1998 format designed for university final-year projects, grading rubrics, and formal design defenses.",
            keyPrefix = "CAP",
            defaultSections = listOf(
                TemplateSection(
                    sectionNumber = "1.0",
                    title = "Introduction",
                    content = "This document specifies the software requirements for the Capstone Engineering project. It outlines functional behavior, user interfaces, system constraints, and verification protocols.",
                    guidancePrompt = "Describe the purpose and goals of the university project."
                ),
                TemplateSection(
                    sectionNumber = "1.1",
                    title = "Purpose and Project Scope",
                    content = "The purpose of this software system is to provide an automated, algorithmic solution for distributed resource management among academic labs. The scope encompasses web client, backend REST services, and database persistence.",
                    guidancePrompt = "Specify what the project will and will not accomplish."
                ),
                TemplateSection(
                    sectionNumber = "2.0",
                    title = "Overall Description & User Classes",
                    content = "The software operates in an academic campus network. Primary user classes include Student Researchers, Faculty Advisors, and System Administrators. All interactions are logged for audit compliance.",
                    guidancePrompt = "Define operating environment, user classes, and system constraints."
                ),
                TemplateSection(
                    sectionNumber = "3.0",
                    title = "External Interface Requirements",
                    content = "3.1 User Interfaces: Responsive web interface conforming to WCAG 2.1 AA standards.\n3.2 Software Interfaces: OAuth 2.0 University Single Sign-On and PostgreSQL 16 database.",
                    guidancePrompt = "Detail UI, API, database, and hardware communication interfaces."
                ),
                TemplateSection(
                    sectionNumber = "4.0",
                    title = "Verification & Quality Control",
                    content = "All functional requirements shall be verified through automated JUnit and integration tests. Non-functional performance metrics shall be benchmarked using simulated concurrent client traffic.",
                    guidancePrompt = "Describe testing methodologies, acceptance gates, and grading criteria."
                )
            ),
            defaultRequirements = listOf(
                TemplateRequirement(
                    reqId = "CAP-F-001",
                    title = "University SSO Authentication",
                    statement = "The system shall authenticate students and faculty using the campus OAuth 2.0 Identity Provider under all standard network conditions.",
                    type = "Functional",
                    priority = "Must Have",
                    acceptanceCriteria = "Given valid campus credentials, when user clicks Sign-In, then the system authenticates within 1.5 seconds and returns a valid JWT session.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-001"
                ),
                TemplateRequirement(
                    reqId = "CAP-F-002",
                    title = "Lab Equipment Booking Conflict Detection",
                    statement = "The system shall detect and reject overlapping timeslot bookings for identical lab apparatus under concurrent user submissions.",
                    type = "Functional",
                    priority = "Must Have",
                    acceptanceCriteria = "When two users request the same equipment for the same slot, the first transaction commits and the second receives HTTP 409 Conflict with an explanatory message.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-002"
                ),
                TemplateRequirement(
                    reqId = "CAP-NF-001",
                    title = "System Response Latency",
                    statement = "The system shall respond to 95% of read queries within 250 milliseconds under a concurrent workload of up to 100 active users.",
                    type = "Performance",
                    priority = "Should Have",
                    acceptanceCriteria = "Benchmark test with 100 concurrent simulated requests records p95 latency under 250ms over a 15-minute load cycle.",
                    verificationMethod = "Analysis",
                    linkedUseCases = "UC-001, UC-002"
                )
            ),
            defaultUseCases = listOf(
                TemplateUseCase(
                    useCaseId = "UC-001",
                    title = "Authenticate via SSO",
                    primaryActor = "Student Researcher",
                    preConditions = "User possesses active university credentials.",
                    mainSuccessScenario = "1. User navigates to portal.\n2. User redirects to University SSO.\n3. User authorizes access.\n4. Portal establishes authenticated dashboard.",
                    alternateFlows = "3a. Invalid credentials: SSO displays error; user returned to login.",
                    postConditions = "User session token active in local storage.",
                    linkedRequirementIds = "CAP-F-001"
                ),
                TemplateUseCase(
                    useCaseId = "UC-002",
                    title = "Reserve Lab Apparatus",
                    primaryActor = "Student Researcher",
                    preConditions = "User is authenticated and has approved project status.",
                    mainSuccessScenario = "1. User selects equipment from catalog.\n2. User chooses open time window.\n3. User submits reservation request.\n4. System locks slot and sends confirmation.",
                    alternateFlows = "3a. Slot taken concurrently: System returns error and offers next available slots.",
                    postConditions = "Reservation record stored in database with pending status.",
                    linkedRequirementIds = "CAP-F-002"
                )
            )
        ),
        TemplatePreset(
            id = "template_saas",
            name = "SaaS Web Application & Dashboard",
            category = "Web Application",
            iconName = "Web",
            description = "Multi-tenant B2B SaaS platform specification with role-based access control, analytics pipelines, Stripe billing, and API endpoints.",
            keyPrefix = "SAS",
            defaultSections = listOf(
                TemplateSection(
                    sectionNumber = "1.0",
                    title = "Introduction & Executive Summary",
                    content = "This document defines the Software Requirements Specification for the CloudAnalytics SaaS Platform. It establishes architectural boundaries, data flow pipelines, user experience standards, and operational service-level agreements.",
                    guidancePrompt = "Summarize the SaaS product vision, market problem, and value proposition."
                ),
                TemplateSection(
                    sectionNumber = "2.0",
                    title = "Product Overview & Tenant Isolation",
                    content = "The platform delivers multi-tenant business telemetry. Each tenant possesses isolated data partitions. The solution comprises a React web front-end, Go/Kotlin microservices, and PostgreSQL with Row-Level Security.",
                    guidancePrompt = "Specify tenant boundaries, supported browsers, and cloud topology."
                ),
                TemplateSection(
                    sectionNumber = "3.0",
                    title = "Functional Requirements",
                    content = "Core capabilities include automated data ingestion, real-time query rendering, customizable dashboard widgets, scheduled PDF reporting, and role-based workspace permissions.",
                    guidancePrompt = "Detail administrative, tenant, and analytical features."
                ),
                TemplateSection(
                    sectionNumber = "4.0",
                    title = "Non-Functional & Security Requirements",
                    content = "System availability shall meet 99.9% uptime SLA. All communication requires TLS 1.3 encryption. Tenant data is isolated with cryptographic keys and strict database policies.",
                    guidancePrompt = "Specify SLA, security, encryption, and scalability limits."
                )
            ),
            defaultRequirements = listOf(
                TemplateRequirement(
                    reqId = "SAS-F-001",
                    title = "Multi-Tenant Workspace Provisioning",
                    statement = "The system shall provision an isolated organization workspace with dedicated schema within 5 seconds of account registration under normal system operation.",
                    type = "Functional",
                    priority = "Must Have",
                    acceptanceCriteria = "Given a completed sign-up form, when submission completes, a new tenant schema and admin account exist and the user is redirected to onboarding.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-SAS-01"
                ),
                TemplateRequirement(
                    reqId = "SAS-F-002",
                    title = "Role-Based Access Control (RBAC)",
                    statement = "The system shall enforce RBAC permissions (Owner, Admin, Editor, Viewer) on all document read and mutation API endpoints under all client requests.",
                    type = "Security",
                    priority = "Must Have",
                    acceptanceCriteria = "A user with 'Viewer' role receiving a mutation request receives HTTP 403 Forbidden with audit logging.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-SAS-02"
                ),
                TemplateRequirement(
                    reqId = "SAS-NF-001",
                    title = "High Availability and Fault Tolerance",
                    statement = "The system shall maintain 99.9% monthly service uptime excluding scheduled maintenance windows under production hosting infrastructure.",
                    type = "Non-Functional",
                    priority = "Must Have",
                    acceptanceCriteria = "Monthly telemetry logs confirm less than 43.8 minutes of unplanned downtime.",
                    verificationMethod = "Analysis",
                    linkedUseCases = "UC-SAS-01, UC-SAS-02"
                )
            ),
            defaultUseCases = listOf(
                TemplateUseCase(
                    useCaseId = "UC-SAS-01",
                    title = "Workspace Onboarding",
                    primaryActor = "Organization Admin",
                    preConditions = "User possesses a verified corporate email.",
                    mainSuccessScenario = "1. Admin enters company details and chooses subscription tier.\n2. System provisions tenant workspace.\n3. Admin invites initial team members.\n4. System sends magic link invitations.",
                    alternateFlows = "1a. Company domain already registered: System suggests joining existing tenant.",
                    postConditions = "Tenant record active; trial period initiated.",
                    linkedRequirementIds = "SAS-F-001"
                ),
                TemplateUseCase(
                    useCaseId = "UC-SAS-02",
                    title = "Export Analytics Report",
                    primaryActor = "Financial Analyst",
                    preConditions = "User is logged in with Editor or higher role.",
                    mainSuccessScenario = "1. User sets date range and filters.\n2. User clicks Export PDF.\n3. Server renders report asynchronously.\n4. Download prompt triggers on client.",
                    alternateFlows = "3a. Job timeout: System emails download link when completed.",
                    postConditions = "Export event written to tenant audit ledger.",
                    linkedRequirementIds = "SAS-F-002"
                )
            )
        ),
        TemplatePreset(
            id = "template_mobile",
            name = "Mobile Banking & Fintech Application",
            category = "Mobile Application",
            iconName = "Smartphone",
            description = "High-security mobile financial specification covering biometric auth, peer-to-peer transfers, push notifications, and PCI-DSS compliance.",
            keyPrefix = "BNK",
            defaultSections = listOf(
                TemplateSection(
                    sectionNumber = "1.0",
                    title = "Scope & Regulatory Context",
                    content = "This document sets forth requirements for the ApexBank Mobile Application for iOS and Android. Compliant with PCI-DSS 4.0, GDPR, and open banking protocols.",
                    guidancePrompt = "Specify banking regulations, supported OS versions, and hardware capabilities."
                ),
                TemplateSection(
                    sectionNumber = "2.0",
                    title = "User Classes & Security Constraints",
                    content = "Users include Retail Customers and Wealth Management Clients. High security requires hardware keystore protection, biometric binding, and automatic session revocation.",
                    guidancePrompt = "Describe device requirements, biometrics, and threat model."
                ),
                TemplateSection(
                    sectionNumber = "3.0",
                    title = "Financial Transaction Requirements",
                    content = "Includes account ledger balance inspection, domestic instant P2P wire transfers, bill payments, and biometric payment authorization.",
                    guidancePrompt = "Detail transaction flows, limits, reversals, and statements."
                )
            ),
            defaultRequirements = listOf(
                TemplateRequirement(
                    reqId = "BNK-F-001",
                    title = "Biometric Step-Up Authorization",
                    statement = "The system shall require biometric fingerprint or face authentication for transfers exceeding $500 USD under all operational states.",
                    type = "Security",
                    priority = "Must Have",
                    acceptanceCriteria = "When a transfer value > $500 is initiated, the app invokes Android BiometricPrompt before executing the transaction payload.",
                    verificationMethod = "Demonstration",
                    linkedUseCases = "UC-BNK-01"
                ),
                TemplateRequirement(
                    reqId = "BNK-F-002",
                    title = "Real-Time Balance Invalidation",
                    statement = "The system shall update and reflect the cleared ledger balance within 500 milliseconds of transaction confirmation under connected network state.",
                    type = "Functional",
                    priority = "Must Have",
                    acceptanceCriteria = "Upon receipt of websocket confirmation event, the UI balance indicator updates without requiring manual pull-to-refresh.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-BNK-01"
                ),
                TemplateRequirement(
                    reqId = "BNK-NF-001",
                    title = "Data-at-Rest Hardware Encryption",
                    statement = "The system shall encrypt all cached transaction records using AES-256-GCM with keys backed by Android Keystore StrongBox under all device states.",
                    type = "Security",
                    priority = "Must Have",
                    acceptanceCriteria = "Verification through security audit confirms root inspection cannot extract plaintext ledger data.",
                    verificationMethod = "Inspection",
                    linkedUseCases = "UC-BNK-01"
                )
            ),
            defaultUseCases = listOf(
                TemplateUseCase(
                    useCaseId = "UC-BNK-01",
                    title = "Instant Peer-to-Peer Transfer",
                    primaryActor = "Retail Account Holder",
                    preConditions = "User has authenticated and has sufficient balance.",
                    mainSuccessScenario = "1. User selects recipient contact.\n2. User enters amount and note.\n3. User confirms with biometric prompt.\n4. Ledger processes transfer instantly and sends receipt.",
                    alternateFlows = "3a. Biometric failure: Fall back to 6-digit transaction PIN.",
                    postConditions = "Account debit and credit posted to core banking engine.",
                    linkedRequirementIds = "BNK-F-001, BNK-F-002"
                )
            )
        ),
        TemplatePreset(
            id = "template_api",
            name = "RESTful Microservices & Cloud Platform",
            category = "API & Backend",
            iconName = "Code",
            description = "Developer-first API specification containing OpenAPI schema compliance, rate limiting, token authentication, and data consistency models.",
            keyPrefix = "API",
            defaultSections = listOf(
                TemplateSection(
                    sectionNumber = "1.0",
                    title = "Architecture & Interface Design",
                    content = "This specification outlines the public and private interfaces for the CloudMesh Service Platform. Emphasizes idempotent operations, RESTful verbs, and JSON payloads.",
                    guidancePrompt = "Specify API design principles, versioning strategy, and telemetry."
                ),
                TemplateSection(
                    sectionNumber = "2.0",
                    title = "Authentication & Traffic Throttling",
                    content = "All requests mandate Bearer API token or mTLS. Throttling is applied per consumer organization using a Token Bucket algorithm backed by Redis.",
                    guidancePrompt = "Detail rate limits, quotas, auth headers, and response formats."
                )
            ),
            defaultRequirements = listOf(
                TemplateRequirement(
                    reqId = "API-F-001",
                    title = "Idempotent Transaction Execution",
                    statement = "The system shall process requests containing an Idempotency-Key header exactly once and return identical response representations on subsequent calls within 24 hours.",
                    type = "Functional",
                    priority = "Must Have",
                    acceptanceCriteria = "Submitting identical POST requests with key 'req_123' twice produces one database mutation and returns HTTP 200 with matching bodies.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-API-01"
                ),
                TemplateRequirement(
                    reqId = "API-NF-001",
                    title = "Token Bucket Rate Limiting",
                    statement = "The system shall return HTTP 429 Too Many Requests with a Retry-After header when an API key exceeds 1,000 requests per minute under sustained traffic.",
                    type = "Performance",
                    priority = "Must Have",
                    acceptanceCriteria = "Simulated benchmark exceeding 1,000 req/min receives HTTP 429 within 10ms with valid header.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-API-01"
                )
            ),
            defaultUseCases = listOf(
                TemplateUseCase(
                    useCaseId = "UC-API-01",
                    title = "Execute Cloud Batch Job",
                    primaryActor = "Developer Service Integration",
                    preConditions = "Valid API Key with scope 'compute.write'.",
                    mainSuccessScenario = "1. Client submits POST /v1/jobs with workload manifest.\n2. API validates schema and returns 202 Accepted with job ID.\n3. Client polls or subscribes to webhook for completion.",
                    alternateFlows = "1a. Invalid manifest: Return 400 Bad Request with field validation errors.",
                    postConditions = "Job queued in messaging broker.",
                    linkedRequirementIds = "API-F-001, API-NF-001"
                )
            )
        ),
        TemplatePreset(
            id = "template_health",
            name = "Hospital & Healthcare Information System",
            category = "Enterprise & Healthcare",
            iconName = "LocalHospital",
            description = "HIPAA and HL7 FHIR compliant clinical health record system specification with strict audit logging, patient consent, and triage modules.",
            keyPrefix = "MED",
            defaultSections = listOf(
                TemplateSection(
                    sectionNumber = "1.0",
                    title = "Clinical Scope & Statutory Compliance",
                    content = "This document defines the electronic health record (EHR) system for General Hospital. Enforces strict HIPAA privacy, audit tracking, and role-based physician access.",
                    guidancePrompt = "State clinical workflows, hospital departments, and compliance rules."
                ),
                TemplateSection(
                    sectionNumber = "2.0",
                    title = "Patient Record Confidentiality",
                    content = "Access to Protected Health Information (PHI) requires active medical relationship or declared emergency 'break-glass' protocol with automatic ethics notification.",
                    guidancePrompt = "Document emergency access protocols, patient consents, and audit trails."
                )
            ),
            defaultRequirements = listOf(
                TemplateRequirement(
                    reqId = "MED-F-001",
                    title = "Emergency Break-Glass Access Protocol",
                    statement = "The system shall permit emergency clinicians to override access restrictions to critical patient allergy history while recording an immutable audit event under emergency declared state.",
                    type = "Security",
                    priority = "Must Have",
                    acceptanceCriteria = "When 'Break Glass' is triggered, clinician enters reason, access is granted within 1 second, and chief medical officer receives automated email alert.",
                    verificationMethod = "Demonstration",
                    linkedUseCases = "UC-MED-01"
                ),
                TemplateRequirement(
                    reqId = "MED-F-002",
                    title = "Drug Allergy Interaction Warning",
                    statement = "The system shall alert the prescribing physician before order submission whenever a medication conflicts with recorded patient allergies under clinical review.",
                    type = "Functional",
                    priority = "Must Have",
                    acceptanceCriteria = "Prescribing penicillin to an allergic patient displays high-severity modal warning requiring explicit clinical rationale to proceed.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-MED-01"
                )
            ),
            defaultUseCases = listOf(
                TemplateUseCase(
                    useCaseId = "UC-MED-01",
                    title = "Clinical Medication Order",
                    primaryActor = "Attending Physician",
                    preConditions = "Physician is authenticated and assigned to patient case.",
                    mainSuccessScenario = "1. Physician selects patient chart.\n2. Physician selects medication and dosage.\n3. System checks interaction ledger.\n4. Order verified and routed to pharmacy.",
                    alternateFlows = "3a. Adverse interaction detected: Modal alerts doctor with alternative drug recommendations.",
                    postConditions = "Medication order saved to patient active treatment plan.",
                    linkedRequirementIds = "MED-F-001, MED-F-002"
                )
            )
        ),
        TemplatePreset(
            id = "template_ecommerce",
            name = "E-Commerce & Omnichannel Retail Platform",
            category = "Commercial Retail",
            iconName = "ShoppingCart",
            description = "High-volume retail platform specification detailing inventory synchronization, shopping cart checkout, shipping carrier APIs, and return workflows.",
            keyPrefix = "ECO",
            defaultSections = listOf(
                TemplateSection(
                    sectionNumber = "1.0",
                    title = "Commercial Product Scope",
                    content = "Defines the customer-facing digital storefront, payment gateways, and warehouse logistics fulfillment backend for OmniStore Global.",
                    guidancePrompt = "Detail market channels, fulfillment centers, and catalog sizing."
                ),
                TemplateSection(
                    sectionNumber = "2.0",
                    title = "Order Management & Inventory Integrity",
                    content = "Guarantees accurate real-time inventory counts across warehouse nodes, preventing stockout orders during high-demand promotional events.",
                    guidancePrompt = "Specify payment processors, tax calculations, and warehouse APIs."
                )
            ),
            defaultRequirements = listOf(
                TemplateRequirement(
                    reqId = "ECO-F-001",
                    title = "Atomic Cart Inventory Reservation",
                    statement = "The system shall hold inventory units for 15 minutes during active checkout to prevent overselling under high concurrency flash sales.",
                    type = "Functional",
                    priority = "Must Have",
                    acceptanceCriteria = "Items reserved in cart decrement available inventory counter immediately; if timer expires without checkout completion, units return to stock.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-ECO-01"
                ),
                TemplateRequirement(
                    reqId = "ECO-F-002",
                    title = "Multi-Currency Dynamic Conversion",
                    statement = "The system shall present prices in the customer's localized currency updated via live exchange rate feeds every 60 minutes under international browsing.",
                    type = "Functional",
                    priority = "Should Have",
                    acceptanceCriteria = "Switching currency from USD to EUR updates all catalog items with correct formatting and FX rate.",
                    verificationMethod = "Test",
                    linkedUseCases = "UC-ECO-01"
                )
            ),
            defaultUseCases = listOf(
                TemplateUseCase(
                    useCaseId = "UC-ECO-01",
                    title = "Complete Secure Checkout",
                    primaryActor = "Registered Customer",
                    preConditions = "Customer has items in cart and is logged in.",
                    mainSuccessScenario = "1. Customer reviews cart.\n2. Customer selects saved shipping address.\n3. Customer authorizes payment.\n4. System processes payment and generates order confirmation.",
                    alternateFlows = "3a. Payment declined: Retain cart and prompt for alternative payment method.",
                    postConditions = "Order created in pending fulfillment state.",
                    linkedRequirementIds = "ECO-F-001, ECO-F-002"
                )
            )
        )
    )
}
