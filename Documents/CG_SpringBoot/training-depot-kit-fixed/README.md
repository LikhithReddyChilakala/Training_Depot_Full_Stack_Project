# Training Depot — Fitness Gear E-Commerce

A Spring Boot + JSP + MySQL fitness-equipment storefront, built on the same
architectural foundation as the uploaded `Electronics.zip` reference project
(Spring MVC → Service → Repository → JPA/Hibernate → MySQL, session-based
auth, Razorpay payments, JavaMailSender emails), transformed into the
"Training Depot" domain and visual identity described in the brief.

## Running it locally

**Requirements:** Java 17, Maven, MySQL running locally on port 3306 with a
`root` user whose password is `admin` (or edit `application.properties` to
match whatever your local MySQL is actually set up with).

1. Nothing to create by hand — `createDatabaseIfNotExists=true` in
   `application.properties` creates the `fitness_gear` schema on first
   connect.
2. Razorpay and mail credentials are still placeholders (never real secrets
   in the checked-in file) and are read from environment variables if set:
   ```
   RAZORPAY_KEY_ID=rzp_test_xxxxxxxx
   RAZORPAY_KEY_SECRET=xxxxxxxxxxxxxxxx
   MAIL_USERNAME=you@gmail.com
   MAIL_APP_PASSWORD=xxxxxxxxxxxxxxxx
   ```
   (A Gmail *app password*, not your normal password, if using Gmail SMTP.)
   Checkout and registration both work without these — payment simply can't
   reach real Razorpay, and emails just get logged as a warning instead of
   sent, per `EmailService`'s best-effort design.
3. `mvn spring-boot:run`, then open `http://localhost:4250/`.

On first run, `DataSeeder` creates one admin account —
`kadali@fitness.com` / `wanderlust18013` (override via
`depot.seed.admin-username` / `depot.seed.admin-password` in
`application.properties`; change the password after first login in any real
deployment) — and a starter catalog of ~17 equipment records across all
eight categories, using the repositories directly rather than a
hand-written `data.sql` (which is fragile against Hibernate's generated
`@ElementCollection` table for specifications).

## What couldn't be verified here

This project was written in a sandboxed container with **no network access**
— there's no Maven Central to resolve dependencies from, no MySQL server to
connect to, and no real Razorpay/SMTP endpoints to call. Every file was
written and reviewed carefully (package/path consistency, brace balance,
import correctness, and the full request→service→repository chain were all
traced by hand), but it has **not gone through an actual `mvn package` or a
live checkout run**. Please run a normal build on your machine as the first
step:
```
mvn clean verify
```
and open an issue with the stack trace if anything doesn't compile — the
architecture is straightforward enough that any miss should be a small,
local fix.

## Architecture

```
Controller → Service (interface) → ServiceImpl → Repository → JPA → MySQL
```

- **Entities**: `User`, `Product`, `Order`, `OrderItem`, `Payment`. No
  `Brand`, no product-variant model, no `Category` table — category is a
  fixed `EquipmentCategory` enum on `Product`, and specifications are a
  `Map<String,String>` via `@ElementCollection`, matching the brief.
- **KIT**: session-only (`Kit` holds `productId → quantity`, nothing else).
  `KitService.view()` re-resolves it against the live database on every
  read, so price/stock changes are reflected immediately and nothing stale
  ever reaches checkout.
- **Checkout/payment** (`OrderServiceImpl`): starting checkout creates a
  `PENDING` order + `CREATED` payment and opens a Razorpay order — stock is
  untouched. `verifyAndFinalize` is one `@Transactional` method that verifies
  the signature, re-checks stock **under a pessimistic row lock**
  (`findByIdForUpdate`), and only then reduces stock and marks the order
  `CONFIRMED` / payment `SUCCESS`. Any failure path (bad signature, order-id
  mismatch, sold out since checkout) marks the order `CANCELLED` and never
  touches stock. This is the one rule the brief called non-negotiable, so it
  has the most test coverage (`OrderServiceImplTest`).
- **Auth**: session-based, `AuthInterceptor` / `AdminInterceptor` centralize
  the login/role checks that were duplicated inline across controllers in
  the reference project. Passwords are BCrypt-hashed
  (`spring-security-crypto` only — no Spring Security filter chain).

## Deliberate differences from the reference project

The reference (`Electronics.zip`) was useful as an architectural baseline,
but a close read turned up several things worth fixing rather than porting
forward:

- Stock was **never actually reduced anywhere** in the reference — this
  project adds the full check-lock-reduce flow described above.
- The `Payment` entity/table existed but was **never written to**; this
  project actually persists the full Razorpay audit trail (order id, payment
  id, signature, status, timing).
- Prices were `double`; here they're `BigDecimal` with `DECIMAL(10,2)`
  columns throughout.
- Passwords were compared in plaintext; here they're BCrypt-hashed.
- Image uploads used the client-supplied filename directly (path-traversal
  and overwrite risk); here every upload gets a generated UUID filename
  behind an extension allow-list.
- `/users` (list/edit/delete any account) had **no auth check at all** in
  the reference. Rather than re-exposing that, this project keeps
  self-registration (public sign-up → `USER` role) but drops the unrestricted
  admin user-management panel, since the brief's admin capability list
  doesn't call for one.
- `Brand`, `Category`, and `Review` entities existed but were either dead
  code or explicitly out of scope for this brief — all three are gone.

## Frontend

Custom CSS design system (`css/tokens.css` → `base.css` → `grid.css` →
`components.css` → `transitions.css`), no framework. IBM Plex Sans/Mono,
a warm grayscale palette with one accent ("marker" orange), a 12/8/4-column
structural grid, and a small FLIP-based transition (`js/grid-transition.js`)
that carries `data-grid-id` header/footer/main regions across full page
loads instead of a plain fade. Equipment glyphs are hand-drawn inline SVG
line icons per category rather than product photography (photo upload is
still supported for admins who want it).

## Tests

`mvn test` runs `ProductServiceImplTest`, `KitServiceImplTest`, and
`OrderServiceImplTest` — pure Mockito unit tests (no Spring context, no
database) focused on the stock/payment correctness rules rather than a large
generic suite.
