# Documentation

- Spring application
- Fill .env_template and rename it to .env

### Code modifications:
- In order to generate a new .jar, first run "docker-compose up -d postgres"
- When .jar is generated, run "docker-compose down -v"
- After this, run "docker-compose up --build -d"

# Endpoints
- Get: 
    - /api/task
      - /{id} : Get task by id
      - /{username} : Get task by username @PathVariable
      - Get task by date @RequestParam
      - Get task by priority @RequestParam
      - Get task by isCompleted @RequestParam
      - Get task order by priority @RequestParam
      - Get task order by date @RequestParam
      - Get task by date and order by priority @RequestParam
    

- Post: 
    - /auth
      - /login
      - /register
      - /logout -> no-impl
      - /forgot-password -> no-impl
    - /api/task
      - /create : Task body

- Put:
  - /api/task
    - /update : Task body
