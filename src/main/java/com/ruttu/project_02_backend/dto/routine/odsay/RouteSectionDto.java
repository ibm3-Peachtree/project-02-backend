package com.ruttu.project_02_backend.dto.routine.odsay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ruttu.project_02_backend.dto.routine.live.DetourBusSectionDto;
import com.ruttu.project_02_backend.dto.routine.live.DetourSubwaySectionDto;
import com.ruttu.project_02_backend.dto.routine.live.DetourWalkSectionDto;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WalkSectionDto.class, name = "walk"),
        @JsonSubTypes.Type(value = BusSectionDto.class, name = "bus"),
        @JsonSubTypes.Type(value = SubwaySectionDto.class, name = "subway"),
        @JsonSubTypes.Type(value = DetourWalkSectionDto.class, name = "walk"),
        @JsonSubTypes.Type(value = DetourBusSectionDto.class, name = "bus"),
        @JsonSubTypes.Type(value = DetourSubwaySectionDto.class, name = "subway")
})
public interface RouteSectionDto {
    @JsonIgnore
    String getType();

    int getSectionTime();

    List<String> getNo();

}
