/* 
 * Copyright 2017 SoftAvail Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softavail.commsrouter.api.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by @author mapuo on 05.10.17.
 */
public class CreatedTaskDto extends ApiObjectId {

  @JsonIgnore
  private String queueId;

  private Long queueTasks;

  public CreatedTaskDto() {}

  public CreatedTaskDto(ApiObjectId taskDto, String queueId, Long queueTasks) {
    super(taskDto);
    this.queueId = queueId;
    this.queueTasks = queueTasks;
  }

  public String getQueueId() {
    return queueId;
  }

  public Long getQueueTasks() {
    return queueTasks;
  }

}
