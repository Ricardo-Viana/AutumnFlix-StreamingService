package com.autumnflix.streaming.core.modelMapper;

import com.autumnflix.streaming.api.model.season.SeasonInputDto;
import com.autumnflix.streaming.api.model.series.SeriesDto;
import com.autumnflix.streaming.api.model.series.SeriesResumeDto;
import com.autumnflix.streaming.domain.model.Season;
import com.autumnflix.streaming.domain.model.Series;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        configureMappings(modelMapper);

        return modelMapper;
    }

    private void configureMappings(ModelMapper mapper) {
        configSeriesSeriesResumeDtoTypeMap(mapper);
        configSeriesSeriesDtoTypeMap(mapper);
    }

    private void configSeriesSeriesDtoTypeMap(ModelMapper mapper) {
        TypeMap<Series, SeriesDto> seriesSeriesDtoTypeMap =
                mapper.typeMap(Series.class, SeriesDto.class);

        seriesSeriesDtoTypeMap.addMappings(attribute -> {
            attribute.map(Series::numOfSeasons, SeriesDto::setNumOfSeasons);
        });
    }

    private void configSeriesSeriesResumeDtoTypeMap(ModelMapper mapper) {
        TypeMap<Series, SeriesResumeDto> seriesSeriesResumeDtoTypeMap =
                mapper.typeMap(Series.class, SeriesResumeDto.class);

        seriesSeriesResumeDtoTypeMap.addMappings(attribute -> {
            attribute.map(Series::numOfSeasons, SeriesResumeDto::setNumOfSeasons);
        });
    }
}
