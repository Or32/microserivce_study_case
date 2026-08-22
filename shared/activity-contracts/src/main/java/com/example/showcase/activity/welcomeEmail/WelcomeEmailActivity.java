package com.example.showcase.activity.welcomeEmail;

import com.example.showcase.activity.ActivityTaskQueue;
import com.example.showcase.activity.common.RequestContext;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
@ActivityTaskQueue("welcome-email-activities")
public interface WelcomeEmailActivity {
  void send(RequestContext context, String email);
}
