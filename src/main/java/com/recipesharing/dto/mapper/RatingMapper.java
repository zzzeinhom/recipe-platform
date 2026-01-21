package com.recipesharing.dto.mapper;

import com.recipesharing.dto.request.CreateRatingRequest;
import com.recipesharing.dto.request.UpdateRatingRequest;
import com.recipesharing.dto.response.RatingResponse;
import com.recipesharing.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RatingMapper {
    Rating toRating(CreateRatingRequest request);

    @Mapping(source = "user.username", target = "username")
    RatingResponse toRatingResponse(Rating rating);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    void updateRatingFromRequest(
            UpdateRatingRequest request,
            @MappingTarget Rating rating
    );

}
