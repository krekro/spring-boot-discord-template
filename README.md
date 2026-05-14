# Spring Boot Discord Bot Template 🚀

---

## 🛠️ Prerequisites

- **Java Development Kit (JDK) 21**
- **Maven 3.9+** (or use the included wrapper `./mvnw.cmd`)
- **Docker Desktop** (for containerized environment testing)

---

## ⚙️ Local Configuration

Create a file named `src/main/resources/application.properties` on your local system. _(This file is isolated via `.gitignore` to prevent credential drops)._

```properties
server.port=8080

# Credentials from the Discord Developer Portal
discord.public-key=YOUR_DISCORD_BOT_PUBLIC_KEY_HEX
discord.application-id=YOUR_DISCORD_APPLICATION_ID
discord.bot-token=YOUR_DISCORD_BOT_TOKEN
```

---

## 🚀 Local Development Workflow

### 1. Fire up the Spring Server Locally

Run the Maven execution target in your terminal:

```powershell
.\mvnw.cmd spring-boot:run
```

### 2. Expose the Inbound Webhook Port

Expose port `8080` to the internet securely via an HTTPS forwarder like **Ngrok**:

```powershell
ngrok http 8080
```

Copy the secure forwarding destination address (e.g., `ngrok-free.app`).

### 3. Connect to the Discord Developer Portal

1. Navigate to your Application dashboard at [Discord Developers](https://discord.com).
2. Locate the **Interactions Endpoint URL** field on the _General Information_ tab.
3. Paste your Ngrok address and append the routing endpoint path:
   `https://YOUR_SUBDOMAIN.ngrok-free.app/interactions`
4. Click **Save Changes**. Discord will test your `DiscordVerificationService` via a cryptographically signed `PING` token instantly.

---

- `config/` - Houses the managed Thread pool execution blocks and reactive `WebClient` engines.
- `controller/` - The entry point route threshold (`/interactions`) handling raw network packet evaluation.
- `dto/` - Clean, compile-safe Java `records` mapping user metrics and option parameters.
- `service/` - Bouncy castle signature processing and async background webhook message patchers.
