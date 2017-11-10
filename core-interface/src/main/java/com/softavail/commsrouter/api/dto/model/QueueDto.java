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

import com.softavail.commsrouter.api.dto.arg.CreateQueueArg;


/**
 *
 * @author ikrustev
 */
public class QueueDto extends RouterObjectId {

  private String description;
  private String predicate;

  public QueueDto() {}

  public QueueDto(CreateQueueArg queueArg, RouterObjectId objectId) {
    super(objectId.getId(), objectId.getRouterId());
    description = queueArg.getDescription();
    predicate = queueArg.getPredicate();
  }

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
