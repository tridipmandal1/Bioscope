package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.models.host.EventHostModel;
import com.bioscope.backend.v01.models.host.EventHostRequestModel;

public interface EventHostService {

    EventHostModel createEventHost(EventHostRequestModel eventHostRequestModel);
}
