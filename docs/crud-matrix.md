# CRUD matrix

Which entity each function creates (C), reads (R), updates (U) or deletes (D).
Bold is the entity the function mainly works on; plain letters are entities it only reads.

| Function | Volunteer | Event | Registration | Ticket | LookupItem |
|---|---|---|---|---|---|
| Add volunteer | **C** | | | | |
| List volunteers | **R** | | | | |
| Edit volunteer | **U** | | | | |
| Delete volunteer | **D** | | | | |
| Add skill to volunteer | **U** | | | | R |
| Set availability | **U** | | | | |
| Match volunteers by skill or availability | R | | | | |
| Create event | | **C** | | | R |
| List events | | **R** | | | |
| Edit event | | **U** | | | |
| Delete event | | **D** | | | |
| Assign volunteer to event | R | **U** | | | |
| Suggest volunteers for event | R | R | | | |
| Register volunteer for event | R | R | **C** | | |
| List registrations | | R | **R** | | |
| Issue ticket | R | R | R | **C** | |
| List tickets | | R | | **R** | |
| Check in (attendance) | | | | **U** | |
| Add lookup entry | | | | | **C** |
| List lookup entries | | | | | **R** |
| Edit lookup entry | | | | | **U** |
| Delete lookup entry | | | | | **D** |

| Entity | Create | Read | Update | Delete |
|---|---|---|---|---|
| Volunteer | yes | yes | yes | yes |
| Event | yes | yes | yes | yes |
| Registration | yes | yes | no | cascades with its event |
| Ticket | yes | yes | check-in only | cascades with its event |
| LookupItem | yes | yes | yes | yes |
