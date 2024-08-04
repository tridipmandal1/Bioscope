package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.ScreenDto;
import com.bioscope.backend.v01.payloads.SeatArrDto;
import com.bioscope.backend.v01.payloads.ShowDto;

import java.util.UUID;

public interface ScreenService {

    ScreenDto getScreenById(UUID screenId);
    ScreenDto getScreenByName(String screenName);
    SeatArrDto getSeatingArrangementByScreenId(UUID screenId);
    SeatArrDto getSeatingArrangementByScreenName(String screenName);
    ScreenDto createScreen(ScreenDto screenDto);
    ScreenDto updateScreen(ScreenDto screenDto);
    void deleteScreen(UUID screenId);
    ShowDto getCurrentShow(UUID screenId);
}
