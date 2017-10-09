package com.softavail.commsrouter.client;

import com.softavail.commsrouter.api.dto.arg.CreateQueueArg;
import com.softavail.commsrouter.api.dto.arg.UpdateQueueArg;
import com.softavail.commsrouter.api.dto.misc.PaginatedList;
import com.softavail.commsrouter.api.dto.model.ApiObjectId;
import com.softavail.commsrouter.api.dto.model.QueueDto;
import com.softavail.commsrouter.api.dto.model.RouterObjectId;
import com.softavail.commsrouter.api.dto.model.TaskDto;
import com.softavail.commsrouter.api.exception.CommsRouterException;
import com.softavail.commsrouter.api.exception.NotFoundException;
import com.softavail.commsrouter.api.interfaces.QueueService;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * Created by @author mapuo on 05.09.17.
 */
public class QueueServiceClient extends ServiceClientBase<QueueDto, ApiObjectId>
    implements QueueService {

  private final Client client;
  private final String endpoint;
  private final String routerId;

  @Inject
  public QueueServiceClient(Client client, String endpoint, String routerId) {
    super(QueueDto.class, ApiObjectId.class);
    this.client = client;
    this.endpoint = endpoint;
    this.routerId = routerId;
  }

  @Override
  UriBuilder getApiUrl() {
    return UriBuilder.fromPath(endpoint).path("routers")
        .path("{routerId}").path("queues").clone();
  }

  @Override
  Client getClient() {
    return client;
  }

  @Override
  public QueueDto get(RouterObjectId routerObject)
      throws NotFoundException {

    return getItem(new RouterObjectId(routerObject.getId(), routerObject.getRouterId()));
  }

  @Override
  public List<QueueDto> list(String routerId) {
    return getList(routerId);
  }

  @Override
  public PaginatedList<QueueDto> listPage(String routerId, int page, int perPage) {
    return getList(routerId, page, perPage);
  }

  @Override
  public void delete(RouterObjectId routerObject) {
    routerObject.setRouterId(routerId);
    super.deleteRequest(new RouterObjectId(routerObject.getId(), routerObject.getRouterId()));
  }

  @Override
  public ApiObjectId create(CreateQueueArg createArg, String id)
      throws NotFoundException {

    return post(createArg, id);
  }

  @Override
  public ApiObjectId create(CreateQueueArg createArg, RouterObjectId objectId)
      throws CommsRouterException {

    return put(createArg, objectId);
  }

  @Override
  public void update(UpdateQueueArg updateArg, RouterObjectId id)
      throws NotFoundException {

    post(updateArg, id);
  }

  @Override
  public long getQueueSize(RouterObjectId routerObjectId)
      throws NotFoundException {

    URI uri = getApiUrl().clone()
        .path("{resourceId}")
        .path("size")
        .build(routerObjectId.getRouterId(), routerObjectId.getId());

    return getClient()
        .target(uri)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(Long.class);
  }

  @Override
  public Collection<TaskDto> getTasks(RouterObjectId routerObjectId)
      throws NotFoundException {

    URI uri = getApiUrl().clone()
        .path("{resourceId}")
        .path("tasks")
        .build(routerObjectId.getRouterId(), routerObjectId.getId());

    return getClient()
        .target(uri)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(new GenericType<Collection<TaskDto>>(){});
  }

}