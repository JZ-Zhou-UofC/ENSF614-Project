# Agent Customer Information Editing - Implementation Guide

## Overview
This document describes the implementation of the functionality that allows agents to change customer information in the Flight Booking System.

## Implementation Summary

The feature was implemented across three architectural layers:
1. **Data Access Layer** - Database operations
2. **Business Logic Layer** - Controller with validation
3. **Presentation Layer** - User interface dialogs

---

## Step-by-Step Implementation

### Step 1: Data Access Layer - UserDAO Update

**File:** `src/main/java/flightapp/data/UserDAO.java`

**What was added:**
- `updateCustomer(Customer customer)` method

**Purpose:**
- Updates customer information in the database
- Updates fields: first_name, last_name, email, phone, subscribed
- Validates that the user is a CUSTOMER role

**Code Location:**
```java
public Customer updateCustomer(Customer customer) throws SQLException {
    String sql = """
        UPDATE users 
        SET first_name = ?, last_name = ?, email = ?, phone = ?, subscribed = ?
        WHERE id = ? AND role = 'CUSTOMER'
    """;
    // ... implementation
}
```

---

### Step 2: Business Logic Layer - UserController Update

**File:** `src/main/java/flightapp/business/controller/UserController.java`

**What was added:**
- `updateCustomer(Agent agent, Customer customer)` method

**Purpose:**
- Validates that only agents can update customer information
- Checks customer existence
- Validates email uniqueness
- Logs all operations using SystemLogger

**Key Features:**
- Agent validation (throws IllegalArgumentException if not an agent)
- Email uniqueness check
- Comprehensive logging for audit trail

**Code Location:**
```java
public Customer updateCustomer(Agent agent, Customer customer) throws SQLException {
    // Agent validation
    // Customer existence check
    // Email uniqueness validation
    // Database update
    // Logging
}
```

---

### Step 3: User Interface - AgentEditCustomerDialog

**File:** `src/main/java/flightapp/presentation/agent/AgentEditCustomerDialog.java`

**What was created:**
- New dialog class for editing customer information

**Purpose:**
- Provides a form-based interface for agents to edit customer data
- Validates user input
- Displays success/error messages

**UI Components:**
- First Name text field
- Last Name text field
- Email text field
- Phone text field
- Subscribed checkbox

**Validation:**
- First name and last name cannot be empty
- Email must be valid (contains "@")
- Phone is optional

---

### Step 4: Update AgentManageCustomerDialog

**File:** `src/main/java/flightapp/presentation/agent/AgentManageCustomerDialog.java`

**What was modified:**
- Added `UserController` parameter to constructor
- Added "Edit Customer Info" button
- Added `openEditDialog()` method
- Made `targetCustomer` non-final to allow updates
- Added `titleLabel` field for dynamic title updates

**Purpose:**
- Integrates the edit functionality into the customer management dialog
- Updates the dialog title after successful edits

---

### Step 5: Update AgentSelectCustomerDialog

**File:** `src/main/java/flightapp/presentation/agent/AgentSelectCustomerDialog.java`

**What was modified:**
- Added `UserController` parameter to constructor
- Updated call to `AgentManageCustomerDialog` to pass UserController

**Purpose:**
- Ensures UserController is available throughout the agent workflow

---

### Step 6: Update AgentMainDialog

**File:** `src/main/java/flightapp/presentation/agent/AgentMainDialog.java`

**What was modified:**
- Added `UserController` parameter to constructor
- Updated call to `AgentSelectCustomerDialog` to pass UserController

**Purpose:**
- Passes UserController from MainWindow to the agent dialogs

---

### Step 7: Update MainWindow

**File:** `src/main/java/flightapp/presentation/MainWindow.java`

**What was modified:**
- Updated call to `AgentMainDialog` to pass `userController`

**Purpose:**
- Connects the UserController from MainWindow to the agent panel

---

## Database Schema

The implementation uses the existing `users` table with the following updatable fields:

| Field | Type | Description |
|-------|------|-------------|
| id | INT | Primary key (not updatable) |
| first_name | VARCHAR(50) | Customer's first name |
| last_name | VARCHAR(50) | Customer's last name |
| email | VARCHAR(100) | Customer's email (must be unique) |
| phone | VARCHAR(20) | Customer's phone number (optional) |
| subscribed | BOOLEAN | Email subscription status |
| role | ENUM | Must be 'CUSTOMER' (not updatable) |

---

## Usage Instructions

### For Agents:

1. **Login as Agent**
   - Start the application
   - Select "Agent" mode in the startup dialog
   - Login with agent credentials

