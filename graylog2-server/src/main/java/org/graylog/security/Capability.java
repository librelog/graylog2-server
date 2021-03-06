/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

public enum Capability {
    @JsonProperty("view")
    VIEW(1),
    @JsonProperty("manage")
    MANAGE(2),
    @JsonProperty("own")
    OWN(3);

    private final int priority;

    public int priority() {
        return priority;
    }

    Capability(int priority) {
        this.priority = priority;
    }

    public String toId() {
        return name().toLowerCase(Locale.US);
    }
}
