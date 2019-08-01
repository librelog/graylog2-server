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
package org.graylog.events.processor;

import org.graylog.events.event.EventProcessorEventFactory;
import org.graylog.events.event.EventWithContext;
import org.graylog.events.fields.EventFieldSpecEngine;
import org.graylog.events.fields.FieldValue;
import org.graylog.events.notifications.EventNotificationHandler;
import org.graylog.events.processor.storage.EventStorageHandlerEngine;
import org.graylog.events.processor.storage.EventStorageHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class EventProcessorEngine {
    private static final Logger LOG = LoggerFactory.getLogger(EventProcessorEngine.class);

    private final DBEventDefinitionService dbService;
    private final Map<String, ? extends EventProcessor.Factory> eventProcessorFactories;
    private final EventFieldSpecEngine fieldSpecEngine;
    private final EventNotificationHandler notificationHandler;
    private final EventStorageHandlerEngine storageHandlerEngine;
    private final Provider<EventProcessorEventFactory> eventFactoryProvider;

    @Inject
    public EventProcessorEngine(Map<String, EventProcessor.Factory> eventProcessorFactories,
                                DBEventDefinitionService dbService,
                                EventFieldSpecEngine fieldSpecEngine,
                                EventNotificationHandler notificationHandler,
                                EventStorageHandlerEngine storageHandlerEngine,
                                Provider<EventProcessorEventFactory> eventFactoryProvider) {
        this.dbService = dbService;
        this.eventProcessorFactories = eventProcessorFactories;
        this.fieldSpecEngine = fieldSpecEngine;
        this.notificationHandler = notificationHandler;
        this.storageHandlerEngine = storageHandlerEngine;
        this.eventFactoryProvider = eventFactoryProvider;
    }

    private EventDefinition getEventDefinition(String id) throws EventProcessorException {
        return dbService.get(id)
                .orElseThrow(() -> new EventProcessorException("Event definition <" + id + "> doesn't exist", true, id));
    }

    // TODO: Implement stop/cancel for event processors to make sure we can gracefully shutdown the server
    public void execute(String definitionId, EventProcessorParameters parameters) throws EventProcessorException {
        final EventDefinition definition = getEventDefinition(definitionId);
        final EventProcessor.Factory factory = eventProcessorFactories.get(definition.config().type());

        if (factory == null) {
            throw new EventProcessorException("Couldn't find event processor factory for type " + definition.config().type(), true, definitionId, definition);
        }

        LOG.debug("Executing event processor <{}/{}/{}>", definition.title(), definition.id(), definition.config().type());

        final EventProcessor eventProcessor = factory.create(definition);
        final EventConsumer<List<EventWithContext>> eventConsumer = eventsWithContext -> emitEvents(definition, eventsWithContext);

        try {
            eventProcessor.createEvents(eventFactoryProvider.get(), parameters, eventConsumer);
        } catch (EventProcessorException e) {
            // We can just re-throw the exception because we already got an EventProcessorException
            throw e;
        } catch (Exception e) {
            LOG.error("Caught an unhandled exception while executing event processor <{}/{}/{}> - Make sure to modify the event processor to throw only EventProcessorExecutionException so we get more context!",
                    definition.config().type(), definition.title(), definition.id(), e);
            // Since we don't know what kind of error this is, we play safe and make this a temporary error.
            throw new EventProcessorException("Couldn't create events for: " + definition.toString(), false, definition, e);
        }
    }

    private void emitEvents(EventDefinition eventDefinition, List<EventWithContext> eventsWithContext) throws EventProcessorException {
        if (eventsWithContext.isEmpty()) {
            return;
        }
        try {
            // Field spec needs to be executed first to make sure all fields are set before executing the handlers
            fieldSpecEngine.execute(eventsWithContext, eventDefinition.fieldSpec());

            // We can only set the key when the field spec is done
            eventsWithContext.forEach(eventWithContext -> {
                final List<String> keyTuple = eventDefinition.keySpec().stream()
                        .map(fieldName -> eventWithContext.event().getField(fieldName))
                        .filter(Objects::nonNull)
                        .filter(fieldValue -> !fieldValue.isError())
                        .map(FieldValue::value)
                        .collect(Collectors.toList());

                // TODO: What should we do in this case? Set no key at all? Set an incomplete key?
                if (keyTuple.size() != eventDefinition.keySpec().size()) {
                    LOG.warn("Key spec <{}> for event <{}> cannot be fulfilled", eventDefinition.keySpec(), eventWithContext.event());
                }

                eventWithContext.event().setKeyTuple(keyTuple);
            });

            // First process notifications - those might modify the events
            notificationHandler.handleEvents(eventDefinition, eventsWithContext);

            // After that process storage which persist the events into a storage system
            try {
                storageHandlerEngine.handleEvents(eventsWithContext, eventDefinition.storage());
            } catch (EventStorageHandlerException e) {
                throw new EventProcessorException("Failed to execute storage handlers", false, eventDefinition, e);
            }
        } catch (Exception e) {
            throw new EventProcessorException("Couldn't emit events for: " + eventDefinition.toString(), false, eventDefinition, e);
        }
    }
}