package com.example.showcase.activity.welcomeEmail;

import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.activity.util.RequestLogger;
import jakarta.inject.Singleton;

@Singleton
public class WelcomeEmailActivityImpl implements WelcomeEmailActivity {
  private static final RequestLogger LOG = RequestLogger.forClass(WelcomeEmailActivityImpl.class);

  @Override
  public void send(RequestContext context, String email) {
    LOG.info(context, "Welcome email queued for {}", email);
  }
}
