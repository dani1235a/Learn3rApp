package group7.tcss450.uw.edu.uilearner.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<String> ITEMS = new ArrayList<String>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<java.lang.String, String> ITEM_MAP = new HashMap<java.lang.String, String>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(String item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static String createDummyItem(int position) {
        return new String(java.lang.String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static java.lang.String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class String {
        public final java.lang.String id;
        public final java.lang.String content;
        public final java.lang.String details;

        public String(java.lang.String id, java.lang.String content, java.lang.String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public java.lang.String toString() {
            return content;
        }
    }
}