2. **Access Agent Panel**
   - Click "Agent Panel" button in the main window

3. **Select Customer**
   - Click "Select Customer..." button
   - Choose a customer from the list
   - Click "Manage Customer"

4. **Edit Customer Information**
   - In the customer management dialog, click "Edit Customer Info"
   - Modify any of the following fields:
     - First Name
     - Last Name
     - Email
     - Phone
     - Subscription status (checkbox)
   - Click "Save Changes"

5. **Verify Changes**
   - Success message will be displayed
   - Dialog title updates to reflect new customer name
   - Changes are saved to the database

---

## Validation Rules

### Input Validation:
- **First Name**: Required, cannot be empty
- **Last Name**: Required, cannot be empty
- **Email**: Required, must contain "@" symbol
- **Phone**: Optional, can be empty
- **Subscribed**: Boolean checkbox

### Business Rules:
- Only agents can update customer information
- Email must be unique (cannot duplicate existing emails)
- Customer must exist in the database
- Customer must have role 'CUSTOMER'

---

## Error Handling

The implementation includes comprehensive error handling:

1. **Validation Errors**
   - Displayed in dialog with clear error messages
   - Prevents invalid data from being saved

2. **Database Errors**
   - SQL exceptions are caught and displayed
   - Logged using SystemLogger for debugging

3. **Business Logic Errors**
   - Email uniqueness violations
   - Customer not found errors
   - Agent permission errors

All errors are logged to the system log for audit purposes.

---

## Logging

All customer update operations are logged using `SystemLogger`:

- **INFO**: Successful updates
- **WARN**: Validation failures, customer not found
- **ERROR**: Database errors, exceptions

Log entries include:
- Agent ID
- Customer ID
- Operation type
- Success/failure status
- Error messages (if applicable)

---

## Security Considerations

1. **Role-Based Access Control**
   - Only agents can update customer information
   - Validation enforced at controller level

2. **Data Validation**
   - Input validation prevents SQL injection
   - Email uniqueness prevents data conflicts

3. **Audit Trail**
   - All operations are logged
   - Agent ID tracked for accountability

---

## Testing Checklist

When testing the implementation, verify:

- [ ] Agent can access customer edit dialog
- [ ] Non-agents cannot update customer information
- [ ] Required field validation works
- [ ] Email validation works
- [ ] Email uniqueness check works
- [ ] Phone field accepts empty values
- [ ] Subscription checkbox works
- [ ] Success message displays after save
- [ ] Dialog title updates after save
- [ ] Database is updated correctly
- [ ] Errors are logged properly
- [ ] Error messages are user-friendly

---

## Files Modified/Created

### Created Files:
1. `src/main/java/flightapp/presentation/agent/AgentEditCustomerDialog.java`

### Modified Files:
1. `src/main/java/flightapp/data/UserDAO.java`
2. `src/main/java/flightapp/business/controller/UserController.java`
3. `src/main/java/flightapp/presentation/agent/AgentManageCustomerDialog.java`
4. `src/main/java/flightapp/presentation/agent/AgentSelectCustomerDialog.java`
5. `src/main/java/flightapp/presentation/agent/AgentMainDialog.java`
6. `src/main/java/flightapp/presentation/MainWindow.java`

---

## Future Enhancements

Potential improvements for future versions:

1. **History Tracking**
   - Track who made changes and when
   - Display change history for customers

2. **Additional Fields**
   - Address information
   - Date of birth
   - Emergency contact

3. **Bulk Operations**
   - Update multiple customers at once
   - Import customer data from CSV

4. **Advanced Validation**
   - Phone number format validation
   - Email format validation (regex)
   - Name format validation

5. **Permissions**
   - Different permission levels for agents
   - Restrict certain fields from editing

---

## Troubleshooting

### Issue: "Agent cannot be null" error
**Solution:** Ensure UserController is properly passed through all dialog constructors.

### Issue: Email already exists error
**Solution:** The email must be unique. Check if another customer already has this email.

### Issue: Customer not found error
**Solution:** Verify the customer exists in the database and has role 'CUSTOMER'.

### Issue: Dialog doesn't update after save
**Solution:** Check that `getUpdatedCustomer()` returns non-null after successful save.

---

## Conclusion

The agent customer information editing feature has been successfully implemented following the MVC (Model-View-Controller) architecture pattern. The implementation includes:

- ✅ Database layer updates
- ✅ Business logic with validation
- ✅ User-friendly interface
- ✅ Comprehensive error handling
- ✅ Logging and audit trail
- ✅ Security and access control

The feature is ready for use and testing.

