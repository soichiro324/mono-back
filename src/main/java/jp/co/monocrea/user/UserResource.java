package jp.co.monocrea.user;

import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jp.co.monocrea.user.entity.User;
import jp.co.monocrea.user.response.SearchUserResponse;
import jp.co.monocrea.user.service.UserService;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    UserService userService;

    private static final Logger log = Logger.getLogger(UserResource.class);

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    public User create(Map<String, String> body) {
        String name = body.get("name");
        log.infov(name);
        return userService.createUser(name);
    }

    @PUT
    public Response update(
        @QueryParam("id") Long id,
        Map<String, String> body
    ) {
        String name = body.get("name");
        if (id == null || name == null || name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Both 'id' and 'name' must be provided")
                        .build();
        }

        User user = new User(id, name);
        boolean updated = userService.updateUser(user);
        if (updated) {
            return Response.ok().build(); // 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with id: " + user.id)
                        .build();
        }
    }

    @GET
    public SearchUserResponse searchUsers(
        @QueryParam("id") Long id,
        @QueryParam("name") String name,
        @QueryParam("sort") @DefaultValue("id") String sort,
        @QueryParam("order") @DefaultValue("asc") String order,
        @QueryParam("limit") @DefaultValue("10") int limit,
        @QueryParam("offset") @DefaultValue("0") int offset
    ) {
        List<User> list = userService.searchUsers(id, name, sort, order, limit, offset);
        long count = userService.allUsersCount(id, name);
        log.infov("success");
        return new SearchUserResponse(list, count);
    }

    @DELETE
    public Response deleteUser(@QueryParam("id") Long id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Query parameter 'id' is required")
                        .build();
        }

        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return Response.noContent().build(); // 204 No Content
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with id: " + id)
                        .build();
        }
    }
}
