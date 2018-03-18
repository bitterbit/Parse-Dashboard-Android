package com.parse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/* Package */ class ParseSchemaController {

    private final ParseHttpClient restClient;

    public ParseSchemaController(ParseHttpClient restClient){
        this.restClient = restClient;
    }

    public Task<List<ParseSchema>> getSchemasInBackground(String sessionToken){
        ParseRESTCommand command = ParseRESTSchemaCommand.getParseSchemasCommand(sessionToken);
        return command.executeAsync(restClient, null).onSuccess(new Continuation<JSONObject, List<ParseSchema>>() {
            @Override
            public List<ParseSchema> then(Task<JSONObject> task) throws Exception {
                JSONObject json = task.getResult();
                JSONArray arr = json.getJSONArray("results");

                List<ParseSchema> schemas = new ArrayList<>();

                for (int i=0; i<arr.length(); i++){
                    ParseSchema schema = new ParseSchema(arr.getJSONObject(i));
                    schemas.add(schema);
                }

                return schemas;
            }
        });
    }

}
