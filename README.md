# Bank Integrate

## How to Run the Source Code

### Prerequisites
- IntelliJ IDEA (or any Java IDE)
- MySQL
- Java 17

### Setup Instructions

1. **Create Databases**  
   You need to create two databases in MySQL:
   - `integrate-bank`
   - `bank`

2. **Configure MySQL Credentials**  
   Update your MySQL username and password in the `application.properties` (or `application.yml`) file:

   ```properties
   spring.datasource.username=your-username
   spring.datasource.password=your-password