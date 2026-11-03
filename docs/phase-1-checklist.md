# Phase 1 checklist

Marks from the assessment brief and where each item is implemented.

| Item | Marks | Where |
|---|---:|---|
| Class diagram | 2 | `diagrams/class-diagram-domain.png`, `class-diagram-patterns.png`, `class-diagram-layers.png` |
| Strategy | 1 | `NotificationStrategy`, `EmailStrategy`, `SmsStrategy`, `SocialMediaStrategy`, `Notifier`; the channel is chosen per donor and in the Notifications tab |
| Observer | 1 | `Subject`, `Observer`, `DonationReceiptObserver`, `EventUpdateObserver` |
| Decorator | 1 | `DonationInfo`, `DonationDecorator`, `GiftAidDecorator`, `MatchingFundDecorator`, `RecurringDecorator` |
| Factory | 0.5 | `DonationFactory`, `NotificationFactory` |
| Singleton | 0.5 | `DataStore` |
| MVC | 2 | Views (`ConsoleView`, `ControlPanelView`), controllers, services |
| User control panel | 1 | `ControlPanelView`: Dashboard, Donations, Volunteers, Events, Beneficiaries, Notifications, Lookup Tables |
| Client requirements | 0.5 | `client-requirements.md` |
| CRUD lookup tables | 0.5 | `LookupService`, Lookup Tables tab, `crud-matrix.md` |
| **Total** | **10** | |

## To confirm with the client before real use

- Which staff may see or change donor and beneficiary personal data.
- Retention, consent and deletion rules for personal data.
- Extra notification channels or delivery records.
- Required reports and volunteer approval steps.
