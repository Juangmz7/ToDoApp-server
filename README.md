# Documentation

- Spring application
- Fill .env_template and rename it to .env

### Code modifications:
- In order to generate a new .jar, first run "docker-compose up -d postgres"
- When .jar is generated, run "docker-compose down -v"
- After this, run "docker-compose up --build -d"

