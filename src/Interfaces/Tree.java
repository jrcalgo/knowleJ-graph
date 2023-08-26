package src.Interfaces;

public interface Tree<T extends Comparable<T>> {
    public boolean isEmpty();

    public int size();

    public void insert(T element);
    
    public boolean contains(T element) throws Exception;

    public T search(T element);

    public void delete(T element);

    public String traverseInOrder();

    public String traversePreOrder();

    public String traversePostOrder();

}
