public interface Action {
    // This interface describes an action such as deletion or insertion

    // Undo this action
    void undo();

    // Redo this action
    void redo();
}
