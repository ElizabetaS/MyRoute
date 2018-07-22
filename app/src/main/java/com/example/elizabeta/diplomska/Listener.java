package com.example.elizabeta.diplomska;

import com.example.elizabeta.diplomska.Model.UserRResponse;
import com.example.elizabeta.diplomska.Model.UserRoute;
import com.example.elizabeta.diplomska.Model.UserRouteResponse;

/**
 * Created by elisp on 22.12.2017.
 */

public interface Listener {
    public void onRowClick(UserRouteResponse userRoute, int position);
    public void onLongClick(UserRouteResponse userRoute, int position);

}
