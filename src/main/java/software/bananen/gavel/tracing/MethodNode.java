package software.bananen.gavel.tracing;

import java.util.ArrayList;
import java.util.Collection;

public final class MethodNode {

    private final String name;

    private Collection<MethodNode> accessTo = new ArrayList<>();
    private Collection<MethodNode> accessFrom = new ArrayList<>();

    MethodNode(final String name) {
        this.name = name;
    }

    public void addAccessTo(final MethodNode methodNode) {
        accessTo.add(methodNode);
    }

    public void addAccessFrom(final MethodNode methodNode) {
        accessFrom.add(methodNode);
    }

    @Override
    public String toString() {
        return name + " | access to " + accessTo + " | access from " + accessFrom;
    }
}
