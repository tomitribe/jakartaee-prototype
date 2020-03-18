package transformer.test.util;

import java.io.PrintWriter;
import java.util.Set;

public interface BiDiMap<Holder, Held> {
    String getHashText();
    void log(PrintWriter writer);

    //

    Class<Holder> getHolderClass();
    String getHolderTag();

    Class<Held> getHeldClass();
    String getHeldTag();

    //

    boolean isEmpty();
    boolean holds(Holder holder, Held held);

    boolean isHolder(Holder hold);
    Set<Holder> getHolders();
    Set<Held> getHeld(Holder holder);

    boolean isHeld(Held held);
    Set<Held> getHeld();
    Set<Holder> getHolders(Held held);

    //

	boolean record(Holder holder, Held held);

	<OtherHolder extends Holder, OtherHeld extends Held>
	    void record(BiDiMap<OtherHolder, OtherHeld> otherMap);
	
	<OtherHolder extends Holder, OtherHeld extends Held>
	    void record(BiDiMap<OtherHolder, OtherHeld> otherMap,
			        Set<? extends Holder> restrictedHolders);

	//

	<OtherHolder extends Holder, OtherHeld extends Held>
	    boolean sameAs(BiDiMap<OtherHolder, OtherHeld> otherMap);
}
