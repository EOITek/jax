/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.flink.job.process;

import com.eoi.jax.flink.job.common.AvroDecoderJobConfig;
import com.eoi.jax.flink.job.common.DecoderFunction;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.flink.formats.avro.utils.MutableByteArrayInputStream;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AvroDecoderFunction extends DecoderFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroDecoderFunction.class);

    private AvroDecoderJobConfig config;
    final OutputTag<Map<String, Object>> errorTag;

    /**
     * Reader that deserializes byte array into a record.
     */
    private transient GenericDatumReader<GenericRecord> datumReader;

    /**
     * Input stream to read message from.
     */
    private transient MutableByteArrayInputStream inputStream;

    /**
     * Avro decoder that decodes binary data.
     */
    private transient Decoder decoder;

    /**
     * Avro schema for the reader.
     */
    private transient Schema reader;

    public AvroDecoderFunction(AvroDecoderJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        super(config, errorTag);
        this.config = config;
        this.errorTag = errorTag;
        this.reader = new Schema.Parser().parse(config.getAvroSchema());
    }

    @Override
    protected Object decodedValue(byte[] bytes) throws Exception {
        GenericRecord genericRecord = deserialize(bytes);
        Map<String, Object> avroResultMap = new HashMap<>();
        genericRecord.getSchema().getFields().forEach(field -> {
            Object val = genericRecord.get(field.name());
            if (val != null && val.getClass() == org.apache.avro.util.Utf8.class) {
                avroResultMap.put(field.name(), val.toString());
            } else {
                avroResultMap.put(field.name(), val);
            }
        });
        return avroResultMap;
    }

    GenericRecord deserialize(byte[] message) throws Exception {
        // read record
        checkAvroInitialized();
        inputStream.setBuffer(message);
        Schema readerSchema = reader;

        datumReader.setSchema(readerSchema);

        GenericRecord result = null;
        result = datumReader.read(null, decoder);

        return result;
    }

    void checkAvroInitialized() {
        if (datumReader != null) {
            return;
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (SpecificRecord.class.isAssignableFrom(GenericRecord.class)) {
            SpecificData specificData = new SpecificData(cl);
            this.datumReader = new SpecificDatumReader<>(specificData);
            this.reader = specificData.getSchema(GenericRecord.class);
        } else {
            this.reader = new Schema.Parser().parse(config.getAvroSchema());
            GenericData genericData = new GenericData(cl);
            this.datumReader = new GenericDatumReader<>(null, this.reader, genericData);
        }

        this.inputStream = new MutableByteArrayInputStream();
        this.decoder = DecoderFactory.get().binaryDecoder(inputStream, null);
    }
}
