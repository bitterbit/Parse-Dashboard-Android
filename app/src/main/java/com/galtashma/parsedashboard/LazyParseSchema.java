package com.galtashma.parsedashboard;

import com.galtashma.lazyparse.LazyParseObject;
import com.parse.ParseClassName;
import com.parse.ParseSchema;
import com.parse.ParseSchemaQuery;

/**
 * Created by gal on 3/9/18.
 */

@ParseClassName("_Lazy_Schema")
public class LazyParseSchema extends ParseSchema implements LazyParseObject{
    LazyParseSchema() {
        super();
    }

    static LazyParseSchemaQuery getQuery(){
        return new LazyParseSchemaQuery();
    }

    static class LazyParseSchemaQuery extends ParseSchemaQuery<LazyParseSchema> {
        LazyParseSchemaQuery(){
            super("_Lazy_Schema");
        }
    }
}
