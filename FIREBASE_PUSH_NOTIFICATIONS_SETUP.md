# Firebase Push Notifications Implementation Guide

## Overview
This implementation provides comprehensive Firebase Cloud Messaging (FCM) push notification support for the Sasip Quiz application. It includes user token management, topic subscriptions, and various notification sending options.

## Setup Instructions

### 1. Firebase Project Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select an existing one
3. Enable Firebase Cloud Messaging (FCM)
4. Generate a service account key:
   - Go to Project Settings → Service Accounts
   - Click "Generate new private key"
   - Download the JSON file

### 2. Backend Configuration
1. Place the downloaded service account JSON file in `src/main/resources/` and rename it to `firebase-service-account.json`
2. Update your `application.properties` file:
   ```properties
   firebase.service-account-key=firebase-service-account.json
   ```

### 3. Database Schema Update
The User table now includes an `fcm_token` column. Run the following SQL to add it:
```sql
ALTER TABLE users ADD COLUMN fcm_token VARCHAR(255);
```

## API Endpoints

### FCM Token Management

#### Register FCM Token
```http
POST /api/notifications/fcm/register
Content-Type: application/json

{
    "userId": 1,
    "fcmToken": "your-fcm-token-here"
}
```

#### Remove FCM Token
```http
DELETE /api/notifications/fcm/{userId}
```

### Push Notifications

#### Send to Single User
```http
POST /api/notifications/push/send-to-user/{userId}
Content-Type: application/json

{
    "title": "Quiz Reminder",
    "body": "Don't forget to take today's quiz!",
    "imageUrl": "https://example.com/image.jpg"
}
```

#### Send to All Users
```http
POST /api/notifications/push/send-to-all
Content-Type: application/json

{
    "title": "New Quiz Available",
    "body": "A new quiz has been published!",
    "imageUrl": "https://example.com/image.jpg"
}
```

#### Send to Topic
```http
POST /api/notifications/push/send-to-topic/{topic}
Content-Type: application/json

{
    "title": "Topic Notification",
    "body": "Message for topic subscribers",
    "imageUrl": "https://example.com/image.jpg"
}
```

#### Create and Send Notification
```http
POST /api/notifications/push/send
Content-Type: application/json

{
    "title": "Quiz Reminder",
    "description": "Complete your daily quiz to maintain your streak!",
    "type": "Quiz Reminder",
    "audience": "All Users", // or "User-{userId}" or topic name
    "imageUrl": "https://example.com/image.jpg",
    "sendPush": true
}
```

### Topic Management

#### Subscribe Users to Topic
```http
POST /api/notifications/topic/subscribe?topic=daily-quiz
Content-Type: application/json

[1, 2, 3, 4, 5] // Array of user IDs
```

#### Unsubscribe Users from Topic
```http
POST /api/notifications/topic/unsubscribe?topic=daily-quiz
Content-Type: application/json

[1, 2, 3, 4, 5] // Array of user IDs
```

### Advanced Push Notification
```http
POST /api/notifications/push/advanced
Content-Type: application/json

{
    "title": "Advanced Notification",
    "body": "This is an advanced notification",
    "imageUrl": "https://example.com/image.jpg",
    "topic": "all-users", // OR use fcmTokens array
    "fcmTokens": ["token1", "token2"],
    "data": {
        "custom_key": "custom_value",
        "quiz_id": "123"
    },
    "clickAction": "QUIZ_DETAIL",
    "sound": "default",
    "badge": "1",
    "priority": 10,
    "silent": false
}
```

## Frontend Integration

### Android (Flutter/React Native)
1. Add Firebase to your Android app
2. Get the FCM token:
   ```dart
   // Flutter
   String? token = await FirebaseMessaging.instance.getToken();
   
   // React Native
   import messaging from '@react-native-firebase/messaging';
   const token = await messaging().getToken();
   ```
3. Register the token with your backend:
   ```dart
   // Send POST request to /api/notifications/fcm/register
   ```

### iOS (Flutter/React Native)
1. Add Firebase to your iOS app
2. Request permission and get token:
   ```dart
   // Flutter
   NotificationSettings settings = await FirebaseMessaging.instance.requestPermission();
   String? token = await FirebaseMessaging.instance.getToken();
   ```

## Usage Examples

### 1. User Registration Flow
When a user registers or logs in:
```javascript
// Frontend: Get FCM token and register it
const fcmToken = await getFCMToken();
await fetch('/api/notifications/fcm/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        userId: currentUser.id,
        fcmToken: fcmToken
    })
});
```

### 2. Quiz Completion Notification
```java
// Backend: Send notification when user completes a quiz
notificationService.createAndSendNotification(
    "Quiz Completed!",
    "Great job! You scored 85% on the Science Quiz.",
    "Quiz Result",
    "User-" + userId,
    "https://example.com/success.jpg",
    true // Send push notification
);
```

### 3. Daily Reminder
```java
// Backend: Send daily reminders to all users
notificationService.sendPushNotificationToAllUsers(
    "Daily Quiz Reminder",
    "Don't break your streak! Complete today's quiz.",
    "https://example.com/reminder.jpg"
);
```

### 4. Topic-based Notifications
```java
// Subscribe users to grade-specific topics
List<Long> grade12Users = getUsersByGrade(12);
userService.getFcmTokensByUserIds(grade12Users);
firebasePushNotificationService.subscribeToTopic(fcmTokens, "grade-12");

// Send notifications to specific grade
notificationService.sendPushNotificationToTopic(
    "grade-12",
    "Grade 12 Special Quiz",
    "A new advanced quiz is available for Grade 12 students!",
    "https://example.com/grade12.jpg"
);
```

## Error Handling

The implementation includes comprehensive error handling:
- Invalid FCM tokens are filtered out
- Firebase initialization failures are logged
- Network errors are caught and logged
- Database errors are handled gracefully

## Security Considerations

1. **FCM Token Security**: FCM tokens are stored securely in the database
2. **Authentication**: All endpoints should be protected with proper authentication
3. **Rate Limiting**: Consider implementing rate limiting for notification endpoints
4. **Data Validation**: All input data is validated before processing

## Monitoring and Analytics

- All notification sending attempts are logged
- Success/failure counts are tracked
- Invalid tokens are identified and can be cleaned up

## Troubleshooting

### Common Issues:
1. **Firebase not initialized**: Ensure the service account JSON file is in the correct location
2. **Invalid FCM tokens**: Tokens expire and need to be refreshed on the client side
3. **Network issues**: Check Firebase service status and network connectivity

### Logs to Check:
- Application startup logs for Firebase initialization
- Push notification sending logs
- Database operation logs for FCM token updates

## Testing

### Test Notification Sending:
```bash
# Test single user notification
curl -X POST http://localhost:8080/api/notifications/push/send-to-user/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","body":"Test message","imageUrl":""}'

# Test topic notification
curl -X POST http://localhost:8080/api/notifications/push/send-to-topic/test-topic \
  -H "Content-Type: application/json" \
  -d '{"title":"Topic Test","body":"Topic test message","imageUrl":""}'
```

## Performance Considerations

- Batch notifications are sent using FCM's multicast messaging for efficiency
- Database queries are optimized to fetch only necessary FCM tokens
- Failed token cleanup should be implemented to maintain performance

This implementation provides a robust, scalable push notification system that can handle various use cases in your quiz application.