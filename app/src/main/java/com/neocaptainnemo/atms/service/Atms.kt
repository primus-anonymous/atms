package com.neocaptainnemo.atms.service

import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import io.reactivex.Observable

interface Atms {

    /**
     * Fetches atms located in a given view port.
     *
     * @param viewPort - view port (bounds of lat, lng, where search is performed).
     * @return emits list of atms on success.
     */
    fun request(viewPort: ViewPort): Observable<List<AtmNode>>

}
