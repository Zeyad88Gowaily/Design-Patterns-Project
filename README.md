# MissionOS

MissionOS is a Java Swing non-profit management application backed by Supabase.
All management screens are available to authenticated users; authorization is
enforced by Supabase Row Level Security, not by a key embedded in the app.

## Supabase setup

1. Create a Supabase project.
2. Open the Supabase SQL Editor and run [`database/supabase-schema.sql`](database/supabase-schema.sql).
   It creates the application tables, foreign keys, initial lookup data, and
   policies allowing authenticated users to manage records.
   Entity attributes are stored as JSONB payloads; IDs and relationship columns
   are relational and protected by foreign keys.
3. Configure the application environment. Use the project URL and its public
   anon/publishable key; never use the `service_role` secret in this desktop app.

PowerShell example:

```powershell
$env:SUPABASE_URL = "https://your-project.supabase.co"
$env:SUPABASE_ANON_KEY = "your-public-anon-or-publishable-key"
```

Set the environment variables for the same terminal session used to launch the
application. The app does not fall back to in-memory storage if configuration
is missing or Supabase is unavailable.

## Build and run

Requires Java 11 or newer and Maven.

```powershell
mvn clean package
mvn exec:java "-Dexec.mainClass=nonprofit.Main" "-Dexec.args=--gui"
```

The application prompts users to sign in or create an account before opening
the management UI. If Supabase email confirmation is enabled, new users must
confirm their email before signing in. The console interface remains available
by omitting `--gui` (authentication is still required).

## Design diagram

[`docs/class-diagram.puml`](docs/class-diagram.puml) shows the implemented
classes, their relationships, and the pattern roles reflected in source. Ideas
for phase 2 are marked as planned and are not represented as implemented
classes.

## Phase 1 coverage

- Supabase persistence is wired for donors, donations, volunteers, events,
  beneficiaries, lookup items, registrations, and tickets.
- Supabase email/password sign-in, signup, session refresh, and sign-out are
  included. The SQL policies grant record access to any authenticated user.
- The existing Swing management interface is retained and now includes
  authentication and donor email/SMS receipt preference.
- The class diagram includes every top-level source type and was checked
  against the code.
- The official phase-one rubric and requirement traceability are documented in
  [`docs/phase-1-requirements.md`](docs/phase-1-requirements.md).

The application has not been connected to a project-specific Supabase
instance as this repository does not include credentials. Apply the schema and
set the environment variables above to run the live integration.

The intended nonprofit client must confirm its organization-specific privacy,
retention, authorization, communication, and reporting requirements before
deployment; see the requirements traceability document.
