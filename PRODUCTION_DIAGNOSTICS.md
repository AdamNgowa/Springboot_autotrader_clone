# Production Diagnostics Runbook

## Purpose

Use this checklist when the production application appears slow, unavailable, or behaves unexpectedly.

Production architecture:

- React frontend → Vercel
- Spring Boot backend → Render
- PostgreSQL database → Neon

---

## 1. Check Render Service Status

Open the Render service dashboard and confirm:

- The service is deployed.
- The latest deployment completed successfully.
- The service is not repeatedly restarting.
- There are no obvious deployment or startup errors.

### Cold-start consideration

The Render free service can spin down after inactivity.

A first request may therefore take longer because:

1. Render wakes the backend container.
2. Spring Boot starts.
3. The application establishes database connectivity.
4. Neon may also need to become responsive.

Do not immediately treat a slow first request as an application failure.

---

## 2. Check Application Health

Call:

`GET /actuator/health`

Expected result:

```json
{
  "status": "UP"
}
```

The health endpoint is intentionally exposed without authentication so Render can use it for service health checks.

If health is not reachable:

- Check Render service status.
- Check the deployment/startup logs.
- Check whether the service is restarting.
- Check database connectivity errors.

---

## 3. Inspect Render Application Logs

Use Render's logs to investigate backend failures.

Look for:

- Spring Boot startup failures
- Bean initialization errors
- Database connection failures
- Authentication/configuration errors
- Unhandled application exceptions
- Repeated restarts
- Out-of-memory or container-level failures

The application writes logs to stdout. Render collects those logs, so the application does not depend on local log files.

### Security rule

Never paste or commit:

- `JWT_SECRET`
- `DB_PASSWORD`
- database connection credentials
- Authorization headers or JWTs
- `.env` contents
- uploaded/private user data

Production logs should not contain secrets or authentication tokens.

---

## 4. Check Neon When Database Errors Appear

If Render logs show database connectivity or PostgreSQL errors:

1. Check the Neon project/dashboard.
2. Verify the database is available.
3. Check for connection-related errors.
4. Compare the timing of the Neon issue with the Render application logs.

This helps distinguish:

**Spring Boot problem → database problem → network/dependency problem**

from an application-code failure.

---

## 5. If the Backend Is Healthy but the UI Fails

If `/actuator/health` reports `UP` but the frontend is failing:

### Vercel

Check:

- Latest deployment status
- Build/deployment errors
- Production environment variables

### Browser

Check the browser developer tools:

- Network requests
- HTTP status codes
- CORS errors
- Failed API requests
- Authentication/session errors

If the backend is healthy and the browser cannot reach it, investigate the frontend/API configuration rather than restarting the backend blindly.

---

## 6. Quick Decision Tree

| Observation | First place to investigate |
|---|---|
| Backend URL does not respond | Render service/deployment |
| First request is slow after inactivity | Render/Neon cold start |
| `/actuator/health` is `UP` | Backend is alive; investigate request/UI behavior |
| Health endpoint fails | Render logs and startup/database errors |
| PostgreSQL connection errors | Neon + Render environment configuration |
| Backend healthy, frontend broken | Vercel + browser Network/CORS |
| Repeated container restarts | Render logs/resource limits |
| Authentication suddenly fails | JWT configuration, frontend session, security logs |

---

## 7. Diagnostic Principle

Follow the dependency chain from outside to inside:

**Vercel → Render → Spring Boot → Neon**

Do not change configuration or restart services until the failing layer has been identified.

The goal is to diagnose the failure using observable evidence rather than guessing.
