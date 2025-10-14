# Stellar Burgers — API Test Suite (JUnit + Rest Assured)

Automated API tests for the Stellar Burgers backend.
Covers user registration & auth, updating user data, creating orders, and fetching orders.

Tech stack

Java 11

Maven

JUnit 4

Rest Assured

Allure (JUnit4 + Rest Assured adapters)

AssertJ (fluent assertions)

Lombok (models)

JavaFaker (test data)

Endpoints under test

POST /api/auth/register — create user

POST /api/auth/login — authenticate

PATCH /api/auth/user — update user profile

POST /api/orders — create order

GET /api/orders — list orders (authorized / unauthorized)

GET /api/ingredients — helper for order creation

Default target host (as used in tests):
https://stellarburgers.nomoreparties.site

⚠️ If the host is unavailable from your network, see “Running with a local mock” below.

Project structure (key parts)
pom.xml                     # Dependencies + surefire + Allure plugins
src/test/java/
  BaseTest.java             # Common setup (RestAssured config, helpers)
  CreateUserTests.java
  LogInUserTests.java
  UpdateUserInfoTests.java
  CreateOrderTests.java
  GetUserOrdersTests.java

Prerequisites

JDK 11 (java -version → 11.x)

Maven (mvn -v)

(Optional) Allure CLI for pretty reports

macOS: brew install allure

Windows (Scoop): scoop install allure

Quick start
# 1) Clone
git clone -b develop2 https://github.com/reginaniko/Diplom_2.git
cd Diplom_2/Java-UI-Tests   # adjust if your test module lives here

# 2) Run all tests
mvn clean test


Test reports (raw Allure results) will appear in:

target/allure-results

Allure report

Ad-hoc (quick server):

allure serve target/allure-results


Static site via Maven:

mvn allure:report
# Open:
# macOS/Linux:
open target/site/allure-maven-plugin/index.html
# Windows:
start target\site\allure-maven-plugin\index.html

Configuration

By default, the tests target:

https://stellarburgers.nomoreparties.site


If your BaseTest reads a system property for the base URL (e.g., -DbaseUrl), you can switch environments like this:

mvn clean test -DbaseUrl=https://stellarburgers.nomoreparties.site
# or a local mock:
mvn clean test -DbaseUrl=http://localhost:8089


If your code doesn’t yet read -DbaseUrl, add this once in BaseTest:

RestAssured.baseURI = System.getProperty("baseUrl",
    "https://stellarburgers.nomoreparties.site");

Running a single test / method
# By class
mvn -Dtest=CreateUserTests test

# Single test method inside class
mvn -Dtest=CreateUserTests#testCreateUniqueUserIsSuccessful test

Running with a local mock (no internet required)

Sometimes the public host may be unreachable (DNS/VPN/firewall). You can still run the suite by stubbing endpoints locally with WireMock.

Add dependency in pom.xml (test scope):

<dependency>
  <groupId>com.github.tomakehurst</groupId>
  <artifactId>wiremock-jre8</artifactId>
  <version>2.35.2</version>
  <scope>test</scope>
</dependency>


In your base or a dedicated test rule:

@Rule
public WireMockRule wm = new WireMockRule(8089); // localhost:8089

@Before
public void setupStubs() {
  stubFor(post(urlEqualTo("/api/auth/register"))
    .willReturn(aResponse()
      .withStatus(200)
      .withHeader("Content-Type","application/json")
      .withBody("{\"success\":true,\"accessToken\":\"token\"}")));

  // add stubs for /api/auth/login, /api/orders, /api/ingredients, etc.
}


Run against the mock:

mvn clean test -DbaseUrl=http://localhost:8089


This gives you a green local run and an Allure report even if the real API is down.

Troubleshooting

UnknownHostException: stellarburgers.nomoreparties.site
Network/DNS issue. Try:

Turn off VPN / try another network or hotspot.

Set system DNS to 1.1.1.1 and/or 8.8.8.8.

Flush DNS (macOS):
sudo dscacheutil -flushcache; sudo killall -HUP mDNSResponder

Or run with the local mock (see above).

SLF4J “No StaticLoggerBinder” warning
Add a simple test logger:

<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-simple</artifactId>
  <version>2.0.13</version>
  <scope>test</scope>
</dependency>


Java version mismatch
Project targets Java 11. Ensure Maven uses JDK 11 (or configure a Maven Toolchain).

Skip tests temporarily
mvn -DskipTests clean package

Useful scripts (copy-paste)

Full run + Allure:

mvn clean test && allure serve target/allure-results


Run only auth tests:

mvn -Dtest=CreateUserTests,LogInUserTests test


Run with local mock:

mvn clean test -DbaseUrl=http://localhost:8089

Notes

Allure is preconfigured in pom.xml (Junit4 + RestAssured adapters and aspectjweaver agent via surefire).

Test data is generated with JavaFaker; email collisions are avoided for “unique user” scenarios.