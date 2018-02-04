package mayuresh.com.retrofitjson;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by mayureshjakhotia on 2/3/18.
 */


/*
 * NOT Used.
 * But can be used to generalize one common function for OBJECTS & ARRAYS by identifying the response
 * & converting them in appropriate format.
 */
final class ResponseExtractorTypeAdapterFactory implements TypeAdapterFactory {

    private final Gson gson;

    private ResponseExtractorTypeAdapterFactory(final Gson gson) {
        this.gson = gson;
    }

    static TypeAdapterFactory getResponseExtractorTypeAdapterFactory(final Gson gson) {
        return new ResponseExtractorTypeAdapterFactory(gson);
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson responseGson, final TypeToken<T> typeToken) {
        // Using responseGson would result in infinite recursion since this type adapter factory overrides any type
        return new ResponseExtractorTypeAdapter<>(gson, typeToken.getType());
    }

    private static final class ResponseExtractorTypeAdapter<T>
            extends TypeAdapter<T> {

        private final Gson gson;
        private final Type type;

        private ResponseExtractorTypeAdapter(final Gson gson, final Type type) {
            this.gson = gson;
            this.type = type;
        }

        @Override
        public void write(final JsonWriter out, final T value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T read(final JsonReader in)
                throws IOException {
            System.out.println("Hello"+in.peek());
            T result = null;
            if (in.peek() == JsonToken.BEGIN_ARRAY) {
                System.out.println("Hello, I'm an ARRAY");
                result =  gson.fromJson(in, type);
            } else if(in.peek() == JsonToken.BEGIN_OBJECT) {
                System.out.println("Hola, I'm an OBJECT");
                result =  gson.fromJson(in, type);
            } else {
                throw new JsonParseException("Unexpected token " + in.peek());
            }

            return result;
        }

    }

}