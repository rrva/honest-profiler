package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.slf4j.LoggerFactory;
import org.webbitserver.WebSocketConnection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

// Not thread safe
public class MachineAdapter implements MachineListener, Consumer<WebSocketConnection> {

    private final Set<VirtualMachine> machines;
    private final ClientConnections clients;
    private final MessageEncoder messages;

    public MachineAdapter(ClientConnections clients, MessageEncoder messages) {
        this.clients = clients;
        this.messages = messages;
        machines = new HashSet<>();
        clients.setListener(this);
    }

    @Override
    public void accept(WebSocketConnection connection) {
        machines.forEach(machine -> {
            connection.send(messages.addJavaVirtualMachine(machine));
        });
    }

    @Override
    public ProfileAdapter onNewMachine(VirtualMachine machine) {
        machines.add(machine);
        clients.sendAll(messages.addJavaVirtualMachine(machine));
        return new ProfileAdapter(LoggerFactory.getLogger(ProfileAdapter.class), machine, clients);
    }

    @Override
    public void onClosedMachine(VirtualMachine machine) {
        machines.remove(machine);
        clients.sendAll(messages.removeJavaVirtualMachine(machine));
    }

}
