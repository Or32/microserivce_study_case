package com.example.showcase.activity.clientUpdate;

import com.example.showcase.activity.ActivityTaskQueue;
import com.example.showcase.activity.common.RequestContext;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
@ActivityTaskQueue("client-update-activities")
public interface ClientUpdateActivity {
  void update(RequestContext context, String status, String detail);
}
