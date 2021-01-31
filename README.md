# API Read Cache
GitHub API read caching service, which periodically caches configured endpoints and flattens paginated responses from them into a single response payload. API endpoints outside of the configured set are proxied through the service directly to GitHub. API token can optionally be used to overcome API rate-limit restrictions.

The following custom views are also calculated and cached for the organization repositories:

- `/view/top/N/forks`
- `/view/top/N/last_updated`
- `/view/top/N/open_issues`
- `/view/top/N/stars`

### Assumptions and design decisions
- Cached data always fits in RAM (a generic abstraction is used in the code, so the default in-memory implementation can be easily replaced with external cache calls)
- Healthcheck should fail on startup until the required data is fully cached and views are calculated. Healthcheck also fails if Github API is not available
- All relevant configuration settings except Github auth token are specified in `resources/application.yml`. That includes cache refresh frequency (default is 5 min), cached endpoints, max page size, github url, etc
- The service is implemented in Kotlin, using Spring Boot framework

### Build, test and execute

```bash
./mvnw clean package
SERVER_PORT=<port> GITHUB_API_TOKEN=<secret> java -jar target/cache.jar
```
