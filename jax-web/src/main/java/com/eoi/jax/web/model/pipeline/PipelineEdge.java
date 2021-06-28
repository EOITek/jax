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

package com.eoi.jax.web.model.pipeline;

public class PipelineEdge {
    private String from;
    private String to;
    private Integer fromSlot;
    private Integer toSlot;
    private Boolean enableMock;
    private String mockId;

    public String getFrom() {
        return from;
    }

    public PipelineEdge setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public PipelineEdge setTo(String to) {
        this.to = to;
        return this;
    }

    public Integer getFromSlot() {
        return fromSlot;
    }

    public PipelineEdge setFromSlot(Integer fromSlot) {
        this.fromSlot = fromSlot;
        return this;
    }

    public Integer getToSlot() {
        return toSlot;
    }

    public PipelineEdge setToSlot(Integer toSlot) {
        this.toSlot = toSlot;
        return this;
    }

    public Boolean getEnableMock() {
        return enableMock;
    }

    public PipelineEdge setEnableMock(Boolean enableMock) {
        this.enableMock = enableMock;
        return this;
    }

    public String getMockId() {
        return mockId;
    }

    public PipelineEdge setMockId(String mockId) {
        this.mockId = mockId;
        return this;
    }
}
