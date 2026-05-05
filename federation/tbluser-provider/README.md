# Keycloak tblUser Storage Provider

A custom UserStorageProvider for Keycloak that reads user data from an existing `tblUser` table instead of Keycloak's default `USER_ENTITY` table.

## Overview

This provider allows Keycloak to authenticate users against an existing user table (`tblUser`) while keeping Keycloak's infrastructure tables (realms, clients, roles, etc.) intact. This is useful when integrating Keycloak with legacy applications that have their own user management system.

## Features

- **User Lookup**: Supports lookup by username, user ID, and email
- **Password Validation**: Validates passwords against the `tblUser` table
- **Read-Only**: User data is read-only from the perspective of Keycloak (modifications are not synced back to tblUser)
- **Account Status**: Respects the `Active` and `Locked` flags from tblUser
- **User Attributes**: Maps tblUser fields to Keycloak user attributes

## Configuration

### Prerequisites

1. The `tblUser` table must exist in your database with at least the following columns:
   - `UserID` (int, primary key)
   - `UserName` (varchar)
   - `Password` (varchar)
   - `FirstName` (varchar)
   - `LastName` (varchar)
   - `FullName` (varchar)
   - `EMail` (varchar)
   - `Active` (tinyint/boolean)
   - `Locked` (tinyint/boolean)

2. A DataSource must be configured in your application server (e.g., WildFly, Quarkus) pointing to the database containing tblUser.

### Setup Steps

1. **Build the Provider**:
   ```bash
   mvn clean install
   ```

2. **Deploy to Keycloak**:
   - Copy the JAR file to your Keycloak providers directory
   - Or include it in your Keycloak distribution

3. **Configure in Keycloak Admin Console**:
   - Go to Realm Settings → User Federation
   - Click "Add Provider" and select "tbluser"
   - Enter the DataSource JNDI name (e.g., `java:jboss/datasources/MyDS`)
   - Click "Save"

4. **Set as Primary User Storage** (optional):
   - In the provider configuration, you can set priority to ensure this provider is checked first

## Database Schema

The provider expects the following columns in `tblUser`:

```sql
CREATE TABLE `tblUser` (
  `UserID` int(6) NOT NULL AUTO_INCREMENT,
  `UserName` varchar(150) NOT NULL,
  `Password` varchar(100) NOT NULL,
  `FirstName` varchar(35) NOT NULL DEFAULT '',
  `LastName` varchar(35) NOT NULL DEFAULT '',
  `FullName` varchar(88) NOT NULL DEFAULT '',
  `EMail` varchar(65) NOT NULL DEFAULT '',
  `Active` tinyint(4) NOT NULL DEFAULT 1,
  `Locked` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `idxUser` (`UserName`)
);
```

## Password Handling

Passwords are validated directly against the `Password` column in tblUser. The provider performs a direct string comparison. If your passwords are hashed, ensure the comparison logic matches your hashing algorithm.

### Important Security Note

If passwords in tblUser are hashed, you may need to modify the `isValid()` method in `TblUserStorageProvider.java` to use the appropriate hashing algorithm for comparison.

## Limitations

- **Read-Only**: User attributes cannot be modified through Keycloak (they are read-only)
- **No User Registration**: New users cannot be created through Keycloak's registration process
- **No Password Changes**: Password changes through Keycloak are not supported
- **No User Deletion**: Users cannot be deleted through Keycloak

## Troubleshooting

### DataSource Not Found
If you get "Could not find DataSource" error:
1. Verify the JNDI name is correct
2. Ensure the DataSource is properly configured in your application server
3. Check the Keycloak logs for more details

### Users Not Found
1. Verify the tblUser table exists and contains data
2. Check that column names match exactly (case-sensitive)
3. Verify the DataSource connection is working

### Password Validation Fails
1. Ensure passwords in tblUser are in plain text or adjust the hashing logic
2. Check that the `Password` column contains the expected values
3. Verify the user exists in tblUser

## Development

To build and test locally:

```bash
cd federation/tbluser-provider
mvn clean package
```

The JAR will be created in the `target/` directory.

## License

Licensed under the Apache License, Version 2.0. See LICENSE.txt for details.
