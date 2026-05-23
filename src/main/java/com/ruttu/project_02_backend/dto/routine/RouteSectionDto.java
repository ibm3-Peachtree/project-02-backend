package com.ruttu.project_02_backend.dto.routine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WalkSectionDto.class, name = "walk"),
        @JsonSubTypes.Type(value = BusSectionDto.class, name = "bus"),
        @JsonSubTypes.Type(value = SubwaySectionDto.class, name = "subway")
})
public interface RouteSectionDto {
    @JsonIgnore
    String getType();

    int getSectionTime();
}
