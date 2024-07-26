package org.tkit.onecx.help.bff.rs.mappers;

import java.util.Map;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.help.bff.rs.internal.model.ExportHelpsRequestDTO;
import gen.org.tkit.onecx.help.exim.client.model.ExportHelpsRequest;
import gen.org.tkit.onecx.help.exim.client.model.HelpSnapshot;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class EximHelpMapper {

    @Inject
    ObjectMapper mapper;

    public abstract ExportHelpsRequest map(ExportHelpsRequestDTO requestDTO);

    public HelpSnapshot createSnapshot(Map<?, ?> object) {
        return mapper.convertValue(object, HelpSnapshot.class);
    }
}
