package com.example.showcase.activity.receipt;

import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.activity.util.RequestLogger;
import jakarta.inject.Singleton;

@Singleton
public class ReceiptActivityImpl implements ReceiptActivity {
  private static final RequestLogger LOG = RequestLogger.forClass(ReceiptActivityImpl.class);

  @Override
  public void issue(RequestContext context) {
    LOG.info(context, "Receipt issued");
  }
}
