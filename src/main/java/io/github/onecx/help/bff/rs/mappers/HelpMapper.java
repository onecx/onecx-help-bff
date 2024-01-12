package io.github.onecx.help.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.help.bff.clients.model.*;
import gen.io.github.onecx.help.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface HelpMapper {

    CreateHelp mapCreateHelp(CreateHelpDTO createHelpDTO);

    HelpDTO mapHelp(Help help);

    @Mapping(target = "removeAppIdsItem", ignore = true)
    HelpAppIdsDTO mapHelpAppIds(HelpAppIds helpAppIds);

    @Mapping(target = "removeStreamItem", ignore = true)
    HelpPageResultDTO mapHelpPageResults(HelpPageResult helpPageResult);

    HelpSearchCriteria mapHelpSearchCriteria(HelpSearchCriteriaDTO helpSearchCriteria);

    UpdateHelp mapUpdateHelp(UpdateHelpDTO updateHelpDTO);
}
