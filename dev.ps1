param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("backend-test", "frontend-test", "test", "build", "up", "down")]
    [string]$Command
)

switch ($Command) {
    "backend-test" {
        Push-Location backend
        try {
            .\gradlew.bat test
        }
        finally {
            Pop-Location
        }
    }

    "frontend-test" {
        npm --prefix frontend run test:run
    }

    "test" {
        Push-Location backend
        try {
            .\gradlew.bat test
        }
        finally {
            Pop-Location
        }

        npm --prefix frontend run test:run
    }

    "build" {
        Push-Location backend
        try {
            .\gradlew.bat bootJar
        }
        finally {
            Pop-Location
        }

        npm --prefix frontend run build
    }

    "up" {
        docker compose up -d
    }

    "down" {
        docker compose down
    }
}