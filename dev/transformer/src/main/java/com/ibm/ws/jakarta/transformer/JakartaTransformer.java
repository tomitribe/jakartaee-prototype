package com.ibm.ws.jakarta.transformer;

import org.eclipse.transformer.Transformer;

public class JakartaTransformer {
    public static void main(String[] args) throws Exception {
        Transformer jTrans = new Transformer(System.out, System.err);
        jTrans.setArgs(args);

        @SuppressWarnings("unused")
        int rc = jTrans.run();
        // System.exit(rc); // TODO: How should this code be returned?
    }
}
