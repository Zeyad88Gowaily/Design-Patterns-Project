# MissionOS

Non-profit management system (CSE336 major task, phase 1). Java 11, a Swing application with
a dashboard, light and dark themes, and a text-menu alternative. Data is stored in Supabase.

## Setup

1. Create a Supabase project and run `database/supabase-schema.sql` in its SQL editor.
2. Create a file named `.env` in this folder with the project URL and the public anon key
   (never the `service_role` key):

   ```
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=your-public-anon-key
   ```

   `.env` is git-ignored. Real environment variables override it.
3. Create a user: the app has a sign-up option on its login screen.

## Run

```powershell
.\run.ps1             # Swing control panel (run.bat does the same)
.\run.ps1 -Console    # console menu
mvn clean package     # build and run the tests
```

Maven and Java 11+ are required.

## Layout

```
src/nonprofit/
  model/          entities, donation types and decorators
  repository/     Repository, DataStore (singleton), Supabase access
  observer/       Subject, Observer
  communication/  notification strategies, factory, observers
  service/        business logic
  controller/     console and Swing controllers
  view/           console view, control panel, login dialogs
database/         Supabase schema and seed data
docs/             requirements, SRS, class diagrams
test/             JUnit tests
```

Flow: view -> controller -> service -> `DataStore` -> repository -> Supabase.

## Class diagrams

`docs/diagrams/class-diagram-full.png` (also `.svg`, zoomable) is the complete, connected Phase 1 diagram:
every class from the view down to the Supabase tables.

Three smaller diagrams show one part each:

- `class-diagram-domain.png`: entities, donation types and the Decorator add-ons
- `class-diagram-patterns.png`: Observer, Strategy and Factory
- `class-diagram-layers.png`: MVC layers, the Singleton `DataStore` and persistence

`database-tables.png` shows the Supabase tables. Each `.puml` file is the source of its picture.
