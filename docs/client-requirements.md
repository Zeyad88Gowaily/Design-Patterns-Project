# Client requirements

Requirements collected from users, on top of the basic requirements in the assignment brief.
Each one is traced to the class or pattern that implements it.

## Donations

| # | Requirement | Priority | Implemented by |
|---|---|---|---|
| R1 | Donors get an automatic receipt after each donation | High | `DonationReceiptObserver` (Observer) |
| R2 | Gift Aid (+25%) for UK donors | Medium | `GiftAidDecorator` |
| R3 | Corporate matching fund (amount doubled) | Medium | `MatchingFundDecorator` |
| R4 | Recurring monthly donations | High | `RecurringDecorator` |
| R5 | Donation types can be changed without code changes | Medium | `DonationFactory` + DONATION_TYPE lookup |
| R6 | Year-end tax document | Low | Phase 2 |

## Volunteers and events

| # | Requirement | Priority | Implemented by |
|---|---|---|---|
| R7 | Volunteers state the days they are available | High | `Volunteer.availability` |
| R8 | Match volunteers to events by skill | High | `VolunteerService.matchBySkill`, `EventService.suggestVolunteers` |
| R9 | Track volunteer hours for certificates | Medium | Phase 2 |
| R10 | Event registration with tickets | High | `Registration`, `Ticket` |
| R11 | Check-in at the event | Medium | `Ticket.checkIn`, `EventService.markAttendance` |
| R12 | Volunteers are told when an event is created or updated | High | `EventUpdateObserver` (Observer) |

## Beneficiaries and communication

| # | Requirement | Priority | Implemented by |
|---|---|---|---|
| R13 | Several notification channels (email, SMS, social media) | High | `NotificationStrategy` and its three implementations |
| R14 | The user chooses the channel | Medium | Notifications tab, donor receipt channel |
| R15 | Record what help each beneficiary needs | High | `Beneficiary.need`, NEED_TYPE lookup |
| R16 | Manage skills, event types, donation types and need types | Medium | Lookup tables (`LookupService`) |
| R17 | Graphical interface | High | `ControlPanelView` (Swing) |
| R18 | Track resources given to beneficiaries | Low | Phase 2 |
