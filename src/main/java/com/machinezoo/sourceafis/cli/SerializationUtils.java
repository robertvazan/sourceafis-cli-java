// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.cbor.*;
import com.machinezoo.noexception.*;
import one.util.streamex.*;

class SerializationUtils {
	private static final ObjectMapper mapper = new ObjectMapper(new CBORFactory())
		.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	static byte[] serialize(Object value) {
		return Exceptions.sneak().get(() -> mapper.writeValueAsBytes(value));
	}
	static <T> T deserialize(byte[] serialized, Class<T> clazz) {
		return Exceptions.sneak().get(() -> mapper.readValue(serialized, clazz));
	}
	private static void normalize(OutputStream stream, JsonNode node) throws IOException {
		switch (node.getNodeType()) {
		case OBJECT:
			stream.write(0xBF);
			for (var key : StreamEx.of(node.fieldNames()).sorted()) {
				stream.write(0x78);
				var utf = key.getBytes(StandardCharsets.UTF_8);
				stream.write(utf.length);
				stream.write(utf);
				normalize(stream, node.get(key));
			}
			stream.write(0xFF);
			break;
		case ARRAY:
			stream.write(0x9F);
			for (int i = 0; i < node.size(); ++i)
				normalize(stream, node.get(i));
			stream.write(0xFF);
			break;
		case NUMBER:
			switch (node.numberType()) {
			case INT:
			case LONG:
				if (node.longValue() >= 0) {
					stream.write(0x1B);
					stream.write(ByteBuffer.allocate(8).putLong(node.longValue()).array());
				} else {
					stream.write(0x3B);
					stream.write(ByteBuffer.allocate(8).putLong(-node.longValue()).array());
				}
				break;
			case FLOAT:
			case DOUBLE:
				stream.write(0xFB);
				stream.write(ByteBuffer.allocate(8).putDouble(node.doubleValue()).array());
				break;
			default:
				throw new IllegalArgumentException();
			}
			break;
		case BINARY:
			stream.write(0x5A);
			stream.write(ByteBuffer.allocate(4).putInt(node.binaryValue().length).array());
			stream.write(node.binaryValue());
			break;
		case STRING:
			var utf = node.textValue().getBytes(StandardCharsets.UTF_8);
			stream.write(0x7A);
			stream.write(ByteBuffer.allocate(4).putInt(utf.length).array());
			stream.write(utf);
			break;
		case BOOLEAN:
			if (node.booleanValue())
				stream.write(0xF5);
			else
				stream.write(0xF4);
			break;
		case NULL:
			stream.write(0xF6);
			break;
		case MISSING:
		case POJO:
		default:
			throw new IllegalArgumentException();
		}
	}
	static byte[] normalize(byte[] denormalized) {
		return Exceptions.sneak().get(() -> {
			var root = mapper.readTree(denormalized);
			var buffer = new ByteArrayOutputStream();
			normalize(buffer, root);
			return buffer.toByteArray();
		});
	}
	static byte[] normalize(String mime, byte[] data) {
		if (mime.equals("application/cbor"))
			return normalize(data);
		return data;
	}
}
