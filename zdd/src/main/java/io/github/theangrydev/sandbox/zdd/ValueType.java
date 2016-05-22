package io.github.theangrydev.sandbox.zdd;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static java.util.Collections.emptySet;

public class ValueType {

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object, emptySet());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, emptySet());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
