# LinguaLeap

LinguaLeap is an AI-powered language learning assistant that provides personalized learning experiences through smart error analysis and adaptive learning paths.

## Features

- **Smart Error Analysis**: AI-powered analysis of language mistakes with detailed explanations
- **Adaptive Learning**: Personalized practice sessions based on user performance
- **Progress Tracking**: Comprehensive tracking of learning progress with visual feedback
- **AI Tutor Interactions**: Conversational interface for natural language learning

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: PostgreSQL
- **Cache**: Redis
- **AI Integration**: Anthropic Claude API
- **Testing**: JUnit 5, Mockito, TestContainers

## Prerequisites

- JDK 17
- PostgreSQL 15+
- Docker (optional, for containerization)
- Maven or Gradle

## Getting Started

1. Clone the repository
```bash
git clone https://github.com/YOUR-USERNAME/lingualeap.git
cd lingualeap
```

2. Configure database
```bash
# Create database
createdb lingualeap
```

3. Configure application properties
```bash
# Copy sample properties
cp application-dev.yml.sample application-dev.yml

# Edit with your configurations
vim application-dev.yml
```

4. Run the application
```bash
./gradlew bootRun
```

The application will be available at `http://localhost:8080/api/v1`

## Development

### Building
```bash
./gradlew clean build
```

### Testing
```bash
./gradlew test
```

### Code Style
We follow standard Java code style with some custom rules. See `rules.txt` for details.

## Contributing

1. Create a feature branch
```bash
git checkout -b feature/your-feature-name
```

2. Commit your changes
```bash
git commit -m "Add some feature"
```

3. Push to the branch
```bash
git push origin feature/your-feature-name
```

4. Create a Pull Request

## Environment Configuration

The application supports multiple environments through Spring profiles:
- `dev`: Development environment
- `test`: Testing environment
- `prod`: Production environment

