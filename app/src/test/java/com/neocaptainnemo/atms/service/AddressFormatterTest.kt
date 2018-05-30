package com.neocaptainnemo.atms.service

import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.Tag
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddressFormatterTest {

    var formatter = AddressFormatter()

    @Test
    fun hasCityHouseNumberStreetZip() {

        val atmNode = AtmNode()
        val tag = Tag()
        tag.city = "City"
        tag.houseNumber = "8"
        tag.postCode = "12345678"
        tag.street = "Street"

        atmNode.tags = tag
        assertThat(formatter.format(atmNode)).isEqualTo("Street 8, 12345678, City")
    }

    @Test
    fun hasCity() {

        val atmNode = AtmNode()
        val tag = Tag()
        tag.city = "City"

        atmNode.tags = tag
        assertThat(formatter.format(atmNode)).isEqualTo("City")
    }

    @Test
    fun hasCityHouseNumberStreet() {

        val atmNode = AtmNode()
        val tag = Tag()
        tag.city = "City"
        tag.houseNumber = "8"
        tag.street = "Street"

        atmNode.tags = tag
        assertThat(formatter.format(atmNode)).isEqualTo("Street 8, City")
    }

    @Test
    fun hasHouseNumberStreetZip() {

        val atmNode = AtmNode()
        val tag = Tag()
        tag.houseNumber = "8"
        tag.postCode = "12345678"
        tag.street = "Street"

        atmNode.tags = tag
        assertThat(formatter.format(atmNode)).isEqualTo("Street 8, 12345678")
    }

    @Test
    fun hasCityStreetZip() {

        val atmNode = AtmNode()
        val tag = Tag()
        tag.city = "City"
        tag.postCode = "12345678"
        tag.street = "Street"

        atmNode.tags = tag
        assertThat(formatter.format(atmNode)).isEqualTo("Street, 12345678, City")
    }

    @Test
    fun hasCityStreet() {

        val atmNode = AtmNode()
        val tag = Tag()
        tag.city = "City"
        tag.street = "Street"

        atmNode.tags = tag
        assertThat(formatter.format(atmNode)).isEqualTo("Street, City")
    }


    @Test
    fun hasNothing() {

        val atmNode = AtmNode()
        val tag = Tag()

        atmNode.tags = tag
        assertThat(formatter.format(atmNode)).isEmpty()
    }
}
