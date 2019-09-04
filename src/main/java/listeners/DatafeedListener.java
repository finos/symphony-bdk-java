package listeners;

public interface DatafeedListener {

    default boolean ignoreSelf(){
        return true;
    }
}
