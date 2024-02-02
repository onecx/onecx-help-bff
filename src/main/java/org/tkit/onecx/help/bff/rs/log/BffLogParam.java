package org.tkit.onecx.help.bff.rs.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.help.bff.rs.internal.model.*;

@ApplicationScoped
public class BffLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, CreateHelpDTO.class, x -> {
                    CreateHelpDTO d = (CreateHelpDTO) x;
                    return CreateHelpDTO.class.getSimpleName() + "[" + d.getAppId() + "," + d.getItemId() + "]";
                }),
                item(10, UpdateHelpDTO.class, x -> {
                    UpdateHelpDTO d = (UpdateHelpDTO) x;
                    return UpdateHelpDTO.class.getSimpleName() + "[" + d.getAppId() + "," + d.getItemId() + "]";
                }),
                item(10, HelpSearchCriteriaDTO.class, x -> {
                    HelpSearchCriteriaDTO d = (HelpSearchCriteriaDTO) x;
                    return HelpSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + ","
                            + d.getPageSize()
                            + "]";
                }));
    }
}
