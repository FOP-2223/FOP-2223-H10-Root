package h10;

import java.util.Objects;

/**
 * A node that keeps track of whether it has been visited.
 *
 * @param <T> the type of the value of the node
 *
 * @author Nhan Huynh
 */
class VisitorNode<T> {

    /**
     * The value of this node.
     */
    private final T value;

    /**
     * Whether this node has been visited.
     */
    private boolean visited;

    /**
     * Creates a new node with the given value.
     *
     * @param value the value of the node
     */
    public VisitorNode(T value) {
        this.value = value;
        this.visited = false;
    }


    /**
     * Returns the value of this node.
     *
     * @return the value of this node
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns whether this node has been visited.
     *
     * @return whether this node has been visited
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Marks this node as visited.
     */
    public void visit() {
        visited = true;
    }

    /**
     * Marks this node as unvisited.
     */
    public void unvisit() {
        visited = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VisitorNode<?> that = (VisitorNode<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("{%s|%s}", value, visited ? "v" : "uv");
    }

}
