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
package org.graylog.grn;

import org.graylog.grn.providers.EventDefinitionGRNDescriptorProvider;
import org.graylog.grn.providers.EventNotificationGRNDescriptorProvider;
import org.graylog.grn.providers.FallbackGRNDescriptorProvider;
import org.graylog.grn.providers.StreamGRNDescriptorProvider;
import org.graylog.grn.providers.UserGRNDescriptorProvider;
import org.graylog.grn.providers.ViewGRNDescriptorProvider;
import org.graylog2.plugin.PluginModule;

public class GRNTypesModule extends PluginModule {
    @Override
    protected void configure() {
        // TODO: Implement missing GRN descriptor providers
        addGRNType(GRNTypes.BUILTIN_TEAM, FallbackGRNDescriptorProvider.class);
        addGRNType(GRNTypes.COLLECTION, FallbackGRNDescriptorProvider.class);
        addGRNType(GRNTypes.DASHBOARD, ViewGRNDescriptorProvider.class);
        addGRNType(GRNTypes.EVENT_DEFINITION, EventDefinitionGRNDescriptorProvider.class);
        addGRNType(GRNTypes.EVENT_NOTIFICATION, EventNotificationGRNDescriptorProvider.class);
        addGRNType(GRNTypes.GRANT, FallbackGRNDescriptorProvider.class);
        addGRNType(GRNTypes.ROLE, FallbackGRNDescriptorProvider.class);
        addGRNType(GRNTypes.SEARCH, ViewGRNDescriptorProvider.class);
        addGRNType(GRNTypes.STREAM, StreamGRNDescriptorProvider.class);
        addGRNType(GRNTypes.USER, UserGRNDescriptorProvider.class);
    }
}
