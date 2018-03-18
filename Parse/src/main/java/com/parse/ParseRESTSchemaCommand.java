/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;

import com.parse.http.ParseHttpRequest;
import java.util.Map;

public class ParseRESTSchemaCommand extends ParseRESTCommand {

    public static ParseRESTSchemaCommand getParseSchemasCommand(String sessionToken){
        return new ParseRESTSchemaCommand("schemas/", ParseHttpRequest.Method.GET, null, sessionToken);
    }

    private ParseRESTSchemaCommand(String httpPath, ParseHttpRequest.Method httpMethod, Map<String, ?> parameters, String sessionToken) {
        super(httpPath, httpMethod, parameters, sessionToken);
    }
}