# Online Movie Website

Online Movie Website is a Spring Boot-based web application that allows users to watch movies and TV shows. The application supports user authentication (form login and Google OAuth2), video uploads, email notifications.

## Features

- Stream movies and TV episodes
- User registration and login (standard and Google OAuth2)
- Email verification and notifications
- Upload and manage media content
- Responsive frontend with Thymeleaf

## Prerequisites

- Java 17 or higher
- Maven
- SQL Server or MySQL
- Gmail account for email service (optional)

## Getting Started

Steps
1. Clone the reposetory:
```sh
git clone https://github.com/B3omQ/OnlineMovieWeb.git
```
2. Create database in your sql server or mySQL(Recommend sql server)
```sh
CREATE DATABASE MovieWebsite
```
3. You can run example data query in data packet.
```sh
OnlineMovieWeb\data\data_Test_Query1.sql
```
4. Read `example-application.properties` to modify it to work on your device.
5. After modify, change the name `example-application.properties`, into `application.properties` and it all done.


## How to Get Google OAuth2 Credentials

To enable Google login in this application, follow these steps to obtain your **Client ID** and **Client Secret**:

### Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click **"Select a project"** → **"New Project"**
3. Enter a project name, then click **"Create"**

### Step 2: Configure OAuth Consent Screen

1. In the left sidebar, go to **APIs & Services > OAuth consent screen**
2. Choose **"External"** as the user type, then click **"Create"**
3. Fill in the required information (app name, support email, etc.)
4. Add scopes: `email`, `profile`, `openid`
5. Add test users (your Gmail account)

### Step 3: Create OAuth2 Credentials

1. Go to **APIs & Services > Credentials**
2. Click **"Create Credentials"** → **"OAuth client ID"**
3. Choose **Web application**
4. Set a name (e.g., `BookStore Login`)
5. Under **Authorized redirect URIs**, add:
```sh
http://localhost:8080/login/oauth2/code/google
```
6. Click **Create**
7. Copy the generated **Client ID** and **Client Secret**

---

## How to Get Gmail SMTP App Password

To send emails from your Gmail account using Spring Boot, follow these steps:

### Step 1: Enable 2-Step Verification

1. Visit [Google Account Security](https://myaccount.google.com/security)
2. Turn on **2-Step Verification**

### Step 2: Generate an App Password

1. Go to [App Passwords](https://myaccount.google.com/apppasswords)  
*(only visible after enabling 2FA)*
2. Select **"Mail"** as the app and **"Other"** or your device name
3. Click **Generate**
4. Copy the **16-character app password** shown

> Use this password in your `application.properties` as the value of `spring.mail.password`

---

## Summary of What You Need

- `spring.security.oauth2.client.registration.google.client-id` → From Google Console
- `spring.security.oauth2.client.registration.google.client-secret` → From Google Console
- `spring.mail.username` → Your Gmail address
- `spring.mail.password` → Gmail App Password (not your real Gmail password!)
