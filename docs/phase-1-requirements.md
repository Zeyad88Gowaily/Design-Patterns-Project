# Phase 1 requirements and acceptance evidence

This traceability record is based on the project brief and requirements
provided by the requesting user. It does not claim that an external nonprofit
representative was interviewed. Organization-specific requirements should be
confirmed with the intended nonprofit client before deployment.

## Collected requirements

| ID | Source | Requirement | Acceptance evidence |
|---|---|---|---|
| UR-01 | Assessment brief | Support core nonprofit work: donations, volunteers, events, beneficiaries, and communication. | The Swing control panel exposes these management areas; services implement their workflows. |
| UR-02 | Requesting user | Preserve the working user interface while completing phase-one needs. | The existing Swing tabs remain available after authentication. |
| UR-03 | Requesting user | Connect the application to Supabase for persistent storage. | The SQL schema defines application tables, foreign keys, and RLS; repositories use authenticated PostgREST. |
| UR-04 | Requesting user | Provide login and signup. | Email/password authentication, signup confirmation handling, token refresh, and sign-out are implemented. |
| UR-05 | Requesting user and assessment brief | Provide a full class diagram that reflects the code and includes implemented design patterns. | The PlantUML diagram includes attributes, pattern roles, and connected class relationships; its names were checked against top-level source declarations. |
| UR-06 | Requesting user | Check phase-one completion against the official assessment rubric. | The rubric and evidence matrix below maps each scored phase-one item to implementation. |
| UR-07 | Assessment brief | Use interchangeable communication strategies for different recipients. | Each donor stores an email/SMS preference and donation receipt notifications use the selected strategy. |

## Phase 1 scoring matrix

| Rubric item | Points | Status | Evidence |
|---|---:|---|---|
| Class diagram | 2 | Implemented | [`class-diagram.puml`](./class-diagram.puml) covers classes, key attributes, multiplicities, and pattern relationships. |
| Strategy pattern | 1 | Implemented | `NotificationStrategy`, `EmailStrategy`, `SmsStrategy`, `SocialMediaStrategy`, and `Notifier`. |
| Observer pattern | 1 | Implemented | `Subject` / `Observer`; donation and event services notify their observers. |
| Decorator pattern | 1 | Implemented | `DonationInfo`, `DonationDecorator`, and concrete donation decorators. |
| Factory pattern | 0.5 | Implemented | Donation and notification factories construct configured types. |
| Singleton pattern | 0.5 | Implemented | `DataStore` has a single configured instance. |
| MVC | 2 | Implemented | Console and Swing views call controllers; the controllers coordinate with services. |
| User control panel | 1 | Implemented | Swing tabs cover the main management areas and lookup administration. |
| Client requirements | 0.5 | Captured from supplied user brief | UR-02 through UR-06 record the requesting user's stated needs. No external interview is represented as having taken place. |
| CRUD lookup tables | 0.5 | Implemented | Lookup administration supports add, list, edit, and delete across lookup categories. |
| **Total** | **10** | **Evidence present for all listed items** | Assessment is based on source, tests, and supplied requirements; final grading remains with the course evaluator. |

## Requirements to confirm with the nonprofit client

The assessment brief does not specify organization-specific policies. Before
deploying for an actual nonprofit, confirm at least:

- Which staff roles may see or change donor and beneficiary personal data.
- Required retention, consent, and deletion rules for personal information.
- Whether additional notification channels, languages, or delivery records
  are required.
- Which reports, donation classifications, and volunteer approval workflows
  the organization needs.

These are open elicitation questions, not assumptions implemented as policy.
