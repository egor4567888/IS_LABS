package org.example.web;

import org.example.model.Person;
import org.example.service.PersonService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.time.ZonedDateTime;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonController {

    @Inject
    private PersonService service;

    @POST
    public Response create(Person p, @Context UriInfo uriInfo) {
        Person created = service.create(p);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(created.getId().toString());
        return Response.created(builder.build()).entity(created).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        Person p = service.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(p).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Person p) {
        if (!id.equals(p.getId())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("ID mismatch").build();
        }
        Person updated = service.update(p);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            service.delete(id);
            return Response.noContent().build();
        } catch (IllegalStateException ex) {
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        }
    }

    // list with pagination & filtering (filter only full match on string columns)
    @GET
    public Response list(@QueryParam("page") @DefaultValue("0") int page,
                         @QueryParam("size") @DefaultValue("20") int size,
                         @QueryParam("sort") String sort,
                         @QueryParam("asc") @DefaultValue("true") boolean asc,
                         @QueryParam("filterColumn") String filterColumn,
                         @QueryParam("filterValue") String filterValue) {
        int offset = page * size;
        List<Person> data = service.findAll(offset, size, sort, asc, filterColumn, filterValue);
        long total = service.count();
        return Response.ok(data).header("X-Total-Count", total).build();
    }

    // специальные операции:
    @GET
    @Path("countByBirthday")
    public Response countByBirthday(@QueryParam("birthday") String birthdayIso) {
        ZonedDateTime d = ZonedDateTime.parse(birthdayIso);
        long cnt = service.countByBirthday(d);
        return Response.ok(cnt).build();
    }

    @DELETE
    @Path("deleteByBirthday")
    public Response deleteByBirthday(@QueryParam("birthday") String birthdayIso) {
        ZonedDateTime d = ZonedDateTime.parse(birthdayIso);
        long removed = service.deleteByBirthday(d);
        return Response.ok(removed).build();
    }

    @GET
    @Path("countHeightLessThan")
    public Response countHeightLessThan(@QueryParam("height") float height) {
        long cnt = service.countHeightLessThan(height);
        return Response.ok(cnt).build();
    }

    @GET
    @Path("countByHairColor")
    public Response countByHairColor(@QueryParam("color") String color) {
        long cnt = service.countByHairColor(color);
        return Response.ok(cnt).build();
    }

    @GET
    @Path("countByEyeColor")
    public Response countByEyeColor(@QueryParam("color") String color) {
        long cnt = service.countByEyeColor(color);
        return Response.ok(cnt).build();
    }
}