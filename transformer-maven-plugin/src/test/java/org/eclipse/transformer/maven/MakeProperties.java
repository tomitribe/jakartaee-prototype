/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.transformer.maven;

import java.io.IOException;
import java.util.Properties;

public class MakeProperties {

    private static final String[] KEYS = {"Servlet request does not contain X.509 certificates in attribute javax.servlet.request.X509Certificate",
        "( int discriminator, javax.servlet.jsp.JspContext jspContext, javax.servlet.jsp.tagext.JspTag _jspx_parent, int[] _jspx_push_body_count ) {",
        "( javax.servlet.jsp.JspWriter out ) ",
        "(javax.servlet.jsp.tagext.SimpleTag) this ));",
        "(javax.servlet.jsp.tagext.SimpleTag) ",
        "(javax.servlet.jsp.tagext.Tag) ",
        "(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)",
        "(javax.servlet.jsp.tagext.JspFragment ",
        "File ({0}) referenced by javax.servlet.context.tempdir attribute is null, or was is not a directory.  Compression for {1} will be unavailable.",
        "( int discriminator, javax.servlet.jsp.JspContext jspContext, javax.servlet.jsp.tagext.JspTag _jspx_parent, int[] _jspx_push_body_count ) {",
        "( javax.servlet.jsp.JspWriter out ) ",
        "(javax.servlet.jsp.tagext.SimpleTag) this ));",
        "(javax.servlet.jsp.tagext.SimpleTag) ",
        "(javax.servlet.jsp.tagext.Tag) ",
        "(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)",
        "(javax.servlet.jsp.tagext.JspFragment"};

    public static void main(String[] args) throws IOException {
        final Properties p = new Properties();

        for (final String key : KEYS) {
            p.setProperty(key, key.replaceAll("javax", "jakarta"));
        }

        p.store(System.out, null);
    }
}
