package com.neocaptainnemo.atms.service

import com.neocaptainnemo.atms.model.AtmNode
import javax.inject.Inject

class AddressFormatter @Inject constructor() {

    fun format(atmNode: AtmNode): String {
        val address = StringBuilder()

        if (atmNode.tags!!.street != null) {
            address.append(atmNode.tags!!.street)
        }

        if (atmNode.tags!!.houseNumber != null) {
            address.append(' ')
            address.append(atmNode.tags!!.houseNumber)
        }

        if (atmNode.tags!!.postCode != null) {
            address.append(',')
            address.append(' ')
            address.append(atmNode.tags!!.postCode)
        }

        if (atmNode.tags!!.city != null) {
            address.append(',')
            address.append(' ')
            address.append(atmNode.tags!!.city)
        }

        var addressStr = address.toString().trim { it <= ' ' }

        if (addressStr.startsWith(",")) {
            addressStr = addressStr.substring(1)
        }

        if (addressStr.endsWith(",")) {
            addressStr = addressStr.substring(0, addressStr.length - 1)
        }

        return addressStr.trim({ it <= ' ' })

    }

}
