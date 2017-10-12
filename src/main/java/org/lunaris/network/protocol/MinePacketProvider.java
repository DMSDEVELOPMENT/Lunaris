package org.lunaris.network.protocol;

import org.lunaris.entity.LPlayer;
import org.lunaris.event.EventManager;
import org.lunaris.event.network.PacketReceivedAsyncEvent;
import org.lunaris.network.NetworkManager;
import org.lunaris.server.IServer;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by RINES on 13.09.17.
 */
public class MinePacketProvider {

    private final MinePacketHandler handler;
    private final Map<Byte, PacketLookup> lookups = new HashMap<>();
    private final EventManager eventManager;

    public MinePacketProvider(IServer server, NetworkManager networkManager) {
        this.handler = new MinePacketHandler(networkManager);
        this.eventManager = server.getEventManager();
        generateLookups();
    }

    public boolean handle(byte packetID, MineBuffer buffer, LPlayer sender) {
        try {
            PacketLookup lookup = this.lookups.get(packetID);
            if(lookup == null) {
                System.out.println("Can not handle unknown packet with id " + String.format("0x%02X", packetID));
                return false;
            }
            MinePacket packet = lookup.generator.get();
            packet.setPlayer(sender);
            packet.read(buffer);
            try {
                PacketReceivedAsyncEvent event = new PacketReceivedAsyncEvent(packet);
                this.eventManager.call(event);
                if(event.isCancelled())
                    return true;
                lookup.handler.accept(packet);
                return true;
            }catch(Exception ex) {
                throw new Exception("Can not handle packet " + packet.getClass().getSimpleName(), ex);
            }
        }catch(Exception ex) {
            throw new IllegalStateException("Something is wrong with packet handling (" + String.format("0x%02X", packetID) + ")", ex);
        }
    }

    public void handle(MinePacket packet) {
        try {
            PacketLookup lookup = this.lookups.get(packet.getByteId());
            try {
                PacketReceivedAsyncEvent event = new PacketReceivedAsyncEvent(packet);
                this.eventManager.call(event);
                if(event.isCancelled())
                    return;
                lookup.handler.accept(packet);
            }catch(Exception ex) {
                throw new Exception("Can not handle packet " + packet.getClass().getSimpleName(), ex);
            }
        }catch(Exception ex) {
            throw new IllegalStateException("Something is wrong with packet handling (" + String.format("0x%02X", packet.getByteId()) + ")", ex);
        }
    }

    public MinePacket getPacket(byte packetID, MineBuffer buffer) {
        try {
            MinePacket packet = this.lookups.get(packetID).generator.get();
            packet.read(buffer);
            return packet;
        }catch(Exception ex) {
            throw new IllegalStateException("Can not generate packet by given id: " + packetID, ex);
        }
    }

    private void generateLookups() {
        try {
            Class<?> handlerClazz = this.handler.getClass();
            Class<MinePacket> packet = MinePacket.class;
            for(Method method : handlerClazz.getDeclaredMethods())
                if((method.getModifiers() & Modifier.PUBLIC) != 0 && method.getParameterCount() == 1) {
                    Class<MinePacket> parameter = (Class<MinePacket>) method.getParameterTypes()[0];
                    if(!packet.isAssignableFrom(parameter))
                        continue;
                    Supplier<MinePacket> generator = constructGenerator(parameter);
                    Consumer<MinePacket> handler = constructHandler(handlerClazz, method);
                    this.lookups.put(parameter.newInstance().getByteId(), new PacketLookup(generator, handler));
                }
        }catch(Throwable ex) {
            throw new IllegalStateException("Can not generate lookups mine packets", ex);
        }
    }

    private Supplier<MinePacket> constructGenerator(Class<MinePacket> parameter) throws Exception {
        Supplier<MinePacket> generator = () -> {
            try {
                return parameter.newInstance();
            }catch(Exception ex) {
                ex.printStackTrace();
                return null;
            }
        };
        return generator;
    }

    private Consumer<MinePacket> constructHandler(Class<?> handlerClazz, Method method) throws Throwable {
        MethodHandles.Lookup lookup = constructLookup(handlerClazz);
        return (Consumer<MinePacket>) LambdaMetafactory.metafactory(
                lookup,
                "accept",
                MethodType.methodType(Consumer.class, handlerClazz),
                MethodType.methodType(void.class, Object.class),
                lookup.unreflect(method),
                MethodType.methodType(void.class, method.getParameterTypes()[0])
        ).getTarget().invoke(this.handler);
    }

    private MethodHandles.Lookup constructLookup(Class<?> handlerClazz) throws Exception {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(handlerClazz);
        }finally {
            constructor.setAccessible(false);
        }
    }

    private static class PacketLookup {

        private final Supplier<MinePacket> generator;

        private final Consumer<MinePacket> handler;

        private PacketLookup(Supplier<MinePacket> generator, Consumer<MinePacket> handler) {
            this.generator = generator;
            this.handler = handler;
        }

    }

}
