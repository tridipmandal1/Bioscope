package com.bioscope.backend.v01.models.user;

import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.host.ShowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {

    private List<ShowModel> shows;
    private List<MovieModel> movies;
    private List<UserModel> hosts;
    private String message;
}
