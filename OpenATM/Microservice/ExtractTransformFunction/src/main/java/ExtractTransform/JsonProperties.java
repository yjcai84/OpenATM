/**
 * Name: Cai Yuejun Leon
 */
package ExtractTransform;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.UnsafeAllocator;
public class JsonProperties {

    private static final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

    /**
     * @param gson This Gson instance must have be initialized with {@link GsonBuilder#serializeNulls()}
     */
    public static Set<String> tryLookupKeys(final Gson gson, final Class<?> clazz)
            throws Exception {
        final Object o = unsafeAllocator.newInstance(clazz);
        final JsonElement jsonElement = gson.toJsonTree(o, clazz);
        if ( !jsonElement.isJsonObject() ) {
            throw new IllegalArgumentException(clazz + " cannot be converted to a JSON object");
        }
        return jsonElement.getAsJsonObject().keySet();
    }
}
