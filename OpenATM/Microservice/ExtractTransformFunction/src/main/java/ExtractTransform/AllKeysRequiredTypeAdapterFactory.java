package ExtractTransform;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

final class AllKeysRequiredTypeAdapterFactory
        implements TypeAdapterFactory {

    private static final TypeAdapterFactory allKeysRequiredTypeAdapterFactory = new AllKeysRequiredTypeAdapterFactory();

    private AllKeysRequiredTypeAdapterFactory() {
    }

    static TypeAdapterFactory get() {
        return allKeysRequiredTypeAdapterFactory;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        @SuppressWarnings("unchecked")
        final Class<T> rawType = (Class<T>) typeToken.getRawType();
        // Or any other way you would like to determine if the given class is fine to be validated
        if ( !rawType.isAnnotationPresent(ExtractTransform.AllKeysRequired.class) ) {
            return null;
        }
        final TypeAdapter<T> delegateTypeAdapter = gson.getDelegateAdapter(this, typeToken);
        final TypeAdapter<JsonElement> jsonElementTypeAdapter = gson.getAdapter(JsonElement.class);
        return AllKeysRequiredTypeAdapter.of(gson, rawType, delegateTypeAdapter, jsonElementTypeAdapter);
    }

    private static final class AllKeysRequiredTypeAdapter<T>
            extends TypeAdapter<T> {

        // This is for the cache below
        private final JsonPropertiesCacheKey jsonPropertiesCacheKey;
        private final TypeAdapter<T> delegateTypeAdapter;
        private final TypeAdapter<JsonElement> jsonElementTypeAdapter;

        private AllKeysRequiredTypeAdapter(final JsonPropertiesCacheKey jsonPropertiesCacheKey, final TypeAdapter<T> delegateTypeAdapter,
                final TypeAdapter<JsonElement> jsonElementTypeAdapter) {
            this.jsonPropertiesCacheKey = jsonPropertiesCacheKey;
            this.delegateTypeAdapter = delegateTypeAdapter;
            this.jsonElementTypeAdapter = jsonElementTypeAdapter;
        }

        private static <T> TypeAdapter<T> of(final Gson gson, final Class<?> rawType, final TypeAdapter<T> delegateTypeAdapter,
                final TypeAdapter<JsonElement> jsonElementTypeAdapter) {
            return new AllKeysRequiredTypeAdapter<>(new JsonPropertiesCacheKey(gson, rawType), delegateTypeAdapter, jsonElementTypeAdapter);
        }

        @Override
        public void write(final JsonWriter jsonWriter, final T t)
                throws IOException {
            delegateTypeAdapter.write(jsonWriter, t);
        }

        @Override
        public T read(final JsonReader jsonReader)
                throws IOException {
            try {
                // First, convert it to a tree to obtain its keys
                final JsonElement jsonElement = jsonElementTypeAdapter.read(jsonReader);
                // Then validate
                validate(jsonElement);
                // And if the validation passes, then just convert the tree to the object
                return delegateTypeAdapter.read(new JsonTreeReader(jsonElement));
            } catch ( final ExecutionException ex ) {
                throw new RuntimeException(ex);
            }
        }

        private void validate(final JsonElement jsonElement)
                throws ExecutionException {
            if ( !jsonElement.isJsonObject() ) {
                throw new JsonParseException("The given tree is not a JSON object");
            }
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            final Set<String> expectedProperties = jsonPropertiesCache.get(jsonPropertiesCacheKey);
            final Set<String> actualProperties = jsonObject.keySet();


            final Set<String> difference1 = Sets.difference(actualProperties, expectedProperties);
            if ( !difference1.isEmpty() ) {
                throw new JsonParseException("The api has excess properties: " + difference1);
                // System.out.println("The JSON string event has excess properties: " + difference1);
            }

            // This method comes from Guava but can be implemented using standard JDK
            final Set<String> difference = Sets.difference(expectedProperties, actualProperties);
            if ( !difference.isEmpty() ) {
                // throw new JsonParseException("The given JSON object lacks some properties from the JSON string event: " + difference);
                System.out.println("The given JSON " + jsonObject + " checks for required expected properties " + expectedProperties + " but actual properties are " + actualProperties);

            }
        }

    }

    private static final class JsonPropertiesCacheKey {

        private final Gson gson;
        private final Class<?> rawType;

        private JsonPropertiesCacheKey(final Gson gson, final Class<?> rawType) {
            this.gson = gson;
            this.rawType = rawType;
        }

        @Override
        @SuppressWarnings("ObjectEquality")
        public boolean equals(final Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            final JsonPropertiesCacheKey jsonPropertiesCacheKey = (JsonPropertiesCacheKey) o;
            @SuppressWarnings("ObjectEquality")
            final boolean areEqual = gson == jsonPropertiesCacheKey.gson && rawType == jsonPropertiesCacheKey.rawType;
            return areEqual;
        }

        @Override
        public int hashCode() {
            return gson.hashCode() * 31 + rawType.hashCode();
        }

    }

    // private static final LookupCache<JsonPropertiesCacheKey, Set<String>> jsonPropertiesCache = CacheBuilder.newBuilder().maximumSize(50).build(new CacheLoader<JsonPropertiesCacheKey, Set<String>>() {
    //     @Override
    //     public Set<String> load(final JsonPropertiesCacheKey jsonPropertiesCacheKey)
    //             throws Exception {
    //         return JsonProperties.tryLookupKeys(jsonPropertiesCacheKey.gson, jsonPropertiesCacheKey.rawType);
    //     }
    // });

    private static final LoadingCache<JsonPropertiesCacheKey, Set<String>> jsonPropertiesCache = CacheBuilder.newBuilder().maximumSize(50).build(new CacheLoader<JsonPropertiesCacheKey, Set<String>>() {
        @Override
        public Set<String> load(final JsonPropertiesCacheKey jsonPropertiesCacheKey)
                throws Exception {
            return JsonProperties.tryLookupKeys(jsonPropertiesCacheKey.gson, jsonPropertiesCacheKey.rawType);
        }
    });

}