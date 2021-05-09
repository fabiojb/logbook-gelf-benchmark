/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.siegmar.logbackgelf;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Benchmak {

    SimpleJsonEncoder encoder;

    @Setup(Level.Trial)
    public void setup() {
        try {
            encoder = new SimpleJsonEncoder(Writer.nullWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void testEncode() throws IOException {
        encoder.appendToJSON("version", "1.1")
                .appendToJSON("host", "localhost")
                .appendToJSON("short_message", "this is a short message")
                .appendToJSON("full_message", "sKih7t7UbgCFpEGZ2Khwk5avF RnYoeFxPFuxspx0zE8EkDw8PpD9VsHKqUjHAwD")
                .appendToJSONUnquoted("timestamp", "1620179468")
                .appendToJSONUnquoted("level", 1);
    }

    @Benchmark
    public void testEncodeEscapeOne() throws IOException {
        encoder.appendToJSON("version", "1.1")
                .appendToJSON("host", "localhost")
                .appendToJSON("short_message", "this is a short message")
                .appendToJSON("full_message", "sKih7t7UbgCFpEGZ2Khwk5avF\n RnYoeFxPFuxspx0zE8EkDw8PpD9VsHKqUjHAwD")
                .appendToJSONUnquoted("timestamp", "1620179468")
                .appendToJSONUnquoted("level", 1);
    }

    @Benchmark
    public void testEncodeEscapeMany() throws IOException {
        encoder.appendToJSON("version", "1.1")
                .appendToJSON("host", "localhost")
                .appendToJSON("short_message", "this is a short message")
                .appendToJSON("full_message", "{\"glossary\":{\"title\":\"example glossary\",\"GlossDiv\":{\"title\":\"S\",\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\",\"GlossTerm\":\"Standard Generalized Markup Language\",\"Acronym\":\"SGML\",\"Abbrev\":\"ISO 8879:1986\",\"GlossDef\":{\"para\":\"A meta-markup language, used to create markup languages such as DocBook.\",\"GlossSeeAlso\":[\"GML\",\"XML\"]},\"GlossSee\":\"markup\"}}}}}\n")
                .appendToJSONUnquoted("timestamp", "1620179468")
                .appendToJSONUnquoted("level", 1);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmak.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .forks(5)
                .build();

        new Runner(opt).run();
    }
}
