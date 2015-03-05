package com.cinebrah.cinebrah.net.requests;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.RoomsResult;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import hugo.weaving.DebugLog;

/**
 * Created by Taylor on 3/3/2015.
 */
public class GetRoomsRequest extends RetrofitSpiceRequest<RoomsResult, CinebrahEndpoints> {

    private int page;

    public GetRoomsRequest(int page) {
        super(RoomsResult.class, CinebrahEndpoints.class);
        this.page = page;
    }

    @Override
    @DebugLog
    public RoomsResult loadDataFromNetwork() throws Exception {
        return getService().getRooms(page);
    }
}
