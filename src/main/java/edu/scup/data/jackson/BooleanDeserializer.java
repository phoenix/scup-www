package edu.scup.data.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class BooleanDeserializer extends StdScalarDeserializer<Boolean> {
    private static final long serialVersionUID = 1L;

    public BooleanDeserializer() {
        super(Boolean.class);
    }

    @Override
    public Boolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if (StringUtils.equals("1", text)) {
                return true;
            }
            if (StringUtils.equals("0", text)) {
                return false;
            }
        }
        return _parseBooleanPrimitive(jp, ctxt);
    }
}
