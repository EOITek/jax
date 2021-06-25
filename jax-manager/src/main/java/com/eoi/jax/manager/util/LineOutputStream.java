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

package com.eoi.jax.manager.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class LineOutputStream extends OutputStream {
    private ByteArrayOutputStream buffer;
    private StreamLineReader reader;

    public LineOutputStream(StreamLineReader reader) {
        this.buffer = new ByteArrayOutputStream(4096);
        this.reader = reader;
    }

    @Override
    public void write(int b) {
        if (b == '\r' || b == '\n') {
            String line = buffer.toString();
            reader.readLine(line);
            buffer.reset();
        } else {
            buffer.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        for (int i = off; i < len; i++) {
            write(b[i]);
        }
    }
}
