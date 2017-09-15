package org.lunaris.event;

import co.aikar.timings.Timings;
import org.lunaris.server.IServer;
import org.lunaris.util.exception.EventExecutionException;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/**
 * Created by RINES on 13.09.17.
 */
public class EventManager {

    private Map<Class<Event>, Set<Handler>> HANDLERS = new ConcurrentHashMap<>();

    private IServer server;

    public EventManager(IServer server) {
        this.server = server;
    }

    public void call(Event event) {
        Set<Handler> handlers = HANDLERS.get(event.getClass());
        if(handlers == null)
            return;
        Timings.eventTimer.startTiming();
        Cancellable cancellable = event instanceof Cancellable ? (Cancellable) event : null;
        try {
            for(Handler handler : handlers)
                if(!handler.ignoreCancelled || cancellable == null || !cancellable.isCancelled())
                    handler.consumer.accept(event);
        }catch(Exception ex) {
            new EventExecutionException(ex).printStackTrace();
        }
        Timings.eventTimer.stopTiming();
    }

    public void register(Listener listener) {
        Class<? extends Listener> clazz = listener.getClass();
        Class<Event> event = Event.class;
        for(Method m : clazz.getDeclaredMethods()) {
            if(m.getParameterCount() != 1 || !m.isAnnotationPresent(EventHandler.class))
                continue;
            Class<?> param = m.getParameterTypes()[0];
            if(!event.isAssignableFrom(param))
                continue;
            Set<Handler> handlers = HANDLERS.get(param);
            if(handlers == null) {
                handlers = new ConcurrentSkipListSet<>(Comparator.comparingInt(Handler::priority));
                HANDLERS.put((Class<Event>) param, handlers);
            }
            EventHandler annotation = m.getAnnotation(EventHandler.class);
            handlers.add(new Handler(annotation.priority(), annotation.ignoreCancelled(), constructConsumer(listener, m)));
        }
    }

    private Consumer<Event> constructConsumer(Listener listener, Method method) {
        try {
            MethodHandles.Lookup lookup = constructLookup(listener.getClass());
            return (Consumer<Event>) LambdaMetafactory.metafactory(
                    lookup,
                    "accept",
                    MethodType.methodType(Consumer.class, listener.getClass()),
                    MethodType.methodType(void.class, Object.class),
                    lookup.unreflect(method),
                    MethodType.methodType(void.class, method.getParameterTypes()[0])
            ).getTarget().invoke(listener);
        }catch(Throwable t) {
            this.server.getLogger().error(t, "Can not construct event handler consumer");
            return null;
        }
    }

    private MethodHandles.Lookup constructLookup(Class<?> owner) throws Exception {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(owner);
        }finally {
            constructor.setAccessible(false);
        }
    }

    private static class Handler {

        private final EventPriority priority;
        private final boolean ignoreCancelled;
        private final Consumer<Event> consumer;

        Handler(EventPriority priority, boolean ignoreCancelled, Consumer<Event> consumer) {
            this.priority = priority;
            this.ignoreCancelled = ignoreCancelled;
            this.consumer = consumer;
        }

        private int priority() {
            return this.priority.ordinal();
        }

    }

}
