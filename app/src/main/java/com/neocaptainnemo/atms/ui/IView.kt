package com.neocaptainnemo.atms.ui

import com.neocaptainnemo.atms.model.AtmNode

 interface IView {

    /**
     * Delivers list of atms to display.
     *
     * @param atmNodes - atms to be displayed.
     */
    fun onGotAtms(atmNodes: List<AtmNode>)

    /**
     * Shows intermediate progress.
     */
    fun showProgress()

    /**
     * Hides intemediate progress.
     */
    fun hideProgress()

    /**
     * Shows user-friendly error nessage.
     *
     * @param errorMsg - message displayed to user.
     */
    fun showError(errorMsg: String)

    /**
     * Shows plz zoom further info dialog.
     */
    fun showZoomInFurther()

    /**
     * Hides plz zoom further info dialog.
     */
    fun hideZoomInFurther()

    /**
     * Shows map tab and hides list and settings tabs.
     */
    fun showMap()

    /**
     * Shows list tab and hides map and settings tabs.
     */
    fun showList()

    /**
     * Shows settings tab and hides map and list tabs.
     */
    fun showSettings()
}
