# Analysis UI

An Angular front end for the Java analysis GraphQL API. Angular 22, NG-ZORRO 22,
Chart.js and D3, zoneless, signals throughout, no state library.

## Run it

Requires Node ≥ 22.22.3 (Angular 22's floor).

```bash
npm install
npm run mock     # dependency-free fake backend on :8080, in a second terminal
npm start        # http://localhost:4200
```

`npm run mock` exists so you can look at the UI before wiring the real backend.
It serves deterministic fake data for the GraphQL queries and implements all
three REST commands — including walking a scheduled analysis through PENDING,
RUNNING and COMPLETED over about sixteen seconds so the polling can be seen
working — on the same port Spring Boot uses, so switching to the real thing means stopping it and starting
your backend — nothing in the app changes.

## Why these libraries

**NG-ZORRO** rather than PrimeNG. PrimeNG went commercial at v22 (repo archived
June 2026), and Ant Design is the closest match to what this app actually is:
dense, tabular, enterprise tooling. The compact theme variant is loaded, so
density comes from the library instead of being fought for with overrides. Its
table — sorting, filtering, pagination, virtual scroll — carries most of the
app's interaction.

The rest of the interface is built from four small components in
`shared/ui.components.ts` against the token layer in `styles/_tokens.scss`, so
the result reads as its own thing rather than stock Ant Design.

**No Apollo.** The app issues one snapshot query and a few lazy detail queries.
A normalised cache would add a second state model without saving a request, so
`api/graphql.client.ts` is twenty lines over `HttpClient`, and caching lives in
the store keyed by id.

**D3 by submodule** (`d3-array`, `d3-axis`, `d3-scale`, `d3-selection`) rather
than the umbrella package, which is roughly 250 kB for four functions.

## Workspaces

The landing view is workspace-oriented, because the three write operations form
a chain rather than a menu: create a workspace pointing at a directory, let the
backend locate its projects, then analyse them. Those are the REST controllers,
not GraphQL — `api/rest.client.ts` holds everything that changes server state
and `api/graphql.client.ts` everything that only reads.

| Action | Call |
|---|---|
| Create workspace | `POST /workspaces` with name, path, excludedPaths, basePackage |
| Locate projects | `POST /workspaces/{id}` |
| Schedule analysis | `POST /projects/{id}` |

Scheduling an analysis starts a poll: one timer, five second interval, stopped
on the first tick that finds nothing in progress, with a hard cap of ten minutes
so a status that never leaves RUNNING cannot leave requests firing in the
background for the rest of the session.

### One thing to check

The `WORKSPACES` document in `src/app/api/queries.ts` is **inferred**, not taken
from your schema — the `schema.graphqls` I was given had no workspace types. The
field names come from your `CreateWorkspaceRequest` record. If your query is
named differently the app does not break: it shows a warning and falls back to
the flat project list, and the write actions keep working, since those are REST.
Correcting the document in that one file is the whole fix.

### Route and REST path collision

The backend maps REST to `/projects` and `/workspaces`, which are also Angular
route prefixes. In dev both are on port 4200, so `proxy.conf.mjs` separates them
by intent: a GET asking for HTML is a browser navigating and gets the app,
anything else is the app calling the API and gets proxied. In production there
is no collision, because the app is served under `/ui/`. Moving the controllers
under `/api/...` would remove the overlap outright and is the cleaner long-term
fix.

## Structure

```
src/app/
  api/            GraphQL client (reads), REST client (writes), queries, types
  state/          One signal store: snapshot, lazy classes, lazy class detail
  shared/         Rating scale, formatting, four UI primitives
  charts/         Main sequence (D3) + three Chart.js charts
  views/          Workspaces, shell, project overview, package detail, class detail
```

### The two decisions worth knowing

**One snapshot, derived views.** The API has no filtering, sorting or
pagination, so every ranked list is computed client-side from a project
snapshot fetched once. That is the trade the whole app rests on: one round trip,
then instant interaction. Classes load lazily per package, because pulling them
into the snapshot would multiply the payload by two orders of magnitude for data
most sessions never open.

**Colour belongs to the data.** The chrome is near-monochrome slate and the one
interactive accent is a cool desaturated teal, deliberately outside the warm
rating ramp so it can never be misread as a metric. `shared/ratings.ts` projects
all five schema enums onto a single five-step concern scale, so a user learns
the colours once.

One place that is not mechanical: `RelationalCohesionRating` is not ordinal.
`GOOD` is the target, `LOW` means under-cohesive and `HIGH` means over-coupled,
so `HIGH` gets the caution tone rather than the best one. Colouring it as "best"
would actively mislead.

## Deploying alongside the backend

Development proxies `/graphql` to `localhost:8080` via `proxy.conf.json`. The
endpoint is relative everywhere, so one build works in dev, from the Spring Boot
jar, and behind any reverse proxy — no environment files.

To build into the backend jar:

```bash
npm run build:spring   # → ../backend/src/main/resources/static/ui
```

Spring will 404 on deep links like `/ui/projects/1/packages/7`, because no such
resource exists. Forward unmatched paths to the shell:

```java
@Controller
class SpaForwardController {

  @GetMapping("/ui/{path:[^\\.]*}")
  String forward() {
    return "forward:/ui/index.html";
  }

  @GetMapping("/ui/{path:^(?!assets).*}/**/{rest:[^\\.]*}")
  String forwardNested() {
    return "forward:/ui/index.html";
  }
}
```

The `[^\\.]*` guard means anything with a dot (`.js`, `.css`, `.woff2`) still
resolves as a static file. Test a hard refresh on a nested route before calling
it done — it is the classic thing that works in dev and breaks in production.

Font inlining is disabled in the production configuration so the build has no
network dependency, which matters for CI and air-gapped builds. Fonts load from
Google Fonts at runtime; self-host them in `src/assets` if that is not
acceptable in your environment.

## Types

`src/app/api/schema.types.ts` is hand-written so the project builds with no
codegen step, but the shapes match what `graphql-codegen` emits from
`schema.graphqls`. To generate them instead:

```bash
npm i -D @graphql-codegen/cli @graphql-codegen/typescript
npx graphql-codegen
```

Every scalar in the schema is nullable, so the model says so and `shared/format.ts`
centralises the coalescing. If you tighten the schema to `Int!` / `String!`,
most of the `?? 0` disappears.

## What is not built

Deliberately left out, in rough order of value:

- **Dark mode.** The token layer is ready for it; it needs a `[data-theme]`
  scope and NG-ZORRO's dark stylesheet.
- **A treemap of packages by lines of code.** The best "whole system at once"
  view, about thirty lines with `d3-hierarchy`.
- **Cross-package class views** (project-wide hotspots, author knowledge map).
  These need `classesByProject` on the backend; doing it client-side means one
  request per package.
- **Tests.** No test setup is wired up at all.
