package org.tkit.onecx.help.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.help.bff.rs.internal.model.*;
import gen.org.tkit.onecx.help.client.model.*;
import gen.org.tkit.onecx.product.store.model.ProductItemPageResult;
import gen.org.tkit.onecx.product.store.model.ProductItemSearchCriteria;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface HelpMapper {

    CreateHelp mapCreateHelp(CreateHelpDTO createHelpDTO);

    HelpDTO mapHelp(Help help);

    @Mapping(target = "removeStreamItem", ignore = true)
    HelpPageResultDTO mapHelpPageResults(HelpPageResult helpPageResult);

    HelpSearchCriteria mapHelpSearchCriteria(HelpSearchCriteriaDTO helpSearchCriteria);

    UpdateHelp mapUpdateHelp(UpdateHelpDTO updateHelpDTO);

    @Mapping(target = "removeProductNamesItem", ignore = true)
    HelpProductNamesDTO mapHelpProductNames(HelpProductNames helpProductNames);

    @Mapping(target = "productNames", ignore = true)
    ProductItemSearchCriteria map(ProductsSearchCriteriaDTO productsSearchCriteriaDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductsPageResultDTO map(ProductItemPageResult productItemPageResult);

}
