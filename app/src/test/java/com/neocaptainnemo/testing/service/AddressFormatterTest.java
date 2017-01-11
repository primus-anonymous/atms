package com.neocaptainnemo.testing.service;

import com.neocaptainnemo.testing.model.AtmNode;
import com.neocaptainnemo.testing.model.Tag;
import com.neocaptainnemo.testing.service.AddressFormatter;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddressFormatterTest {

    AddressFormatter formatter = new AddressFormatter();

    @Test
    public void hasCityHouseNumberStreetZip() {

        AtmNode atmNode = new AtmNode();
        Tag tag = new Tag();
        tag.setCity("City");
        tag.setHouseNumber("8");
        tag.setPostCode("12345678");
        tag.setStreet("Street");

        atmNode.setTag(tag);
        Assertions.assertThat(formatter.format(atmNode)).isEqualTo("Street 8, 12345678, City");
    }

    @Test
    public void hasCity() {

        AtmNode atmNode = new AtmNode();
        Tag tag = new Tag();
        tag.setCity("City");

        atmNode.setTag(tag);
        Assertions.assertThat(formatter.format(atmNode)).isEqualTo("City");
    }

    @Test
    public void hasCityHouseNumberStreet() {

        AtmNode atmNode = new AtmNode();
        Tag tag = new Tag();
        tag.setCity("City");
        tag.setHouseNumber("8");
        tag.setStreet("Street");

        atmNode.setTag(tag);
        Assertions.assertThat(formatter.format(atmNode)).isEqualTo("Street 8, City");
    }

    @Test
    public void hasHouseNumberStreetZip() {

        AtmNode atmNode = new AtmNode();
        Tag tag = new Tag();
        tag.setHouseNumber("8");
        tag.setPostCode("12345678");
        tag.setStreet("Street");

        atmNode.setTag(tag);
        Assertions.assertThat(formatter.format(atmNode)).isEqualTo("Street 8, 12345678");
    }

    @Test
    public void hasCityStreetZip() {

        AtmNode atmNode = new AtmNode();
        Tag tag = new Tag();
        tag.setCity("City");
        tag.setPostCode("12345678");
        tag.setStreet("Street");

        atmNode.setTag(tag);
        Assertions.assertThat(formatter.format(atmNode)).isEqualTo("Street, 12345678, City");
    }

    @Test
    public void hasCityStreet() {

        AtmNode atmNode = new AtmNode();
        Tag tag = new Tag();
        tag.setCity("City");
        tag.setStreet("Street");

        atmNode.setTag(tag);
        Assertions.assertThat(formatter.format(atmNode)).isEqualTo("Street, City");
    }


    @Test
    public void hasNothing() {

        AtmNode atmNode = new AtmNode();
        Tag tag = new Tag();

        atmNode.setTag(tag);
        Assertions.assertThat(formatter.format(atmNode)).isEmpty();
    }
}
