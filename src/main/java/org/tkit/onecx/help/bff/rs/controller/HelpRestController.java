package org.tkit.onecx.help.bff.rs.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.help.bff.rs.mappers.ExceptionMapper;
import org.tkit.onecx.help.bff.rs.mappers.HelpMapper;
import org.tkit.onecx.help.bff.rs.mappers.ProblemDetailMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.help.bff.clients.api.HelpsInternalApi;
import gen.org.tkit.onecx.help.bff.clients.model.Help;
import gen.org.tkit.onecx.help.bff.clients.model.HelpAppIds;
import gen.org.tkit.onecx.help.bff.clients.model.HelpPageResult;
import gen.org.tkit.onecx.help.bff.clients.model.ProblemDetailResponse;
import gen.org.tkit.onecx.help.bff.rs.internal.HelpsInternalApiService;
import gen.org.tkit.onecx.help.bff.rs.internal.model.*;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class HelpRestController implements HelpsInternalApiService {

    @Inject
    @RestClient
    HelpsInternalApi client;

    @Inject
    HelpMapper helpMapper;

    @Inject
    ProblemDetailMapper problemDetailMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createNewHelp(CreateHelpDTO createHelpDTO) {

        try (Response response = client.createNewHelp(helpMapper.mapCreateHelp(createHelpDTO))) {
            Help help = response.readEntity(Help.class);
            HelpDTO helpDTO = helpMapper.mapHelp(help);
            return Response.status(response.getStatus()).entity(helpDTO).build();
        }
    }

    @Override
    public Response deleteHelp(String id) {

        try (Response response = client.deleteHelp(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAllAppsWithHelpItems() {

        try (Response response = client.getAllAppsWithHelpItems()) {
            HelpAppIds helpAppIds = response.readEntity(HelpAppIds.class);
            HelpAppIdsDTO helpAppIdsDTO = helpMapper.mapHelpAppIds(helpAppIds);
            return Response.status(response.getStatus()).entity(helpAppIdsDTO).build();
        }
    }

    @Override
    public Response getHelpById(String id) {
        try (Response response = client.getHelpById(id)) {
            Help help = response.readEntity(Help.class);
            HelpDTO helpDTO = helpMapper.mapHelp(help);
            return Response.status(response.getStatus()).entity(helpDTO).build();
        }
    }

    @Override
    public Response searchHelps(HelpSearchCriteriaDTO helpSearchCriteriaDTO) {

        try (Response response = client.searchHelps(helpMapper.mapHelpSearchCriteria(helpSearchCriteriaDTO))) {
            HelpPageResult helpPageResult = response.readEntity(HelpPageResult.class);
            HelpPageResultDTO helpPageResultDTO = helpMapper.mapHelpPageResults(helpPageResult);
            return Response.status(response.getStatus()).entity(helpPageResultDTO).build();
        }
    }

    @Override
    public Response updateHelp(String id, UpdateHelpDTO updateHelpDTO) {
        try (Response response = client.updateHelp(id, helpMapper.mapUpdateHelp(updateHelpDTO))) {
            return Response.status(response.getStatus()).build();
        } catch (WebApplicationException ex) {
            return Response.status(ex.getResponse().getStatus())
                    .entity(problemDetailMapper.map(ex.getResponse().readEntity(ProblemDetailResponse.class))).build();
        }
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public Response restException(WebApplicationException ex) {
        return Response.status(ex.getResponse().getStatus()).build();
    }
}
