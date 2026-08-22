package com.example.showcase.activity.clientUpdate;

import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.activity.util.RequestLogger;
import jakarta.inject.Singleton;

/** Replace this implementation with an HTTP client in a real integration. */
@Singleton
public class ClientUpdateActivityImpl implements ClientUpdateActivity {
  private static final RequestLogger LOG = RequestLogger.forClass(ClientUpdateActivityImpl.class);

  @Override
  public void update(RequestContext context, String status, String detail) {
    LOG.info(context, "MOCK CLIENT UPDATE: status={}, detail={}", status, detail);
  }
}
