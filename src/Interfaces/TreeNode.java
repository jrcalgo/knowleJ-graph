package src.Interfaces;

public interface TreeNode<T extends Comparable<T>> {
    public void setValue(T value);
    public T getValue();
    public void setRight(TreeNode<T> right);
    public void setLeft(TreeNode<T> left);
    public TreeNode<T> getRight();
    public TreeNode<T> getLeft();
}
